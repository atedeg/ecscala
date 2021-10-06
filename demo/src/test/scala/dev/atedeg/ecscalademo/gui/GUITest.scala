package dev.atedeg.ecscalademo.gui

import dev.atedeg.ecscalademo.State
import dev.atedeg.ecscalademo.controller.MainViewController
import dev.atedeg.ecscalademo.gui.TestData.*
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.stage.Stage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.{ Arguments, MethodSource }
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.testfx.api.FxRobot
import org.testfx.assertions.api.Assertions.assertThat
import org.testfx.framework.junit5.{ ApplicationExtension, Start }
import scala.jdk.javaapi.StreamConverters
import scalafx.Includes.*
import scalafx.scene.Scene

object TestData {
  val playPauseButtonId = "#playPauseBtnDelegate"
  val addBallButtonId = "#addBallBtnDelegate"
  val changeVelocityButtonId = "#changeVelBtnDelegate"
  val resetButtonId = "#resetBtnDelegate"
  val canvasId = "#canvasDelegate"

  type ButtonName = String
  type Disabled = Boolean
  type ButtonState = (ButtonName, Disabled)

  private def buttonsState(
      playPause: Boolean,
      addBall: Boolean,
      changeVelocity: Boolean,
      reset: Boolean,
  ): Seq[ButtonState] = Seq(
    (playPauseButtonId, playPause),
    (addBallButtonId, addBall),
    (changeVelocityButtonId, changeVelocity),
    (resetButtonId, reset),
  )

  type EnabledTransitions = Seq[(State, FxRobot => Unit)]

  private val pauseEnabledTransitions: EnabledTransitions = Seq(
    State.AddBalls -> reachAddBallState,
    State.SelectBall -> reachSelectBallState,
    State.Play -> reachPlayState,
    State.Pause -> (_.clickOn(resetButtonId)),
  )

  private val playEnabledTransitions: EnabledTransitions = Seq(
    State.Pause -> (_.clickOn(playPauseButtonId)),
  )

  private val addBallsEnabledTransitions: EnabledTransitions = Seq(
    State.Pause -> (_.clickOn(addBallButtonId)),
    State.AddBalls -> (_.clickOn(canvasId)),
    State.Pause -> (_.clickOn(resetButtonId)),
  )

  private val selectBallEnabledTransitions: EnabledTransitions = Seq(
    State.Play -> (_.clickOn(playPauseButtonId)),
    State.SelectBall -> reachSelectBallState,
    State.ChangeVelocity -> (_.clickOn(changeVelocityButtonId)),
    State.AddBalls -> (_.clickOn(addBallButtonId)),
    State.Pause -> (_.clickOn(canvasId)),
    State.Pause -> (_.clickOn(resetButtonId)),
  )

  private val changeVelocityEnabledTransitions: EnabledTransitions = Seq(
    State.Play -> (_.clickOn(playPauseButtonId)),
    State.Pause -> (_.clickOn(resetButtonId)),
    State.Pause -> (_.clickOn(changeVelocityButtonId)),
  )

  private def reachPlayState(fxRobot: FxRobot) = fxRobot.clickOn(playPauseButtonId)

  private def reachAddBallState(fxRobot: FxRobot) = fxRobot.clickOn(addBallButtonId)

  private def reachSelectBallState(fxRobot: FxRobot) = {
    fxRobot.moveTo(mainScene.lookup(canvasId).get)
    fxRobot.moveBy(50, 0)
    fxRobot.clickOn()
  }

  private def reachChangeVelocityState(fxRobot: FxRobot) = {
    reachSelectBallState(fxRobot)
    fxRobot.clickOn(changeVelocityButtonId)
  }

  type StateDescription = (FxRobot => Unit, EnabledTransitions, Seq[ButtonState])

  private val stateDescriptions: Map[State, StateDescription] = Map(
    State.Pause -> (_ => (), pauseEnabledTransitions, buttonsState(false, false, true, false)),
    State.Play -> (reachPlayState, playEnabledTransitions, buttonsState(false, true, true, true)),
    State.AddBalls -> (reachAddBallState, addBallsEnabledTransitions, buttonsState(false, false, true, false)),
    State.SelectBall -> (reachSelectBallState, selectBallEnabledTransitions, buttonsState(false, false, false, false)),
    State.ChangeVelocity ->
      (reachChangeVelocityState, changeVelocityEnabledTransitions, buttonsState(false, true, false, false)),
  )

  def buttonsTestArguments = StreamConverters.asJavaSeqStream(
    for {
      (state, (reachState, _, expectedButtonsConfiguration)) <- stateDescriptions
    } yield Arguments.of(state, reachState, expectedButtonsConfiguration),
  )

  def transitionsTestArguments = StreamConverters.asJavaSeqStream(
    for {
      (state, (reachState, transitions, _)) <- stateDescriptions
      (expectedState, transition) <- transitions
    } yield Arguments.of(state, reachState, transition, expectedState),
  )
}

private var mainScene: Scene = _
private var controller: MainViewController = _

@RunWith(classOf[JUnitPlatform])
@ExtendWith(Array(classOf[ApplicationExtension]))
class GUITest {

  // Necessary in order to test the GUI in the CI headless environemnt
  private def setupHeadlessTesting(): Unit = {
    System.setProperty("testfx.robot", "glass")
    System.setProperty("testfx.headless", "true")
    System.setProperty("prism.order", "sw")
    System.setProperty("prism.text", "t2k")
    System.setProperty("monocle.platform", "Headless")
    System.setProperty("glass.platform", "Monocle")
  }
  setupHeadlessTesting()

  @Start def start(stage: Stage): Unit = {
    val loader: FXMLLoader = FXMLLoader()
    val root: Parent = loader.load(getClass.getResource("/MainView.fxml").openStream)
    controller = loader.getController[MainViewController]
    mainScene = new Scene(root)
    stage.setScene(mainScene)
    stage.setMinHeight(540)
    stage.setMinWidth(960)
    stage.show()
  }

  @ParameterizedTest(name = "The {0} state should respects its button configuration")
  @MethodSource(Array("dev.atedeg.ecscalademo.gui.TestData#buttonsTestArguments"))
  def checkStatesButtons(
      testedState: State,
      reachState: FxRobot => Unit,
      expectedButtonsConfiguration: Seq[ButtonState],
  ): Unit = {
    val robot = new FxRobot()
    reachState(robot)
    robot.checkAllButtons(expectedButtonsConfiguration)
  }

  @ParameterizedTest(name = "It should be possible to go from the {0} state to the {3} state")
  @MethodSource(Array("dev.atedeg.ecscalademo.gui.TestData#transitionsTestArguments"))
  def checkStateTransitions(
      testedState: State,
      reachState: FxRobot => Unit,
      reachExpectedState: FxRobot => Unit,
      expectedState: State,
  ): Unit = {
    val robot = new FxRobot()
    reachState(robot)
    reachExpectedState(robot)
    assertEquals(expectedState, controller.playState.gameState)
  }

  @Test
  def twoResetsInARowShouldNotThrowAnException(fxRobot: FxRobot): Unit = {
    (1 to 2) foreach { _ =>
      fxRobot.clickOn(playPauseButtonId)
      fxRobot.sleep(100)
      fxRobot.clickOn(playPauseButtonId)
      fxRobot.clickOn(resetButtonId)
    }
  }
}

extension (fxRobot: FxRobot) {

  def checkAllButtons(buttonsExpectedConfiguration: Seq[ButtonState]): Unit =
    buttonsExpectedConfiguration foreach { fxRobot.findButton(_).checkEnabled(_) }
  def findButton(buttonId: String): Button = fxRobot.lookup(buttonId).queryButton()
}

extension (button: Button) {

  def checkEnabled(shouldBeEnabled: Boolean): Unit =
    if shouldBeEnabled then assertThat(button).isDisabled
    else assertThat(button).isEnabled
}
