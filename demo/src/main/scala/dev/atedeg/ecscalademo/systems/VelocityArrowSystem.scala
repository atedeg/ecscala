package dev.atedeg.ecscalademo.systems

import scala.language.implicitConversions

import dev.atedeg.ecscala.{ DeltaTime, System, World }
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.given
import dev.atedeg.ecscalademo.{ Color, ECSCanvas, MouseState, PlayState, Position, State }

class VelocityArrowSystem(
    private val playState: PlayState,
    private val mouseState: MouseState,
    private val canvas: ECSCanvas,
) extends System {
  private val arrowColor = Color(255, 0, 0)
  private val arrowWidth = 2

  override def shouldRun = playState.gameState == State.ChangeVelocity

  override def update(deltaTime: DeltaTime, world: World): Unit = {
    val ballPosition = playState.selectedBall.get.getComponent[Position].get
    canvas.drawLine(ballPosition, mouseState.coordinates, arrowColor, arrowWidth)
  }
}
