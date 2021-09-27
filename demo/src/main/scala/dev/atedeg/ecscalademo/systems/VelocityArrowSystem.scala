package dev.atedeg.ecscalademo.systems

import scala.language.implicitConversions

import dev.atedeg.ecscala.{DeltaTime, EmptySystem, World}
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.given
import dev.atedeg.ecscalademo.{ECSCanvas, MouseState, PlayState, Color, Position}

class VelocityArrowSystem(canvas: ECSCanvas) extends EmptySystem {
  private val arrowColor = Color(255, 0, 0)
  private val arrowWidth = 2

  override def shouldRun = !PlayState.playing && PlayState.selectedBall.isDefined && PlayState.velocityEditingMode
  override def update(deltaTime: DeltaTime, world: World): Unit = {
    val ballPosition = PlayState.selectedBall.get.getComponent[Position].get
    canvas.drawLine(ballPosition, MouseState.coordinates, arrowColor, arrowWidth)
  }
}
