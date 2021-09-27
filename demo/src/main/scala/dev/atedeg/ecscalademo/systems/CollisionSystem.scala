package dev.atedeg.ecscalademo.systems

import scala.language.implicitConversions
import dev.atedeg.ecscala.{ &:, CNil, Deletable, DeltaTime, EmptySystem, Entity, View, World }
import dev.atedeg.ecscala
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.util.WritableSpacePartitionContainer
import dev.atedeg.ecscalademo.given
import dev.atedeg.ecscalademo.{ Circle, Mass, PlayState, Point, Position, Vector, Velocity }

class CollisionSystem(private val regions: WritableSpacePartitionContainer) extends EmptySystem {
  override def shouldRun: Boolean = PlayState.playing

  override def update(deltaTime: DeltaTime, world: World): Unit = {
    for {
      region <- regions.regionsIterator
      candidateColliders <- entitiesInNeighborRegions(region).combinations(2)
    } {
      val Seq(candidateA, candidateB) = candidateColliders
      // We are sure we have those components because we checked for them when adding these entities to the space partition container
      val positionA = candidateA.getComponent[Position].get
      val positionB = candidateB.getComponent[Position].get
      val velocityA = candidateA.getComponent[Velocity].get
      val velocityB = candidateB.getComponent[Velocity].get
      val circleA = candidateA.getComponent[Circle].get
      val circleB = candidateB.getComponent[Circle].get
      val massA = candidateA.getComponent[Mass].get
      val massB = candidateB.getComponent[Mass].get
      if (isColliding((positionA, positionB), (circleA.radius, circleB.radius))) {
        if (isStuck((positionA, positionB), (circleA.radius, circleB.radius))) {
          val (newPositionA, newPositionB) = unstuck((positionA, positionB), (circleA.radius, circleB.radius))
          candidateA addComponent Position(newPositionA)
          candidateB addComponent Position(newPositionB)
        }
        val (newVelocityA, newVelocityB) =
          newVelocities((positionA, positionB), (velocityA, velocityB), (circleA.radius, circleB.radius))
        candidateA addComponent Velocity(newVelocityA)
        candidateB addComponent Velocity(newVelocityB)
      }
    }
  }

  private def isColliding(positions: (Point, Point), radii: (Double, Double)) =
    compareDistances(positions, radii)(_ <= _)

  private def isStuck(positions: (Point, Point), radii: (Double, Double)) =
    compareDistances(positions, radii)(_ < _)

  private def compareDistances(positions: (Point, Point), radii: (Double, Double))(
      comparer: (Double, Double) => Boolean,
  ) = comparer((positions._1 - positions._2).squaredNorm, math.pow(radii._1 + radii._2, 2))

  private def unstuck(positions: (Point, Point), radii: (Double, Double)) = {
    val distanceVector = positions._1 - positions._2
    val distanceDirection = distanceVector.normalized
    val moveFactor = radii._1 + radii._2 - distanceVector.norm
    val deltaPosition = distanceDirection * moveFactor / 2
    (positions._1 + deltaPosition, positions._2 - deltaPosition)
  }

  private def newVelocities(positions: (Point, Point), velocities: (Vector, Vector), masses: (Double, Double)) = {
    val (posA, posB) = positions
    val (velA, velB) = velocities
    val (massA, massB) = masses
    val deltaPositions = posA - posB
    val deltaVelocities = velA - velB
    val projectedVelocity = deltaPositions * (deltaVelocities dot deltaPositions) / deltaPositions.squaredNorm
    (velA - projectedVelocity * (2 * massB / (massA + massB)), velB + projectedVelocity * (2 * massA / (massA + massB)))
  }

  private def entitiesInNeighborRegions(region: (Int, Int)): Seq[Entity] = for {
    x <- -1 to 0
    y <- -1 to 1
    entity <- regions get (region._1 + x, region._2 + y) if x != 0 || y != 1
  } yield entity
}
