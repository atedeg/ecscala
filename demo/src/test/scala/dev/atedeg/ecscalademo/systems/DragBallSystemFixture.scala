package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.Entity
import dev.atedeg.ecscalademo.fixture.WorldFixture

trait DragBallSystemFixture extends WorldFixture {
  lazy val dragBallSystem: DragBallSystem = DragBallSystem()
}
