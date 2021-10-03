package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.*
import dev.atedeg.ecscalademo.{
  given_Conversion_Position_Point,
  isOverlappedWith,
  Circle,
  MouseState,
  PlayState,
  Position,
  StartingState,
  State,
}
import dev.atedeg.ecscala.util.types.given

/**
 * This [[System]] is used to update the selected ball's [[Position]] according to the mouse pointer.
 */
class DragBallSystem(private val playState: PlayState, private val mouseState: MouseState) extends EmptySystem {

  override def shouldRun: Boolean = playState.gameState == State.Dragging

  override def update(deltaTime: DeltaTime, world: World): Unit = {
    playState.selectedBall match {
      case Some(entity) => entity.setComponent(Position(mouseState.coordinates))
      case _ => ()
    }
  }
}
