package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.*
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.*
import scalafx.scene.paint.Color as SfxColor

import scala.language.implicitConversions

class BallCreationRenderingSystem(canvas: ECSCanvas) extends EmptySystem {

  given Conversion[Color, SfxColor] with
    def apply(color: Color): SfxColor = SfxColor(color.r, color.g, color.b, 1)

  override def shouldRun: Boolean = !PlayState.playing

  override def update(deltaTime: DeltaTime, world: World): Unit =
    canvas.drawCircle(MouseState.coordinates, StartingState.startingRadius, StartingState.startingColor, 1)
}
