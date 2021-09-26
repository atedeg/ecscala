package dev.atedeg.ecscalademo.controller

import dev.atedeg.ecscala.{ &:, CNil, World }
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.{ Circle, Color, MouseState, PlayState, Point, Position, ScalaFXCanvas, Vector, Velocity }
import javafx.fxml.{ FXML, Initializable }
import javafx.scene.control.Label as JfxLabel
import javafx.scene.control.Button as JfxButton
import javafx.scene.layout as jfx
import javafx.scene.canvas.Canvas as JfxCanvas
import javafx.scene.layout.Pane as JfxPane
import scalafx.scene.control.{ Button, Label }
import scalafx.scene.canvas.{ Canvas, GraphicsContext }
import scalafx.scene.layout.Pane
import javafx.scene.input.MouseEvent
import scalafx.animation.AnimationTimer
import scalafx.util.converter.NumberStringConverter
import dev.atedeg.ecscalademo.systems.*

import java.net.URL
import java.util.ResourceBundle
import scala.language.postfixOps
import dev.atedeg.ecscala.util.types.given

class MainViewController() extends Initializable with ECScalaDSL {

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

  private var loop: GameLoop = _

  private var isRunning = false

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    import dev.atedeg.ecscalademo.StartingState.*
    val world = World()
    for {
      ((position, color), velocity) <- startingPositions zip startingColors zip startingVelocities
    } world hasAn entity withComponents { Circle(radius, color) &: position &: velocity }

    loop = GameLoop(world.update(_))

    playPauseBtn = new Button(playPauseBtnDelegate)
    canvas = new Canvas(canvasDelegate)
    fps = new Label(fpsDelegate)
    fps.text.bindBidirectional(loop.fps, new NumberStringConverter("FPS: "))

    addSystemsToWorld(world, canvas)
    loop.start
  }

  def onMouseMovedHandler(event: MouseEvent): Unit = {
    MouseState.coordinates = Point(event.getX, event.getY)
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
    } else {
      PlayState.playing = true
      isRunning = true
      playPauseBtn.text = "Pause"
    }
  }

  def addSystemsToWorld(world: World, canvas: scalafx.scene.canvas.Canvas) = {
    world hasA system(FrictionSystem())
    world hasA system(MovementSystem())
    world hasA system(RenderSystem(ScalaFXCanvas(canvas)))
  }
}
