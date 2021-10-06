package dev.atedeg.ecscalademo.fixtures

import dev.atedeg.ecscala.{ World, given }
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.{ MouseState, PlayState, Position }
import dev.atedeg.ecscalademo.systems.DragBallSystem

trait DragBallSystemFixture extends ECScalaDSL with WorldFixture with WorldStateFixture {
  val entity1 = world hasAn entity withComponent Position(0, 0)
  val dragBallSystem = DragBallSystem(playState, mouseState)
  world hasA system(dragBallSystem)
}
