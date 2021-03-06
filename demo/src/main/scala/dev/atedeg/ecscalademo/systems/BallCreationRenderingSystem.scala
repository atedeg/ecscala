package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.{ DeltaTime, System, World }
import dev.atedeg.ecscalademo.{ ECSCanvas, MouseState, PlayState, StartingState, State }

/**
 * This [[System]] is used to render the ball that is about to be added to the [[World]].
 * @param canvas
 *   the canvas to draw in.
 */
class BallCreationRenderingSystem(
    private val playState: PlayState,
    private val mouseState: MouseState,
    private val startingState: StartingState,
    private val canvas: ECSCanvas,
) extends System {

  override def shouldRun: Boolean = playState.gameState == State.AddBalls

  override def update(deltaTime: DeltaTime, world: World): Unit = {
    canvas.drawCircle(mouseState.coordinates, startingState.startingRadius, startingState.startingColor, 1)
  }
}
