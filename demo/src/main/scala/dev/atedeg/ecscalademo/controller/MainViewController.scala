package dev.atedeg.ecscalademo.controller

import dev.atedeg.ecscala.World
import javafx.fxml.{ FXML, Initializable }
import javafx.scene.control.Label as JfxLabel
import javafx.scene.layout as jfx
import javafx.scene.canvas.Canvas as JfxCanvas
import javafx.scene.layout.Pane as JfxPane
import scalafx.scene.control.{ Button, Label }
import scalafx.scene.canvas.{ Canvas, GraphicsContext }
import scalafx.scene.paint.Color
import scalafx.scene.layout.Pane
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
  private var graphicsContext: GraphicsContext = _

  @FXML
  private var fpsDelegate: JfxLabel = _
  private var fps: Label = _

  private val world: World = World()
  private var loop: GameLoop = _

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    loop = GameLoop(f => { world.update(f.toFloat) })

    canvas = new Canvas(canvasDelegate)
    graphicsContext = canvas.graphicsContext2D
    fps = new Label(fpsDelegate)
    fps.text.bindBidirectional(loop.fps, new NumberStringConverter("FPS: "))

    canvas.widthProperty().addListener(_ => updateCanvas)
    canvas.heightProperty().addListener(_ => updateCanvas)

    loop.start
  }

  private def updateCanvas: Unit = {
    graphicsContext.clearRect(0, 0, canvas.getWidth, canvas.getHeight)
  }
}
