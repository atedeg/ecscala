package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.fixtures.BallCreationRenderingSystemFixture
import dev.atedeg.ecscalademo.{ ECSCanvas, MouseState, PlayState, ScalaFXCanvas, StartingState, State }
import javafx.scene.canvas.Canvas as JfxCanvas
import org.mockito.ArgumentMatchers.{ any, anyDouble }
import org.mockito.Mockito.verify
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import scalafx.scene.canvas.Canvas

class BallCreationRenderingSystemTest extends AnyWordSpec with Matchers with MockitoSugar {

  "A RenderingCreationBallSystem" when {
    "the game is not running" should {
      "be enabled" in new BallCreationRenderingSystemFixture {
        ballCreationRenderingSystem.shouldRun shouldBe false
        enableSystemCondition(playState)
        ballCreationRenderingSystem.shouldRun shouldBe true
      }
      "render the ball" in new BallCreationRenderingSystemFixture {
        enableSystemCondition(playState)
        world.update(10)
        verify(canvas).drawCircle(any(), anyDouble(), any(), anyDouble())
      }
    }
    "the game is running" should {
      "be disabled" in new BallCreationRenderingSystemFixture {
        disableSystemCondition(playState)
        ballCreationRenderingSystem.shouldRun shouldBe false
      }
    }
  }

  private def enableSystemCondition(playState: PlayState): Unit = playState.gameState = State.AddBalls
  private def disableSystemCondition(playState: PlayState): Unit = playState.gameState = State.Pause
}
