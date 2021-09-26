package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.Entity
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.given
import dev.atedeg.ecscalademo.fixtures.WorldFixture
import dev.atedeg.ecscalademo.{ MouseState, PlayState, Point, Position }
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.language.implicitConversions

class DragBallSystemTest extends AnyWordSpec with Matchers {

  trait DragBallSystemFixture extends WorldFixture {
    lazy val dragBallSystem: DragBallSystem = DragBallSystem()
  }

  "A DragBallSystem" when {
    "the game is running and the mouse is clicked" should {
      "not run" in new DragBallSystemFixture {
        PlayState.playing = true
        MouseState.down = true
        MouseState.clicked = true
        dragBallSystem.shouldRun shouldBe false

      }
    }
    "the game is running and the mouse is not clicked" should {
      "not run" in new DragBallSystemFixture {
        PlayState.playing = true
        MouseState.up = true
        MouseState.clicked = false
        dragBallSystem.shouldRun shouldBe false
      }
    }
    "the game is not running and the mouse is clicked" should {
      "run" in new DragBallSystemFixture {
        PlayState.playing = false
        PlayState.selectedBall = Some(entity)
        MouseState.down = true
        MouseState.clicked = true
        dragBallSystem.shouldRun shouldBe true
      }
      "update the selectes entity's position" in new DragBallSystemFixture {
        entity.addComponent(Position(0.0, 0.0))

        PlayState.selectedBall = Some(entity)
        MouseState.coordinates = Point(10.0, 10.0)

        world.addSystem(dragBallSystem)
        world.update(10)

        entity.getComponent[Position] match {
          case Some(position) => position
          case _ => fail("A component should been defined")
        } shouldBe Position(10.0, 10.0)
      }
    }
  }
}
