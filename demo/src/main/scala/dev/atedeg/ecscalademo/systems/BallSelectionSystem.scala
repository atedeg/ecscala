package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.*
import dev.atedeg.ecscalademo.{ Circle, MouseState, PlayState, Point, Position, StartingState }

/**
 * This [[System]] is used to identify the selected ball. If a ball were selected, the [[PlayState.selectedBall]]
 * contains the [[Entity]] associated to the selected ball.
 */
class BallSelectionSystem extends EmptySystem {

  override def shouldRun: Boolean = !PlayState.playing && MouseState.down

  override def update(deltaTime: DeltaTime, world: World): Unit = {
    val entitySelected: Option[Entity] = world.getView[Position &: Circle &: CNil] find { e =>
      val Position(point) &: Circle(radius, _) &: CNil = e._2
      isOverlapped(point, radius)
    } map (_._1)

    if (entitySelected.isEmpty) then PlayState.selectedBall = None
    else PlayState.selectedBall = Some(entitySelected.get)
  }

  private def isOverlapped(point: Point, radius: Double): Boolean =
    (MouseState.coordinates - point).norm < (radius + StartingState.startingRadius)
}
