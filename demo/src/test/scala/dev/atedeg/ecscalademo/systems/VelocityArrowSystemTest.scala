package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.dsl.ECScalaDSL

import scala.language.implicitConversions
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.Inspectors.forAll
import org.scalatestplus.mockito.MockitoSugar.mock
import org.mockito.Mockito.{ times, verify }
import org.mockito.ArgumentMatchers.{ any, anyDouble, eq as is }
import dev.atedeg.ecscalademo.given
import dev.atedeg.ecscalademo.fixtures.VelocityFixture
import dev.atedeg.ecscalademo.{ Color, ECSCanvas, MouseState, PlayState, Point, Position, Velocity }
import dev.atedeg.ecscala.util.types.given

class VelocityArrowSystemTest extends AnyWordSpec with Matchers with ECScalaDSL {

  trait VelocityArrowFixture {
    lazy val world = World()
    lazy val entity1 = world hasAn entity withComponents { Position(0, 0) &: Velocity(10, 10) }
    lazy val canvas = mock[ECSCanvas]
    lazy val arrowSystem = VelocityArrowSystem(canvas)
  }

  "A VelocityArrowSystem" should {
    "run if the game is paused, there is a selected ball and the game is in velocity editing mode" in new VelocityArrowFixture {
      arrowSystem.shouldRun shouldBe false
      PlayState.selectedBall = Some(entity1)
      PlayState.velocityEditingMode = true
      PlayState.playing = false
      arrowSystem.shouldRun shouldBe true
    }
    "draw an arrow in the canvas" in new VelocityArrowFixture {
      PlayState.playing = false
      PlayState.selectedBall = Some(entity1)
      PlayState.velocityEditingMode = true
      MouseState.coordinates = (10.0, 10.0)

      world hasA system(arrowSystem)

      arrowSystem.shouldRun shouldBe true
      world.update(10)
      verify(canvas).drawLine(is(Point(0, 0)), is(Point(10, 10)), any(), anyDouble())
    }
  }
}
