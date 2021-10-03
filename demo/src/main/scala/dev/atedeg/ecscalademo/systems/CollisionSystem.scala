package dev.atedeg.ecscalademo.systems

import scala.language.implicitConversions
import dev.atedeg.ecscala.{ &:, CNil, Deletable, DeltaTime, Entity, System, View, World }
import dev.atedeg.ecscala
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.util.WritableSpacePartitionContainer
import dev.atedeg.ecscalademo.given
import dev.atedeg.ecscalademo.{ Circle, Mass, PlayState, Point, Position, State, Vector, Velocity }

class CollisionSystem(private val playState: PlayState, private val regions: WritableSpacePartitionContainer)
    extends System {
  override def shouldRun: Boolean = playState.gameState == State.Play

  override def update(deltaTime: DeltaTime, world: World): Unit = {
    for {
      region <- regions.regionsIterator
      candidateColliders <- combinations2(entitiesInNeighborRegions(region))
    } {
      val (candidateAEntity, candidateBEntity) = candidateColliders
      // We are sure we have those components because we checked for them when adding these entities to the space partition container
      var positionA = candidateAEntity.getComponent[Position].get
      val velocityA = candidateAEntity.getComponent[Velocity].get
      val circleA = candidateAEntity.getComponent[Circle].get
      val massA = candidateAEntity.getComponent[Mass].get
      var positionB = candidateBEntity.getComponent[Position].get
      val velocityB = candidateBEntity.getComponent[Velocity].get
      val circleB = candidateBEntity.getComponent[Circle].get
      val massB = candidateBEntity.getComponent[Mass].get
      if (isColliding((positionA, positionB), (circleA.radius, circleB.radius))) {
        if (isStuck((positionA, positionB), (circleA.radius, circleB.radius))) {
          val (newPositionA, newPositionB) = unstuck((positionA, positionB), (circleA.radius, circleB.radius))
          candidateAEntity setComponent newPositionA
          candidateBEntity setComponent newPositionB
          // Update the positions for correctly calculating the collision velocities
          positionA = newPositionA
          positionB = newPositionB
        }
        val (newVelocityA, newVelocityB) =
          newVelocities((positionA, positionB), (velocityA, velocityB), (circleA.radius, circleB.radius))
        candidateAEntity setComponent newVelocityA
        candidateBEntity setComponent newVelocityB
      }
    }
  }

  private def getComponents(entity: Entity): (Position, Velocity, Circle, Mass) =
    (
      entity.getComponent[Position].get,
      entity.getComponent[Velocity].get,
      entity.getComponent[Circle].get,
      entity.getComponent[Mass].get,
    )

  private def isColliding(positions: (Point, Point), radii: (Double, Double)) =
    compareDistances(positions, radii)(_ <= _)

  private def isStuck(positions: (Point, Point), radii: (Double, Double)) =
    compareDistances(positions, radii)(_ - _ < 0.001)

  private def compareDistances(positions: (Point, Point), radii: (Double, Double))(
      comparer: (Double, Double) => Boolean,
  ) = comparer((positions._1 - positions._2).norm, math.pow(radii._1 + radii._2, 1))

  private def unstuck(positions: (Point, Point), radii: (Double, Double)) = {
    val distanceVector = positions._1 - positions._2
    val distanceDirection = distanceVector.normalized
    val moveFactor = radii._1 + radii._2 - distanceVector.norm
    val deltaPosition = distanceDirection * moveFactor / 2
    (Position(positions._1 + deltaPosition), Position(positions._2 - deltaPosition))
  }

  private def newVelocities(positions: (Point, Point), velocities: (Vector, Vector), masses: (Double, Double)) = {
    val (posA, posB) = positions
    val (velA, velB) = velocities
    val (massA, massB) = masses
    val deltaPositions = posA - posB
    val deltaVelocities = velA - velB
    val projectedVelocity = deltaPositions * (deltaVelocities dot deltaPositions) / deltaPositions.squaredNorm
    (
      Velocity(velA - projectedVelocity * (2 * massB / (massA + massB))),
      Velocity(velB + projectedVelocity * (2 * massA / (massA + massB))),
    )
  }

  private def entitiesInNeighborRegions(region: (Int, Int)): Seq[Entity] = for {
    x <- -1 to 0
    y <- -1 to 1
    entity <- regions get (region._1 + x, region._2 + y) if x != 0 || y != 1
  } yield entity

  private def combinations2[T](seq: Seq[T]): Iterator[(T, T)] =
    seq.tails flatMap {
      case h +: t => t.iterator map ((h, _))
      case Nil => Iterator.empty
    }
}
