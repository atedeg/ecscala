package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.fixtures.WorldFixture
import dev.atedeg.ecscalademo.{ ECSCanvas, PlayState, ScalaFXCanvas }
import javafx.scene.canvas.Canvas as JfxCanvas
import org.mockito.ArgumentMatchers.{ any, anyDouble }
import org.mockito.Mockito.verify
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import scalafx.scene.canvas.Canvas

class BallCreationRenderingSystemTest extends AnyWordSpec with Matchers with MockitoSugar {

  trait BallCreationRenderingSystemFixture extends WorldFixture {
    val canvasMock = mock[ECSCanvas]
    lazy val system = BallCreationRenderingSystem(canvasMock)
  }

  "A RenderingCreationBallSystem" when {
    "the game is not running" should {
      "be enabled" in new BallCreationRenderingSystemFixture {
        enableSystemCondition()
        system.shouldRun shouldBe true
      }
      "render the ball" in new BallCreationRenderingSystemFixture {
        enableSystemCondition()
        world.addSystem(system)
        world.update(10)
        verify(canvasMock).drawCircle(any(), anyDouble(), any(), anyDouble())
      }
    }
    "the game is running" should {
      "be disabled" in new BallCreationRenderingSystemFixture {
        disableSystemCondition()
        system.shouldRun shouldBe false
      }
    }
  }

  private def enableSystemCondition(): Unit = PlayState.addBallMode = true
  private def disableSystemCondition(): Unit = PlayState.addBallMode = false
}
