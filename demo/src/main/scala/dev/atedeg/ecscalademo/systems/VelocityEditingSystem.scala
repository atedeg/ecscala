package dev.atedeg.ecscalademo.systems

import scala.language.implicitConversions
import dev.atedeg.ecscalademo.given
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.{DeltaTime, EmptySystem, World}
import dev.atedeg.ecscalademo.{MouseState, PlayState, Position, Vector, Velocity, clamped}

class VelocityEditingSystem extends EmptySystem {
  val minVelocityIntensity = 0
  val maxVelocityIntensity = 1000
  val intensityMultiplier = 2

  override def shouldRun = !PlayState.playing && PlayState.selectedBall.isDefined && PlayState.velocityEditingMode
  override def update(deltaTime: DeltaTime, world: World): Unit = {
    if (MouseState.clicked) {
      val selectedBall = PlayState.selectedBall.get
      val selectedBallPosition = selectedBall.getComponent[Position].get
      val newVelocity = MouseState.coordinates - selectedBallPosition
      val newDirection = newVelocity.normalized
      val newIntensity = newVelocity.norm clamped (minVelocityIntensity, maxVelocityIntensity)
      selectedBall.addComponent(Velocity(newDirection * newIntensity * intensityMultiplier))
    }
  }
}
