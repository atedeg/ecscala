package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.Deleted.entity
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscala.{Entity, World}
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.fixtures.DragBallSystemFixture
import dev.atedeg.ecscalademo.util.{AnyValue, checkAllStates, WritableSpacePartitionContainer}
import dev.atedeg.ecscalademo.{MouseState, PlayState, Point, Position, State}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.language.implicitConversions

class DragBallSystemTest extends AnyWordSpec with Matchers with ECScalaDSL {
  "A DragBallSystem" should {
    "run" when {
      "in an enabled state" in
        checkAllStates(DragBallSystem(_, _))(
          (State.Dragging, AnyValue, AnyValue, AnyValue),
        )
    }
  }

  "A DragBallSystem" when {
    "the game is in drag mode" should {
      "update the selectes entity's position" in new DragBallSystemFixture {
        playState.gameState = State.Dragging
        playState.selectedBall = Some(entity1)
        mouseState.coordinates = Point(10.0, 10.0)

        world.update(10)

        entity1.getComponent[Position] match {
          case Some(position) => position
          case _ => fail("A component should be defined")
        } shouldBe Position(10.0, 10.0)
      }
    }
  }
}
