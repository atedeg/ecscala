package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.{ ECSCanvas, MouseState, PlayState, ScalaFXCanvas, StartingState, State }
import dev.atedeg.ecscalademo.fixtures.BallCreationRenderingSystemFixture
import dev.atedeg.ecscalademo.util.{ checkAllStates, AnyValue }
import javafx.scene.canvas.Canvas as JfxCanvas
import org.mockito.ArgumentMatchers.{ any, anyDouble }
import org.mockito.Mockito.verify
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import scalafx.scene.canvas.Canvas

class BallCreationRenderingSystemTest extends AnyWordSpec with Matchers with MockitoSugar {

  "A BallCreationRenderingSystem" should {
    "run" when {
      "in an enabled state" in
        checkAllStates(BallCreationRenderingSystem(_, _, mock[StartingState], mock[ECSCanvas]))(
          (State.AddBalls, AnyValue, AnyValue, AnyValue)
        )
    }
  }

  "A RenderingCreationBallSystem" when {
    "enabled" should {
      "render the ball" in new BallCreationRenderingSystemFixture {
        enableSystemCondition(playState)
        world.update(10)
        verify(canvas).drawCircle(any(), anyDouble(), any(), anyDouble())
      }
    }
  }

  private def enableSystemCondition(playState: PlayState): Unit = playState.gameState = State.AddBalls
}
