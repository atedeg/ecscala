package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.dsl.ECScalaDSL

import scala.language.implicitConversions
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.given
import dev.atedeg.ecscalademo.fixtures.VelocityFixture
import dev.atedeg.ecscalademo.{ MouseState, PlayState, Point, State, Velocity }
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class VelocityEditingSystemTest extends AnyWordSpec with Matchers with ECScalaDSL {

  "A VelocityEditingSystem" should {
    "run when the simulation is not running, there is a selected ball and the game is in velocity editing mode" in new VelocityFixture {
      system.shouldRun shouldBe false
      playState.gameState = State.ChangeVelocity
      playState.selectedBall = Some(entity1)
      system.shouldRun shouldBe true
    }
    "correctly update the velocity" in new VelocityFixture {
      testNewExpectedVelocity(playState, mouseState, Point(1, 2), Velocity(2, 4))
    }
    "limit the maximum velocity" in new VelocityFixture {
      testNewExpectedVelocity(playState, mouseState, Point(3000, 0), Velocity(2000, 0))
    }
  }

  private def testNewExpectedVelocity(
      pState: PlayState,
      mState: MouseState,
      mouseCoordinates: Point,
      expectedVelocity: Velocity,
  ): Unit = new VelocityFixture {
    pState.gameState = State.ChangeVelocity
    pState.selectedBall = Some(entity1)
    mState.clicked = true
    mState.coordinates = mouseCoordinates
    world.update(10)
    entity1.getComponent[Velocity].get shouldBe expectedVelocity
  }
}
