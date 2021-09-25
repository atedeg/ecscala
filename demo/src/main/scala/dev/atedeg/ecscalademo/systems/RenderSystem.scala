package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.*
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.{ Circle, Color, ECSCanvas, PlayState, Point, Position }
import dev.atedeg.ecscalademo.Vector

/**
 * The [[System]] that renders the balls on their updated Positions.
 * @param ecsCanvas
 */
class RenderSystem(ecsCanvas: ECSCanvas) extends System[Circle &: Position &: CNil] {

  override def before(deltaTime: DeltaTime, world: World, view: View[Circle &: Position &: CNil]): Unit =
    ecsCanvas.clear()

  override def update(entity: Entity, components: Circle &: Position &: CNil)(
      deltaTime: DeltaTime,
      world: World,
      view: View[Circle &: Position &: CNil],
  ): Deletable[Circle &: Position &: CNil] = {
    val lineWidth = PlayState.selectedBall match { case Some(`entity`) => 3; case _ => 1 }
    val Circle(radius, color) &: Position(point) &: CNil = components
    ecsCanvas.drawCircle(point, radius, color, lineWidth)
    components
  }
}
