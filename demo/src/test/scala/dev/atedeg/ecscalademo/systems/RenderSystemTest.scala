package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.{ Circle, Color, ECSCanvas, MouseState, PlayState, Point, Position }
import dev.atedeg.ecscalademo.fixtures.{ RenderSystemFixture, WorldFixture }
import dev.atedeg.ecscalademo.util.{ AnyValue, checkAllStates }
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import org.mockito.Mockito.{ verify, when }

class RenderSystemTest extends AnyWordSpec with Matchers with ECScalaDSL {

  "A RenderSystem" should {
    "always run" in {
      checkAllStates((playState, _) => RenderSystem(playState, mock[ECSCanvas]))(
        (AnyValue, AnyValue, AnyValue, AnyValue)
      )
    }

    "call the drawCircle method with the correct parameters" in new RenderSystemFixture {
      world.update(10)
      verify(canvas).drawCircle(position.position, circle.radius, circle.color, 1)
      playState.selectedBall = Some(ball)
      world.update(10)
      verify(canvas).drawCircle(position.position, circle.radius, circle.color, 3)
    }
  }
}
