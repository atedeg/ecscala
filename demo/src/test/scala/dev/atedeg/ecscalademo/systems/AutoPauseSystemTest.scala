package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.dsl.ECScalaDSL
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscalademo.{ State, Velocity }
import dev.atedeg.ecscalademo.fixtures.AutoPauseSystemFixture
import dev.atedeg.ecscalademo.util.{ checkAllStates, AnyValue }

class AutoPauseSystemTest extends AnyWordSpec with Matchers with ECScalaDSL {

  "An AutoPauseSystem" should {
    "run" when {
      "in an enabled state" in {
        checkAllStates((playState, _) => AutoPauseSystem(playState))(
          (State.Play, AnyValue, AnyValue, AnyValue),
        )
      }
    }
    "pause the game" when {
      "the energy of the system is 0" in new AutoPauseSystemFixture {
        playState.gameState = State.Play
        world hasAn entity withComponent Velocity(0.0, 0.0)
        world hasAn entity withComponent Velocity(0.0, 0.0)
        world.update(10)
        playState.gameState shouldBe State.Pause
      }
    }
    "do nothing" when {
      "the energy of the system is not 0" in new AutoPauseSystemFixture {
        playState.gameState = State.Play
        world hasAn entity withComponent Velocity(10.0, 10.0)
        world hasAn entity withComponent Velocity(0.0, 0.0)
        world.update(10)
        playState.gameState shouldBe State.Play
      }
    }
  }
}
