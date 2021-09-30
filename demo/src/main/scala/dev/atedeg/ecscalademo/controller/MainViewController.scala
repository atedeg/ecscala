package dev.atedeg.ecscalademo.controller

import dev.atedeg.ecscala.World
import dev.atedeg.ecscalademo.{
  Circle,
  Color,
  ECSCanvas,
  EnvironmentState,
  Mass,
  MouseState,
  PlayState,
  Point,
  Position,
  ScalaFXCanvas,
  State,
  Vector,
  Velocity,
}
import dev.atedeg.ecscala.{ &:, CNil, World }
import dev.atedeg.ecscala.dsl.ECScalaDSL
import javafx.fxml.{ FXML, Initializable }
import javafx.scene.control.Label as JfxLabel
import javafx.scene.control.Button as JfxButton
import javafx.scene.control.Slider as JfxSlider
import javafx.scene.layout as jfx
import javafx.scene.canvas.Canvas as JfxCanvas
import javafx.scene.layout.Pane as JfxPane
import scalafx.scene.control.{ Button, Label, Slider }
import scalafx.scene.canvas.{ Canvas, GraphicsContext }
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
import dev.atedeg.ecscalademo.StartingState.{ startingMass, startingPositions, startingRadius }
import dev.atedeg.ecscalademo.util.WritableSpacePartitionContainer

import java.text.DecimalFormat

class MainViewController extends Initializable with ECScalaDSL {

  @FXML
  private var playPauseBtnDelegate: JfxButton = _
  private lazy val playPauseBtn: Button = new Button(playPauseBtnDelegate)

  @FXML
  private var addBallBtnDelegate: JfxButton = _
  private lazy val addBallBtn: Button = new Button(addBallBtnDelegate)

  @FXML
  private var changeVelBtnDelegate: JfxButton = _
  private lazy val changeVelBtn: Button = new Button(changeVelBtnDelegate)

  @FXML
  private var resetBtnDelegate: JfxButton = _
  private lazy val resetBtn = new Button(resetBtnDelegate)

  @FXML
  private var canvasDelegate: JfxCanvas = _
  private lazy val canvas: Canvas = new Canvas(canvasDelegate)

  @FXML
  private var fpsDelegate: JfxLabel = _
  private lazy val fps: Label = new Label(fpsDelegate)

  @FXML
  private var wrSliderDelegate: JfxSlider = _
  private lazy val wrSlider = new Slider(wrSliderDelegate)

  @FXML
  private var fcSliderDelegate: JfxSlider = _
  private lazy val fcSlider = new Slider(fcSliderDelegate)

  @FXML
  private var fcLabelDelegate: JfxLabel = _
  private lazy val fcLabel = new Label(fcLabelDelegate)

  @FXML
  private var wrLabelDelegate: JfxLabel = _
  private lazy val wrLabel = new Label(wrLabelDelegate)

  private lazy val ecsCanvas = ScalaFXCanvas(canvas)
  private val world: World = World()
  private var loop: GameLoop = _
  private var mouseState: MouseState = MouseState()
  private var playState: PlayState = PlayState()
  private var environmentState = EnvironmentState()

  private var isRunning = false

  private val addBallButtonLabel = "Add Ball"
  private val stopAddingButtonLabel = "Stop Adding"

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    createEntitiesWithComponents()
    addSystemsToWorld()

    loop = GameLoop(dt => {
      world.update(dt)
      mouseState.down = false
      mouseState.up = false
      refreshUiElements()
    })

    fps.text.bindBidirectional(loop.fps, new NumberStringConverter("FPS: "))
    environmentState.frictionCoefficient <== fcSlider.value
    environmentState.wallRestitution <== wrSlider.value
    val decimalFormat = new DecimalFormat()
    decimalFormat.setMaximumFractionDigits(2)
    decimalFormat.setMinimumFractionDigits(2)
    fcLabel.text.bindBidirectional(fcSlider.value, new NumberStringConverter(decimalFormat))
    wrLabel.text.bindBidirectional(wrSlider.value, new NumberStringConverter(decimalFormat))

    loop.start
  }

  private def refreshUiElements(): Unit = {
    playState.gameState match {
      case State.Play => {
        playPauseBtn.text = "Pause"
        addBallBtn.text = addBallButtonLabel
        playPauseBtn.disable = false
        addBallBtn.disable = true
        changeVelBtn.disable = true
        resetBtn.disable = true
      }
      case State.Pause => {
        playPauseBtn.text = "Play"
        addBallBtn.text = addBallButtonLabel
        playPauseBtn.disable = false
        addBallBtn.disable = false
        changeVelBtn.disable = true
        resetBtn.disable = false
      }
      case State.AddBalls => {
        addBallBtn.text = stopAddingButtonLabel
        playPauseBtn.disable = false
        addBallBtn.disable = false
        changeVelBtn.disable = true
        resetBtn.disable = false
      }
      case State.ChangeVelocity => {
        playPauseBtn.disable = false
        addBallBtn.disable = true
        changeVelBtn.disable = false
        resetBtn.disable = false
      }
      case State.Dragging => {
        playPauseBtn.disable = true
        addBallBtn.disable = true
        changeVelBtn.disable = true
        resetBtn.disable = true
      }
      case State.SelectBall => {
        playPauseBtn.disable = false
        addBallBtn.disable = false
        changeVelBtn.disable = false
        resetBtn.disable = false
      }
    }
  }

  def onMouseMovedHandler(event: MouseEvent): Unit = mouseState.coordinates = Point(event.getX, event.getY)

  def onMousePressedHandler(event: MouseEvent): Unit = {
    mouseState.down = true
    mouseState.clicked = true
  }

  def onMouseReleasedHandler(event: MouseEvent): Unit = {
    mouseState.up = true
    mouseState.clicked = false
    playState.gameState = if playState.gameState == State.Dragging then State.SelectBall else playState.gameState
  }

  def onDragDetectedHandler(event: MouseEvent): Unit = {
    mouseState.coordinates = Point(event.getX, event.getY)
    playState.gameState = if playState.gameState == State.AddBalls then playState.gameState else State.Dragging
  }

  def onPlayPauseClickHandler(): Unit = {
    isRunning = !isRunning
    playState.gameState = if isRunning then State.Play else State.Pause
  }

  def onAddBallButtonHandler(): Unit = playState.gameState =
    if playState.gameState == State.AddBalls then State.Pause else State.AddBalls

  def onChangeVelocityButtonHandler(): Unit = playState.gameState =
    if playState.gameState == State.SelectBall then State.ChangeVelocity else State.Pause

  def onResetClickHandler(): Unit = {
    playState.gameState match {
      case State.Play | State.Dragging => ()
      case _ => {
        playState.selectedBall = None
        clearAll from world
        createEntitiesWithComponents()
      }
    }
  }

  private def createEntitiesWithComponents() = {
    import dev.atedeg.ecscalademo.StartingState.*
    import dev.atedeg.ecscalademo.EnvironmentState.*
    for {
      ((position, color), velocity) <- startingPositions zip startingColors zip startingVelocities
    } world hasAn entity withComponents { Circle(startingRadius, color) &: position &: velocity &: Mass(startingMass) }
  }

  private def addSystemsToWorld() = {
    val container = WritableSpacePartitionContainer()
    world hasA system(ClearCanvasSystem(ecsCanvas))
    world hasA system(BallCreationSystem(playState, mouseState))
    world hasA system(BallCreationRenderingSystem(playState, mouseState, ecsCanvas))
    world hasA system(VelocityEditingSystem(playState, mouseState))
    world hasA system(VelocityArrowSystem(playState, mouseState, ecsCanvas))
    world hasA system(BallSelectionSystem(playState, mouseState))
    world hasA system(DragBallSystem(playState, mouseState))
    world hasA system(FrictionSystem(playState, environmentState))
    world hasA system(MovementSystem(playState))
    world hasA system(RegionAssignmentSystem(container))
    world hasA system(CollisionSystem(playState, container))
    world hasA system(WallCollisionSystem(playState, environmentState, ecsCanvas))
    world hasA system(RenderSystem(playState, ecsCanvas))
  }
}
