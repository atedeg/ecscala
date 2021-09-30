package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.{ DeltaTime, EmptySystem, World }
import dev.atedeg.ecscalademo.{ ECSCanvas, State }

class ClearCanvasSystem(private val ecsCanvas: ECSCanvas) extends EmptySystem {
  override def update(deltaTime: DeltaTime, world: World): Unit = ecsCanvas.clear()
}