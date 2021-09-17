package dev.atedeg.ecscalademo.controller

import dev.atedeg.ecscala.World
import javafx.fxml.{ FXML, Initializable }
import javafx.scene.control.Label as JfxLabel
import javafx.scene.layout as jfx
import javafx.scene.canvas.Canvas as JfxCanvas
import scalafx.scene.control.{ Button, Label }
import scalafx.scene.canvas.Canvas
import scalafx.animation.AnimationTimer
import scalafx.util.converter.NumberStringConverter

import java.net.URL
import java.util.ResourceBundle
import scala.language.postfixOps

class MainViewController extends Initializable {

  @FXML
  private var playPauseBtn: Button = _

  @FXML
  private var addBallBtn: Button = _

  @FXML
  private var selectBallBtn: Button = _

  @FXML
  private var moveBtn: Button = _

  @FXML
  private var changeVelBtn: Button = _

  @FXML
  private var canvasDelegate: JfxCanvas = _
  private var canvas: Canvas = _

  @FXML
  private var fpsLabel: JfxLabel = _
  private var fps: Label = _

  private val world: World = World()
  //private val ecsController: ECScontroller = ECScontroller(world, 16 milliseconds)
  private var loop: GameLoop = _

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    canvas = new Canvas(canvasDelegate)
    fps = new Label(fpsLabel)

    loop = GameLoop(f => { world.update(f.toFloat) })
    loop.start()
    fps.text.bindBidirectional(loop.fpsProp, new NumberStringConverter("FPS: "))
    //ecsController.start
    //fpsLabel.textProperty().bindBidirectional(???, "FPS: ")
  }

  def onClick: Unit = {
    loop.stop()
  }
}
