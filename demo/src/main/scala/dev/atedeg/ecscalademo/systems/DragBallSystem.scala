package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.*
import dev.atedeg.ecscalademo.{ MouseState, PlayState, Position }

class DragBallSystem extends EmptySystem {

  override def shouldRun: Boolean = !PlayState.playing && PlayState.selectedBall.isDefined && MouseState.clicked

  override def update(deltaTime: DeltaTime, world: World): Unit = {
    PlayState.selectedBall match {
      case Some(entity) => entity.addComponent(Position(MouseState.coordinates))
      case _ => ()
    }
  }
}
