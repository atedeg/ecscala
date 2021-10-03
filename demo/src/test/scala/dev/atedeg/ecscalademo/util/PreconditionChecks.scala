package dev.atedeg.ecscalademo.util

import dev.atedeg.ecscala.System
import dev.atedeg.ecscalademo.{MouseState, PlayState, State}
import org.scalatest.Assertions.withClue
import org.scalatest.matchers.should.Matchers.shouldBe
import org.scalatest.prop.TableDrivenPropertyChecks.*

sealed trait AnyValue
case object AnyValue extends AnyValue
type TestState[T] = T | AnyValue
type ClickedState = TestState[Boolean]
type DownState = TestState[Boolean]
type UpState = TestState[Boolean]
type StateDescription = (TestState[State], ClickedState, DownState, UpState)

private given defaultBooleanValues: Set[Boolean] = Set(true, false)
private given defaultStateValues: Set[State] = Set.from(State.values)
extension[T] (state: TestState[T]) {
  def values(using defaultValues: Set[T]): Set[T] = state match {
    case AnyValue => defaultValues
    case t => Set(t.asInstanceOf[T])
  }
}

def checkAllStates(systemBuilder: (PlayState, MouseState) => System[?])(enabled: StateDescription*): Unit = {
  import StateUtils.*
  val enabledStates = expandStates(enabled*)
  val disabledStates = allStates -- enabledStates
  println(enabledStates)
  println(disabledStates)
  checkStates(systemBuilder)(enabledStates)(true)
  checkStates(systemBuilder)(disabledStates)(false)
}

private object StateUtils {
  def checkStates(systemBuilder: (PlayState, MouseState) => System[?])
                         (states: Set[(PlayState, MouseState)])
                         (shouldRun: Boolean) = {
    val table = Table(("playState", "mouseState"), states.toSeq*)
    forAll(table) { (playState, mouseState) =>
      val errorClue = s"System expected to be ${if shouldRun then "enabled, instead was disabled" else "disabled, instead was enabled" }."
      withClue(errorClue) { systemBuilder(playState, mouseState).shouldRun shouldBe shouldRun }
    }
  }

  def expandStates(states: StateDescription*): Set[(PlayState, MouseState)] = for {
    (stateValue, clickedValue, downValue, upValue) <- Set.from(states)
    state <- stateValue.values
    clicked <- clickedValue.values
    down <- downValue.values
    up <- upValue.values
  } yield (PlayState(state), MouseState(clicked = clicked, down = down, up = up))

  def allStates: Set[(PlayState, MouseState)] = for {
    clicked <- Set(true, false)
    down <- Set(true, false)
    up <- Set(true, false)
    state <- Set.from(State.values)
  } yield (PlayState(state), MouseState(clicked = clicked, down = down, up = up))
}
