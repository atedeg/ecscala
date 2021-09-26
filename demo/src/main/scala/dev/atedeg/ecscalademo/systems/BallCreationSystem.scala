package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.*
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.*

/**
 * This [[System]] is used to add a new ball into the [[World]]. If the mouse pointer is in the area of another ball, no
 * ball will be added.
 */
class BallCreationSystem extends EmptySystem with ECScalaDSL {

  override def shouldRun: Boolean = MouseState.clicked && PlayState.addBallMode

  override def update(deltaTime: DeltaTime, world: World): Unit = {
    val canBeCreated = world.getView[Position &: Circle &: CNil] map (_._2) forall { cl =>
      val Position(point) &: Circle(radius, _) &: CNil = cl
      !isOverlapped(point, radius)
    }
    if (canBeCreated) {
      world hasAn entity withComponents {
        Position(MouseState.coordinates) &:
          Circle(StartingState.startingRadius, StartingState.startingColor) &:
          Velocity(StartingState.startingVelocity) &:
          Mass(StartingState.startingMass)
      }
    }
  }

  private def isOverlapped(point: Point, radius: Double): Boolean =
    (point - MouseState.coordinates).squaredNorm < Math.pow(radius + StartingState.startingRadius, 2)
}
