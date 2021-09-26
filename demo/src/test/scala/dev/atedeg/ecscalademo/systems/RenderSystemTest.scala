package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.{ Circle, Color, ECSCanvas, MouseState, PlayState, Point, Position }
import dev.atedeg.ecscalademo.fixtures.WorldFixture
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import org.mockito.Mockito.{ verify, when }
import dev.atedeg.ecscala.util.types.given

class RenderSystemTest extends AnyWordSpec with Matchers with ECScalaDSL {

  "A RenderSystem" should {
    "call the drawCircle method with the correct parameters" in new WorldFixture {
      val canvas = mock[ECSCanvas]
      val renderSystem = new RenderSystem(canvas)
      val position = Position(Point(0, 0))
      val circle = Circle(20, Color(255, 0, 0))

      val ball = world hasAn entity withComponents { circle &: position }
      world hasA system(renderSystem)

      world.update(10)
      verify(canvas).drawCircle(position.position, circle.radius, circle.color, 1)

      PlayState.selectedBall = Some(ball)
      world.update(10)

      verify(canvas).drawCircle(position.position, circle.radius, circle.color, 3)

    }
  }
}
