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

  private var isRunning = false

  private val addBallButtonLabel = "Add Ball"
  private val stopAddingButtonLabel = "Stop Adding"
  private val playButtonLabel = "Play"
  private val pauseButtonLabel = "Pause"

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    createEntitiesWithComponents()
    addSystemsToWorld()

    loop = GameLoop(dt => {
      world.update(dt)
      MouseState.down = false
      MouseState.up = false
      // This check is made here because we cannot pass buttons to the systems
      if (!PlayState.playing && PlayState.selectedBall.isDefined) {
        changeVelBtn.disable = false
      } else {
        changeVelBtn.disable = true
      }
    })

    fps.text.bindBidirectional(loop.fps, new NumberStringConverter("FPS: "))
    EnvironmentState.frictionCoefficient <== fcSlider.value
    EnvironmentState.wallRestitution <== wrSlider.value
    val decimalFormat = new DecimalFormat()
    decimalFormat.setMaximumFractionDigits(2)
    decimalFormat.setMinimumFractionDigits(2)
    fcLabel.text.bindBidirectional(fcSlider.value, new NumberStringConverter(decimalFormat))
    wrLabel.text.bindBidirectional(wrSlider.value, new NumberStringConverter(decimalFormat))

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
    PlayState.isDragging = false
  }

  def onDragDetectedHandler(event: MouseEvent): Unit = {
    MouseState.coordinates = Point(event.getX, event.getY)
    PlayState.isDragging = true
  }

  def onPlayPauseClickHandler(): Unit = {
    PlayState.playing = !isRunning
    PlayState.selectedBall = None
    isRunning = !isRunning
    playPauseBtn.text = if isRunning then pauseButtonLabel else playButtonLabel
    changeVelBtn.disable = isRunning
    addBallBtn.disable = isRunning
    addBallBtn.text = addBallButtonLabel
    wrSlider.disable = isRunning
    fcSlider.disable = isRunning
    if (!isRunning) {
      PlayState.velocityEditingMode = false
      PlayState.addBallMode = false
    }
    resetBtn.disable = isRunning
  }

  def onAddBallButtonHandler(): Unit = {
    PlayState.addBallMode = !PlayState.addBallMode
    addBallBtn.text = if PlayState.addBallMode then stopAddingButtonLabel else addBallButtonLabel
    PlayState.velocityEditingMode = false
    changeVelBtn.disable = true
  }

  def onChangeVelocityButtonHandler(): Unit = {
    PlayState.addBallMode = false
    PlayState.velocityEditingMode = true
    addBallBtn.disable = false
  }

  def onResetClickHandler(): Unit = {
    if (!PlayState.playing) {
      PlayState.selectedBall = Option.empty
      ecsCanvas.clear()
      clearAll from world
      createEntitiesWithComponents()
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
    world hasA system(BallCreationSystem())
    world hasA system(BallCreationRenderingSystem(ecsCanvas))
    world hasA system(VelocityEditingSystem())
    world hasA system(VelocityArrowSystem(ecsCanvas))
    world hasA system(BallSelectionSystem())
    world hasA system(DragBallSystem())
    world hasA system(RegionAssignmentSystem(container))
    world hasA system(FrictionSystem())
    world hasA system(MovementSystem())
    world hasA system(CollisionSystem(container))
    world hasA system(WallCollisionSystem(ecsCanvas))
    world hasA system(RenderSystem(ecsCanvas))
  }
}
