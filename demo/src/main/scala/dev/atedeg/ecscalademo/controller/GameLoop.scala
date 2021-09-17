package dev.atedeg.ecscalademo.controller

import javafx.animation.AnimationTimer as AT
import scalafx.animation.AnimationTimer
import scalafx.beans.property.IntegerProperty

abstract class GameLoop(delegate: AT) extends AnimationTimer(delegate) {
  def fpsProp: IntegerProperty
}

object GameLoop {
  private var delta = 0L
  private var lastFrameTime = 0L
  private val _fpsProp = IntegerProperty(0)
  private var count = 0

  def apply(handler: Long => Unit): GameLoop = new GameLoop(new AT() {
    override def handle(now: Long): Unit = {
      delta = now - lastFrameTime
      count += 1
      lastFrameTime = now
      if (count >= 25) {
        _fpsProp.value = (1e9 / delta).toInt
        count = 0
      }
      handler(delta)
    }
  }) {

    override def fpsProp: IntegerProperty = {
      _fpsProp
    }
  }
}
