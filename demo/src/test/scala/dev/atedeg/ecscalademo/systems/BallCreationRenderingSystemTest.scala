package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscalademo.{ PlayState, ScalaFXCanvas }
import dev.atedeg.ecscalademo.fixture.WorldFixture
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import scalafx.scene.canvas.Canvas
import javafx.scene.canvas.Canvas as JfxCanvas

class BallCreationRenderingSystemTest extends AnyWordSpec with Matchers {

  trait BallCreationRenderingSystemFixture extends WorldFixture {
    lazy val system = BallCreationRenderingSystem(ScalaFXCanvas(new Canvas(new JfxCanvas())))
  }

  "A RenderingCreationBallSystem" when {
    "the game is run" should {
      "enabled" in new BallCreationRenderingSystemFixture {
        enableSystemCondition
        system.shouldRun shouldBe true
      }
      "disabled" in new BallCreationRenderingSystemFixture {
        disableSystemCondition
        system.shouldRun shouldBe false
      }
    }
  }

  private def enableSystemCondition: Unit = PlayState.playing = false
  private def disableSystemCondition: Unit = PlayState.playing = true
}
