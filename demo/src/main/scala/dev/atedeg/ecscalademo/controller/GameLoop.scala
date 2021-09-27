package dev.atedeg.ecscalademo.controller

import javafx.animation.AnimationTimer as JfxAnimationTimer
import scalafx.animation.AnimationTimer
import scalafx.beans.property.IntegerProperty

trait GameLoop {
  val fps = IntegerProperty(0)
  def start: Unit
  def stop: Unit
}

object GameLoop {
  def apply(handler: Double => Unit): GameLoop = GameLoopImpl(handler)

  private class GameLoopImpl(handler: Double => Unit) extends GameLoop {

    private val animationTimer = new JfxAnimationTimer() {
      private var prevFrameTime = 0L
      private var count = 0

      override def handle(now: Long): Unit = {
        val delta = (now - prevFrameTime) / 1e9
        count += 1
        if (count >= 20) {
          fpsCount(delta)
          count = 0
        }
        handler(delta)
        prevFrameTime = now
      }

      private def fpsCount(delta: Double): Int = {
        val currentFps = (1 / delta).toInt
        fps.value = currentFps
        currentFps
      }
    }

    override def start: Unit = animationTimer.start()

    override def stop: Unit = animationTimer.stop()
  }
}
