package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.dsl.ECScalaDSL

import scala.language.implicitConversions
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.Inspectors.forAll
import org.scalatestplus.mockito.MockitoSugar.mock
import org.mockito.Mockito.{times, verify}
import org.mockito.ArgumentMatchers.{any, anyDouble, eq as is}
import dev.atedeg.ecscalademo.given
import dev.atedeg.ecscalademo.fixtures.VelocityFixture
import dev.atedeg.ecscalademo.{Color, ECSCanvas, MouseState, PlayState, Point}
import dev.atedeg.ecscala.util.types.given

class VelocityArrowSystemTest extends AnyWordSpec with Matchers with ECScalaDSL {
  "A VelocityArrowSystem" should {
    "run if the game is paused, there is a selected ball and the game is in velocity editing mode" in new VelocityFixture {
      val canvas = mock[ECSCanvas]
      val arrowSystem = VelocityArrowSystem(canvas)
      arrowSystem.shouldRun shouldBe false
      PlayState.selectedBall = Some(entity1)
      PlayState.velocityEditingMode = true
      PlayState.playing = false
      arrowSystem.shouldRun shouldBe true
    }
    "draw an arrow in the canvas" in new VelocityFixture {
      val canvas = mock[ECSCanvas]
      val arrowSystem = VelocityArrowSystem(canvas)
      world hasA system(arrowSystem)
      PlayState.selectedBall = Some(entity1)
      PlayState.playing = false
      MouseState.coordinates = (10.0, 10.0)
      world.update(10)
      verify(canvas, times(1)).drawLine(is(Point(0, 0)), is(Point(10, 10)), any(), anyDouble())
    }
  }
}
