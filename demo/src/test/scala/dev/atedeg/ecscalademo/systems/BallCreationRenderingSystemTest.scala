package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.{ ECSCanvas, MouseState, PlayState, ScalaFXCanvas, State }
import javafx.scene.canvas.Canvas as JfxCanvas
import org.mockito.ArgumentMatchers.{ any, anyDouble }
import org.mockito.Mockito.verify
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import scalafx.scene.canvas.Canvas

class BallCreationRenderingSystemTest extends AnyWordSpec with Matchers with MockitoSugar {

  trait BallCreationRenderingSystemFixture {
    val world = World()
    val canvasMock = mock[ECSCanvas]
    val playState = PlayState()
    val mouseState = MouseState()
    lazy val system = new BallCreationRenderingSystem(playState, mouseState, canvasMock)
  }

  "A RenderingCreationBallSystem" when {
    "the game is not running" should {
      "be enabled" in new BallCreationRenderingSystemFixture {
        system.shouldRun shouldBe false
        enableSystemCondition(playState)
        system.shouldRun shouldBe true
      }
      "render the ball" in new BallCreationRenderingSystemFixture {
        enableSystemCondition(playState)
        world.addSystem(system)
        world.update(10)
        verify(canvasMock).drawCircle(any(), anyDouble(), any(), anyDouble())
      }
    }
    "the game is running" should {
      "be disabled" in new BallCreationRenderingSystemFixture {
        disableSystemCondition(playState)
        system.shouldRun shouldBe false
      }
    }
  }

  private def enableSystemCondition(playState: PlayState): Unit = playState.gameState = State.AddBalls
  private def disableSystemCondition(playState: PlayState): Unit = playState.gameState = State.Pause
}
