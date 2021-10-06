package dev.atedeg.ecscalademo.systems

import scala.language.implicitConversions
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.{ MouseState, PlayState, Point, State, Velocity }
import dev.atedeg.ecscalademo.util.{ checkAllStates, AnyValue }
import dev.atedeg.ecscalademo.fixtures.VelocityFixture

class VelocityEditingSystemTest extends AnyWordSpec with Matchers {

  "A VelocityEditingSystem" should {
    "run" when {
      "in an enabled state" in
        checkAllStates(VelocityEditingSystem(_, _))(
          (State.ChangeVelocity, true, AnyValue, AnyValue),
        )
    }
    "correctly update the velocity" in testNewExpectedVelocity(Point(1, 2), Velocity(2, 4))
    "limit the maximum velocity" in testNewExpectedVelocity(Point(3000, 0), Velocity(2000, 0))
  }

  private def testNewExpectedVelocity(mouseCoordinates: Point, expectedVelocity: Velocity): Unit = new VelocityFixture {
    playState.gameState = State.ChangeVelocity
    playState.selectedBall = Some(entity1)
    mouseState.clicked = true
    mouseState.coordinates = mouseCoordinates
    velocityEditingSystem.shouldRun shouldBe true
    world.update(10)
    entity1.getComponent[Velocity].get shouldBe expectedVelocity
  }
}
