package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.dsl.ECScalaDSL

import scala.language.implicitConversions
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.given
import dev.atedeg.ecscalademo.fixtures.VelocityFixture
import dev.atedeg.ecscalademo.{ MouseState, PlayState, Point, Velocity }
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class VelocityEditingSystemTest extends AnyWordSpec with Matchers with ECScalaDSL {

  "A VelocityEditingSystem" should {
    "run when the simulation is not running, there is a selected ball and the game is in velocity editing mode" in new VelocityFixture {
      system.shouldRun shouldBe false
      PlayState.playing = false
      PlayState.velocityEditingMode = true
      PlayState.selectedBall = Some(entity1)
      system.shouldRun shouldBe true
    }
    "correctly update the velocity" in testNewExpectedVelocity(Point(1, 2), Velocity(2, 4))
    "limit the maximum velocity" in testNewExpectedVelocity(Point(3000, 0), Velocity(2000, 0))
  }

  private def testNewExpectedVelocity(mouseCoordinates: Point, expectedVelocity: Velocity): Unit = new VelocityFixture {
    PlayState.playing = false
    PlayState.selectedBall = Some(entity1)
    MouseState.clicked = true
    PlayState.velocityEditingMode = true
    MouseState.coordinates = mouseCoordinates
    world.update(10)
    entity1.getComponent[Velocity].get shouldBe expectedVelocity
  }
}
