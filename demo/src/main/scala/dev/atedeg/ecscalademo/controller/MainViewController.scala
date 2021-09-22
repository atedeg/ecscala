package dev.atedeg.ecscalademo.controller

import dev.atedeg.ecscala.World
import dev.atedeg.ecscalademo.{Coordinates, ECSCanvas, MouseState, PlayState}
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.Label as JfxLabel
import javafx.scene.control.Button as JfxButton
import javafx.scene.layout as jfx
import javafx.scene.canvas.Canvas as JfxCanvas
import javafx.scene.layout.Pane as JfxPane
import scalafx.scene.control.{Button, Label}
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.paint.Color
import scalafx.scene.layout.Pane
import javafx.scene.input.MouseEvent
import scalafx.scene.shape.ArcType
import scalafx.animation.AnimationTimer
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
    fps = new Label(fpsDelegate)
    fps.text.bindBidirectional(loop.fps, new NumberStringConverter("FPS: "))

    ECSCanvas(canvas)
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
}
