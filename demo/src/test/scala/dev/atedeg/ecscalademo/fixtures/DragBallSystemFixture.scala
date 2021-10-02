package dev.atedeg.ecscalademo.fixtures

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.{ MouseState, PlayState }
import dev.atedeg.ecscalademo.systems.DragBallSystem

trait DragBallSystemFixture extends ECScalaDSL with WorldFixture with WorldStateFixture {
  val entity1 = world hasAn entity
  val dragBallSystem = new DragBallSystem(playState, mouseState)
  world hasA system(dragBallSystem)
}
