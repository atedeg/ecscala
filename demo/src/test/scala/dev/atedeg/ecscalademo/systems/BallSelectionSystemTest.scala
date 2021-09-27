package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.given
import dev.atedeg.ecscalademo.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.language.implicitConversions

class BallSelectionSystemTest extends AnyWordSpec with Matchers with ECScalaDSL {

  trait BallSelectionSystemFixture {
    val world = World()
    lazy val system = BallSelectionSystem()
  }

  "A BallSelectionSystem" when {
    "the game is paused and the mouse is clicked" should {
      "be enabled" in new BallSelectionSystemFixture {
        enableSystemCondition()
        system.shouldRun shouldBe true
      }
    }
    "the game is paused and the mouse is not clicked" should {
      "not be enabled" in new BallSelectionSystemFixture {
        disableSystemCondition()
        system.shouldRun shouldBe false
      }
    }
    "the game is running" should {
      "not be enabled" in new BallSelectionSystemFixture {
        PlayState.playing = true
        system.shouldRun shouldBe false
      }
    }
    "a ball is selected" should {
      "set the ball as currently selected" in new BallSelectionSystemFixture {
        enableSystemCondition()
        PlayState.selectedBall = None

        val entity1 = world hasAn entity withComponents {
          Position(10.0, 10.0) &: Circle(20, StartingState.startingColor)
        }
        val entity2 = world hasAn entity withComponents {
          Position(70.0, 70.0) &: Circle(20, StartingState.startingColor)
        }

        world.addSystem(system)

        PlayState.selectedBall shouldBe None
        MouseState.coordinates = Point(10.0, 10.0)
        world.update(10)

        PlayState.selectedBall shouldBe Some(entity1)

        MouseState.coordinates = Point(65.0, 65.0)
        world.update(10)

        PlayState.selectedBall shouldBe Some(entity2)
      }
    }
  }

  private def enableSystemCondition(): Unit = {
    PlayState.playing = false
    MouseState.down = true
  }

  private def disableSystemCondition(): Unit = {
    PlayState.playing = false
    MouseState.down = false
  }
}
