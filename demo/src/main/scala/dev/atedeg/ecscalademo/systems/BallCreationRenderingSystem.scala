package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.*
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.*
import scalafx.scene.paint.Color as SfxColor

import scala.language.implicitConversions

/**
 * This [[System]] is used to render the ball that is about to be added to the [[World]].
 * @param canvas
 *   the canvas to draw in.
 */
class BallCreationRenderingSystem(private val canvas: ECSCanvas) extends EmptySystem {

  override def shouldRun: Boolean = !PlayState.playing && PlayState.addBallMode

  override def update(deltaTime: DeltaTime, world: World): Unit =
    canvas.drawCircle(MouseState.coordinates, StartingState.startingRadius, StartingState.startingColor, 1)
}
