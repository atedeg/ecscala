package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.Deleted.entity
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscala.{ Entity, World }
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.{ MouseState, PlayState, Point, Position }
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.language.implicitConversions

class DragBallSystemTest extends AnyWordSpec with Matchers with ECScalaDSL {

  trait DragBallSystemFixture {
    val world = World()
    val entity1 = world hasAn entity
    lazy val dragBallSystem: DragBallSystem = DragBallSystem()
  }

  "A DragBallSystem" when {
    "the change velocity is set" should {
      "not run" in new DragBallSystemFixture {
        PlayState.selectedBall = Some(entity1)
        PlayState.playing = false
        MouseState.down = true
        MouseState.clicked = true
        PlayState.velocityChainging = true
        dragBallSystem.shouldRun shouldBe false
      }
    }
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
        PlayState.velocityChainging = false
        PlayState.playing = false
        PlayState.selectedBall = Some(entity1)
        MouseState.down = true
        MouseState.clicked = true
        dragBallSystem.shouldRun shouldBe true
      }
      "update the selectes entity's position" in new DragBallSystemFixture {
        entity1 withComponent Position(0.0, 0.0)

        PlayState.velocityChainging = false
        PlayState.selectedBall = Some(entity1)
        MouseState.coordinates = Point(10.0, 10.0)

        world.addSystem(dragBallSystem)
        world.update(10)

        entity1.getComponent[Position] match {
          case Some(position) => position
          case _ => fail("A component should been defined")
        } shouldBe Position(10.0, 10.0)
      }
    }
  }
}
