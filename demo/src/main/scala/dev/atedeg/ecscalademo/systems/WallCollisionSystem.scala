package dev.atedeg.ecscalademo.systems

import scala.language.implicitConversions
import dev.atedeg.ecscala.{ &:, CNil, Deletable, DeltaTime, Entity, System, View, World }
import dev.atedeg.ecscala
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.{
  clamped,
  Circle,
  ECSCanvas,
  EnvironmentState,
  PlayState,
  Point,
  Position,
  Vector,
  Velocity,
}
import dev.atedeg.ecscalademo.util.WritableSpacePartitionContainer
import dev.atedeg.ecscalademo
import dev.atedeg.ecscalademo.given

/**
 * This system handles the collisions of balls on the walls. It should be run after the [[RegionAssignmentSystem]].
 * @param canvas
 *   the [[SpacePartitionContainer]] that will be read.
 */
class WallCollisionSystem(private val canvas: ECSCanvas) extends System[Position &: Velocity &: Circle &: CNil] {

  override def shouldRun: Boolean = PlayState.playing

  override def update(entity: Entity, components: Position &: Velocity &: Circle &: CNil)(
      deltaTime: DeltaTime,
      world: World,
      view: View[Position &: Velocity &: Circle &: CNil],
  ): Deletable[Position &: Velocity &: Circle &: CNil] = {
    val Position(x, y) &: Velocity(vx, vy) &: Circle(radius, color) &: CNil = components
    val collidesLeft = x < radius
    val collidesRight = x > canvas.width - radius
    val collidesTop = y < radius
    val collidesBottom = y > canvas.height - radius
    lazy val mirroredHorizontalVelocity = -vx * EnvironmentState.wallRestitution
    lazy val mirroredVerticalVelocity = -vy * EnvironmentState.wallRestitution
    val newVelocity = Velocity(
      if (collidesLeft) {
        if vx < 0 then mirroredHorizontalVelocity else vx
      } else if (collidesRight) {
        if vx > 0 then mirroredHorizontalVelocity else vx
      } else {
        vx
      },
      if (collidesTop) {
        if vy < 0 then mirroredVerticalVelocity else vy
      } else if (collidesBottom) {
        if vy > 0 then mirroredHorizontalVelocity else vy
      } else {
        vy
      },
    )
    val newPosition = Position(
      x clamped (radius, canvas.width - radius),
      y clamped (radius, canvas.height - radius),
    )
    newPosition &: newVelocity &: Circle(radius, color) &: CNil
  }
}
