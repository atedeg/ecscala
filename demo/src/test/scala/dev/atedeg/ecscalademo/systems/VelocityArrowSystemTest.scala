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
import dev.atedeg.ecscalademo.fixtures.{ VelocityArrowSystemFixture, VelocityFixture }
import dev.atedeg.ecscalademo.{ Color, ECSCanvas, MouseState, PlayState, Point, Position, State, Velocity }
import dev.atedeg.ecscalademo.util.{ checkAllStates, AnyValue }
import dev.atedeg.ecscala.util.types.given

class VelocityArrowSystemTest extends AnyWordSpec with Matchers with ECScalaDSL {

  "A VelocityArrowSystem" should {
    "run" when {
      "in an enabled state" in
        checkAllStates(VelocityArrowSystem(_, _, mock[ECSCanvas]))(
          (State.ChangeVelocity, AnyValue, AnyValue, AnyValue),
        )
    }

    "draw an arrow in the canvas" in new VelocityArrowSystemFixture {
      playState.gameState = State.ChangeVelocity
      playState.selectedBall = Some(entity1)
      mouseState.coordinates = (10.0, 10.0)
      world.update(10)
      verify(canvas).drawLine(is(Point(0, 0)), is(Point(10, 10)), any(), anyDouble())
    }
  }
}
