package dev.atedeg.ecscalademo.systems

import scala.language.implicitConversions
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.{ Circle, MouseState, PlayState, Point, Position, State }
import dev.atedeg.ecscalademo.fixtures.BallSelectionSystemFixture
import dev.atedeg.ecscalademo.util.{ checkAllStates, AnyValue }

class BallSelectionSystemTest extends AnyWordSpec with Matchers with ECScalaDSL {

  "A BallSelectionSystem" should {
    "run" when {
      "in an enabled state" in
        checkAllStates(BallSelectionSystem(_, _))(
          (State.Pause, AnyValue, true, AnyValue),
          (State.SelectBall, AnyValue, true, AnyValue),
        )
    }
  }

  "A BallSelectionSystem" when {
    "a ball is selected" should {
      "set the ball as currently selected" in new BallSelectionSystemFixture {
        enableSystemCondition(playState, mouseState)
        playState.selectedBall = None

        val entity1 = world hasAn entity withComponents {
          Position(10.0, 10.0) &: Circle(startingState.startingRadius, startingState.startingColor)
        }
        val entity2 = world hasAn entity withComponents {
          Position(70.0, 70.0) &: Circle(startingState.startingRadius, startingState.startingColor)
        }

        mouseState.coordinates = Point(10.0, 10.0)
        world.update(10)
        playState.selectedBall shouldBe Some(entity1)

        mouseState.coordinates = Point(65.0, 65.0)
        world.update(10)
        playState.selectedBall shouldBe Some(entity2)
      }
    }
  }

  private def enableSystemCondition(playState: PlayState, mouseState: MouseState): Unit = {
    playState.gameState = State.SelectBall
    mouseState.down = true
  }

  private def disableSystemCondition(playState: PlayState, mouseState: MouseState): Unit = {
    playState.gameState = State.Pause
    mouseState.down = false
  }
}
