package dev.atedeg.ecscalademo.systems

import scala.language.implicitConversions
import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.{ DeltaTime, System, World }
import dev.atedeg.ecscalademo.given
import dev.atedeg.ecscalademo.{ clamped, MouseState, PlayState, Position, State, Vector, Velocity }

class VelocityEditingSystem(private val playState: PlayState, private val mouseState: MouseState) extends System {
  val minVelocityIntensity = 0
  val maxVelocityIntensity = 1000
  val intensityMultiplier = 2

  override def shouldRun = playState.gameState == State.ChangeVelocity && mouseState.clicked

  override def update(deltaTime: DeltaTime, world: World): Unit = {
    val selectedBall = playState.selectedBall.get
    val selectedBallPosition = selectedBall.getComponent[Position].get
    val newVelocity = mouseState.coordinates - selectedBallPosition
    val newDirection = newVelocity.normalized
    val newIntensity = newVelocity.norm clamped (minVelocityIntensity, maxVelocityIntensity)
    selectedBall setComponent Velocity(newDirection * newIntensity * intensityMultiplier)
    playState.gameState = State.Pause
  }
}
