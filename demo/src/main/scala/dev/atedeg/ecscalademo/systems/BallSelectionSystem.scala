package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.{ System, DeltaTime, World, Entity, &:, CNil }
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.{ PlayState, MouseState, State, Position, Circle, isOverlappedWith }

/**
 * This [[System]] is used to identify the selected ball. If a ball were selected, the [[PlayState.selectedBall]]
 * contains the [[Entity]] associated to the selected ball.
 */
class BallSelectionSystem(private val playState: PlayState, private val mouseState: MouseState) extends System {

  override def shouldRun: Boolean =
    (playState.gameState == State.Pause || playState.gameState == State.SelectBall) && mouseState.down

  override def update(deltaTime: DeltaTime, world: World): Unit = {
    val selectedEntity: Option[Entity] = world.getView[Position &: Circle &: CNil] find { e =>
      val Position(point) &: Circle(radius, _) &: CNil = e._2
      mouseState.coordinates.isOverlappedWith(point, 0, radius)
    } map (_._1)

    if (selectedEntity.isEmpty) {
      playState.selectedBall = None
      playState.gameState = State.Pause
    } else {
      playState.selectedBall = Some(selectedEntity.get)
      playState.gameState = State.SelectBall
    }
  }
}
