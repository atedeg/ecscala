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
    val position &: velocity &: circle &: CNil = components
    val radius = circle.radius
    val collidesLeftX = position.x < radius
    val collidesRightX = position.x > canvas.width - radius
    val collidesTopY = position.y < radius
    val collidesBottomY = position.y > canvas.height - radius
    lazy val mirroredHorizontalVelocity = -velocity.x * EnvironmentState.wallRestitution
    lazy val mirroredVerticalVelocity = -velocity.y * EnvironmentState.wallRestitution
    val newVelocity = Velocity(
      if (collidesLeftX) {
        if velocity.x < 0 then mirroredHorizontalVelocity else velocity.x
      } else if (collidesRightX) {
        if velocity.x > 0 then mirroredHorizontalVelocity else velocity.x
      } else {
        velocity.x
      },
      if (collidesTopY) {
        if velocity.y < 0 then mirroredVerticalVelocity else velocity.y
      } else if (collidesBottomY) {
        if velocity.y > 0 then mirroredHorizontalVelocity else velocity.y
      } else {
        velocity.y
      },
    )
    val newPosition = Position(
      position.x clamped (radius, canvas.width - radius),
      position.y clamped (radius, canvas.height - radius),
    )
    newPosition &: newVelocity &: circle &: CNil
  }
}
