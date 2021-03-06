package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.{ DeltaTime, System, World }
import dev.atedeg.ecscalademo.given
import dev.atedeg.ecscalademo.{ MouseState, PlayState, Position, State }

/**
 * This [[System]] is used to update the selected ball's [[Position]] according to the mouse pointer.
 */
class DragBallSystem(private val playState: PlayState, private val mouseState: MouseState) extends System {

  override def shouldRun: Boolean = playState.gameState == State.Dragging

  override def update(deltaTime: DeltaTime, world: World): Unit = {
    playState.selectedBall match {
      case Some(entity) => entity setComponent Position(mouseState.coordinates)
      case _ => ()
    }
  }
}
