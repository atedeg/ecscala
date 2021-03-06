package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.{ &:, CNil, Deletable, DeltaTime, Entity, IteratingSystem, View, World }
import dev.atedeg.ecscalademo.{ Circle, ECSCanvas, PlayState, Position }

/**
 * The [[System]] that renders the balls on their updated Positions.
 * @param ecsCanvas
 */
class RenderSystem(private val playState: PlayState, private val ecsCanvas: ECSCanvas)
    extends IteratingSystem[Circle &: Position &: CNil] {
  private val selectedBallLineWidth = 3
  private val regularBallLineWidth = 1

  override def update(entity: Entity, components: Circle &: Position &: CNil)(
      deltaTime: DeltaTime,
      world: World,
      view: View[Circle &: Position &: CNil],
  ): Deletable[Circle &: Position &: CNil] = {
    val lineWidth = playState.selectedBall match {
      case Some(`entity`) => selectedBallLineWidth; case _ => regularBallLineWidth
    }
    val Circle(radius, color) &: Position(point) &: CNil = components
    ecsCanvas.drawCircle(point, radius, color, lineWidth)
    components
  }
}
