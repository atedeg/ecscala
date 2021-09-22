package dev.atedeg.ecscalademo.controller

import dev.atedeg.ecscala.World
import dev.atedeg.ecscalademo.{Coordinates, MouseState, PlayState}
import javafx.fxml.{FXML, Initializable}
import javafx.scene.canvas.Canvas as JfxCanvas
import javafx.scene.control.{Button as JfxButton, Label as JfxLabel}
import javafx.scene.input.MouseEvent
import javafx.scene.layout as jfx
import javafx.scene.layout.Pane as JfxPane
import scalafx.animation.AnimationTimer
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color
import scalafx.util.converter.NumberStringConverter

import java.net.URL
import java.util.ResourceBundle
import scala.language.postfixOps

class MainViewController extends Initializable {

  @FXML
  private var playPauseBtnDelegate: JfxButton = _
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

  private var isRunning = false

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    loop = GameLoop(f => { world.update(f.toFloat) })

    playPauseBtn = new Button(playPauseBtnDelegate)
    canvas = new Canvas(canvasDelegate)
    graphicsContext = canvas.graphicsContext2D
    fps = new Label(fpsDelegate)
    fps.text.bindBidirectional(loop.fps, new NumberStringConverter("FPS: "))

    canvas.widthProperty().addListener(_ => updateCanvas(canvas))
    canvas.heightProperty().addListener(_ => updateCanvas(canvas))
  }

  def onMouseMovedHandler(event: MouseEvent): Unit = {
    MouseState.coordinates = Coordinates(event.getX, event.getY)
  }

  def onMousePressedHandler(event: MouseEvent): Unit = {
    MouseState.down = true
    MouseState.clicked = true
  }

  def onMouseReleasedHandler(event: MouseEvent): Unit = {
    MouseState.up = true
    MouseState.clicked = false
  }

  def onPlayPauseClickHandler(): Unit = {
    if (isRunning) {
      PlayState.playing = false
      isRunning = false
      playPauseBtn.text = "Play"
      loop.stop
    } else {
      PlayState.playing = true
      isRunning = true
      playPauseBtn.text = "Pause"
      loop.start
    }
  }

  private def updateCanvas(canvas: Canvas): Unit = {
    graphicsContext.clearRect(0, 0, canvas.getWidth, canvas.getHeight)
    graphicsContext.setFill(Color.GreenYellow)
    graphicsContext.fillRect(0, 0, canvas.getWidth, canvas.getHeight)
    graphicsContext.setFill(Color.Gray)
    graphicsContext.fillOval(canvas.getWidth - 30, canvas.getHeight - 30, 20, 20)
  }
}
