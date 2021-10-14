package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.{ DeltaTime, System, World }
import dev.atedeg.ecscalademo.ECSCanvas

class ClearCanvasSystem(private val ecsCanvas: ECSCanvas) extends System {
  override def update(deltaTime: DeltaTime, world: World): Unit = ecsCanvas.clear()
}
