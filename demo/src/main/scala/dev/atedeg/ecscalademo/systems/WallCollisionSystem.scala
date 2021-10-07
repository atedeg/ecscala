package dev.atedeg.ecscalademo.systems

import scala.language.implicitConversions
import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.{ &:, CNil, Deletable, DeltaTime, Entity, IteratingSystem, View, World }
import dev.atedeg.ecscalademo.given
import dev.atedeg.ecscalademo.{
  clamped,
  Circle,
  ECSCanvas,
  EnvironmentState,
  PlayState,
  Point,
  Position,
  State,
  Vector,
  Velocity,
}
import dev.atedeg.ecscalademo.util.WritableSpacePartitionContainer

/**
 * This system handles the collisions of balls on the walls. It should be run after the [[RegionAssignmentSystem]].
 * @param canvas
 *   the [[SpacePartitionContainer]] that will be read.
 */
class WallCollisionSystem(
    private val playState: PlayState,
    private val environmentState: EnvironmentState,
    private val canvas: ECSCanvas,
) extends IteratingSystem[Position &: Velocity &: Circle &: CNil] {

  override def shouldRun: Boolean = playState.gameState == State.Play

  override def update(entity: Entity, components: Position &: Velocity &: Circle &: CNil)(
      deltaTime: DeltaTime,
      world: World,
      view: View[Position &: Velocity &: Circle &: CNil],
  ): Deletable[Position &: Velocity &: Circle &: CNil] = {
    val Position(Point(x, y)) &: Velocity(Vector(vx, vy)) &: Circle(radius, color) &: CNil = components
    val collidesLeft = x < radius
    val collidesRight = x > canvas.width - radius
    val collidesTop = y < radius
    val collidesBottom = y > canvas.height - radius
    lazy val mirroredHorizontalVelocity = -vx * environmentState.wallRestitution
    lazy val mirroredVerticalVelocity = -vy * environmentState.wallRestitution
    val newVelocity = Velocity(
      if (collidesLeft && vx < 0) || (collidesRight && vx > 0) then mirroredHorizontalVelocity else vx,
      if (collidesTop && vy < 0) || (collidesBottom && vy > 0) then mirroredVerticalVelocity else vy,
    )
    val newPosition = Position(
      x clamped (radius, canvas.width - radius),
      y clamped (radius, canvas.height - radius),
    )
    newPosition &: newVelocity &: Circle(radius, color) &: CNil
  }
}
