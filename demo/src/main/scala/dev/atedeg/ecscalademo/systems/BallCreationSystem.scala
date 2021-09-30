package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.*
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.*

/**
 * This [[System]] is used to add a new ball into the [[World]]. If the mouse pointer is in the area of another ball, no
 * ball will be added.
 */
class BallCreationSystem(
    private val playState: PlayState,
    private val mouseState: MouseState,
    private val startingState: StartingState,
) extends EmptySystem
    with ECScalaDSL {

  override def shouldRun: Boolean = mouseState.clicked && playState.gameState == State.AddBalls

  override def update(deltaTime: DeltaTime, world: World): Unit = {
    val canBeCreated = world.getView[Position &: Circle &: CNil] map (_._2) forall { cl =>
      val Position(point) &: Circle(radius, _) &: CNil = cl
      !point.isOverlappedWith(mouseState.coordinates, radius, startingState.startingRadius)
    }
    if (canBeCreated) {
      world hasAn entity withComponents {
        Position(mouseState.coordinates) &:
          Circle(startingState.startingRadius, startingState.startingColor) &:
          Velocity(startingState.startingVelocity) &:
          Mass(startingState.startingMass)
      }
    }
  }
}
