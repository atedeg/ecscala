package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.Deleted.entity
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscala.{ Entity, World }
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.{ MouseState, PlayState, Point, Position, State }
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.language.implicitConversions

class DragBallSystemTest extends AnyWordSpec with Matchers with ECScalaDSL {

  trait DragBallSystemFixture {
    val world = World()
    val entity1 = world hasAn entity
    val playState = PlayState()
    val mouseState = MouseState()
    lazy val dragBallSystem: DragBallSystem = new DragBallSystem(playState, mouseState)
  }

  "A DragBallSystem" when {
    "the change velocity is set" should {
      "not run" in new DragBallSystemFixture {
        playState.selectedBall = Some(entity1)
        playState.gameState = State.ChangeVelocity
        mouseState.down = true
        mouseState.clicked = true
        dragBallSystem.shouldRun shouldBe false
      }
    }
    "the game is running and the mouse is clicked" should {
      "not run" in new DragBallSystemFixture {
        playState.gameState = State.Play
        mouseState.down = true
        mouseState.clicked = true
        dragBallSystem.shouldRun shouldBe false

      }
    }
    "the game is running and the mouse is not clicked" should {
      "not run" in new DragBallSystemFixture {
        playState.gameState = State.Play
        mouseState.up = true
        mouseState.clicked = false
        dragBallSystem.shouldRun shouldBe false
      }
    }
    "the game is not running and the mouse is clicked" should {
      "run" in new DragBallSystemFixture {
        playState.gameState = State.Dragging
        playState.selectedBall = Some(entity1)
        mouseState.clicked = true
        dragBallSystem.shouldRun shouldBe true
      }
      "update the selectes entity's position" in new DragBallSystemFixture {
        entity1 withComponent Position(0.0, 0.0)

        playState.gameState = State.Dragging
        playState.selectedBall = Some(entity1)
        mouseState.coordinates = Point(10.0, 10.0)

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
