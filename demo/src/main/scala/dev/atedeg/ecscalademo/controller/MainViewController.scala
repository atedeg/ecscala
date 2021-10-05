package dev.atedeg.ecscalademo.controller

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
  StartingState,
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
import dev.atedeg.ecscalademo.util.WritableSpacePartitionContainer
import scalafx.beans.property.DoubleProperty

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
  private var wallRestitutionSliderDelegate: JfxSlider = _
  private lazy val wallRestitutionSlider = new Slider(wallRestitutionSliderDelegate)

  @FXML
  private var frictionCoefficientSliderDelegate: JfxSlider = _
  private lazy val frictionCoefficientSlider = new Slider(frictionCoefficientSliderDelegate)

  @FXML
  private var wallRestitutionLabelDelegate: JfxLabel = _
  private lazy val wallRestitutionLabel = new Label(wallRestitutionLabelDelegate)

  @FXML
  private var frictionCoefficientLabelDelegate: JfxLabel = _
  private lazy val frictionCoefficientLabel = new Label(frictionCoefficientLabelDelegate)

  private lazy val ecsCanvas = ScalaFXCanvas(canvas)
  private val world: World = World()
  private var loop: GameLoop = _
  private val mouseState: MouseState = MouseState()
  private val playState: PlayState = PlayState()
  private var environmentState: EnvironmentState = _
  private var startingState: StartingState = _

  private var isRunning = false

  private val addBallButtonLabel = "Add Ball"
  private val stopAddingButtonLabel = "Stop Adding"

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    startingState = StartingState(ecsCanvas)
    val frictionCoefficentProperty = DoubleProperty(0.05)
    val wallRestitutionProperty = DoubleProperty(0.5)
    environmentState = EnvironmentState(frictionCoefficentProperty, wallRestitutionProperty)

    canvas.widthProperty().addListener(e => startingState = StartingState(ecsCanvas))
    canvas.heightProperty().addListener(e => startingState = StartingState(ecsCanvas))

    createEntitiesWithComponents()
    addSystemsToWorld()

    loop = GameLoop(dt => {
      world.update(dt)
      mouseState.down = false
      mouseState.up = false
      refreshUiElements()
    })

    fps.text.bindBidirectional(loop.fps, new NumberStringConverter("FPS: "))
    frictionCoefficentProperty <== frictionCoefficientSlider.value
    wallRestitutionProperty <== wallRestitutionSlider.value
    val decimalFormat = new DecimalFormat()
    decimalFormat.setMaximumFractionDigits(2)
    decimalFormat.setMinimumFractionDigits(2)
    frictionCoefficientLabel.text
      .bindBidirectional(frictionCoefficientSlider.value, new NumberStringConverter(decimalFormat))
    wallRestitutionLabel.text.bindBidirectional(wallRestitutionSlider.value, new NumberStringConverter(decimalFormat))

    loop.start
  }

  private def refreshUiElements(): Unit = {
    playState.gameState match {
      case State.Play => {
        playPauseBtn.text = "Pause"
        addBallBtn.text = addBallButtonLabel
        frictionCoefficientSlider.disable = true
        wallRestitutionSlider.disable = true
        setButtonState(
          isPlayPauseDisable = false,
          isAddBallDiasable = true,
          isChangeVelDisable = true,
          isResetDisable = true,
        )
      }
      case State.Pause => {
        playPauseBtn.text = "Play"
        addBallBtn.text = addBallButtonLabel
        frictionCoefficientSlider.disable = false
        wallRestitutionSlider.disable = false
        setButtonState(
          isPlayPauseDisable = false,
          isAddBallDiasable = false,
          isChangeVelDisable = true,
          isResetDisable = false,
        )
      }
      case State.AddBalls => {
        addBallBtn.text = stopAddingButtonLabel
        setButtonState(
          isPlayPauseDisable = false,
          isAddBallDiasable = false,
          isChangeVelDisable = true,
          isResetDisable = false,
        )
      }
      case State.ChangeVelocity => {
        setButtonState(
          isPlayPauseDisable = false,
          isAddBallDiasable = true,
          isChangeVelDisable = false,
          isResetDisable = false,
        )
      }
      case State.Dragging => {
        setButtonState(
          isPlayPauseDisable = true,
          isAddBallDiasable = true,
          isChangeVelDisable = true,
          isResetDisable = true,
        )
      }
      case State.SelectBall => {
        setButtonState(
          isPlayPauseDisable = false,
          isAddBallDiasable = false,
          isChangeVelDisable = false,
          isResetDisable = false,
        )
      }
    }
  }

  private def setButtonState(
      isPlayPauseDisable: Boolean,
      isAddBallDiasable: Boolean,
      isChangeVelDisable: Boolean,
      isResetDisable: Boolean,
  ): Unit = {
    playPauseBtn.disable = isPlayPauseDisable
    addBallBtn.disable = isAddBallDiasable
    changeVelBtn.disable = isChangeVelDisable
    resetBtn.disable = isResetDisable
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
    playState.gameState =
      if (playState.gameState == State.AddBalls || playState.gameState == State.Play) then playState.gameState
      else State.Dragging
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
        clearAllEntities from world
        createEntitiesWithComponents()
      }
    }
  }

  private def createEntitiesWithComponents() = {
    import dev.atedeg.ecscalademo.StartingState.*
    import dev.atedeg.ecscalademo.EnvironmentState.*
    for {
      ((position, color), velocity) <-
        startingState.startingPosition zip startingState.startingColors zip startingState.startingVelocities
    } world hasAn entity withComponents {
      Circle(startingState.startingRadius, color) &: Position(position) &: Velocity(velocity) &: Mass(startingState.startingMass)
    }
  }

  private def addSystemsToWorld() = {
    val container = WritableSpacePartitionContainer()
    world hasA system(ClearCanvasSystem(ecsCanvas))
    world hasA system(BallCreationSystem(playState, mouseState, startingState))
    world hasA system(BallCreationRenderingSystem(playState, mouseState, startingState, ecsCanvas))
    world hasA system(VelocityEditingSystem(playState, mouseState))
    world hasA system(VelocityArrowSystem(playState, mouseState, ecsCanvas))
    world hasA system(BallSelectionSystem(playState, mouseState))
    world hasA system(DragBallSystem(playState, mouseState))
    world hasA system(FrictionSystem(playState, environmentState))
    world hasA system(MovementSystem(playState))
    world hasA system(RegionAssignmentSystem(playState, container))
    world hasA system(CollisionSystem(playState, container))
    world hasA system(WallCollisionSystem(playState, environmentState, ecsCanvas))
    world hasA system(RenderSystem(playState, ecsCanvas))
  }
}
