package dev.atedeg.ecscalademo.systems

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.mockito.Mockito.when
import dev.atedeg.ecscala.{ &:, CNil }
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.{ PlayState, Point, Position, State, Vector, Velocity }
import dev.atedeg.ecscalademo.fixtures.{ MovementSystemFixture, WorldFixture }
import dev.atedeg.ecscalademo.util.{ checkAllStates, AnyValue }

class MovementSystemTest extends AnyWordSpec with Matchers with ECScalaDSL {

  "A MovementSystem" should {
    "run" when {
      "in an enabled state" in
        checkAllStates((playState, _) => MovementSystem(playState))(
          (State.Play, AnyValue, AnyValue, AnyValue),
        )
    }
  }

  "A MovementSystem" should {
    "update an entity Position" when {
      "the game is playing" in new MovementSystemFixture {
        playState.gameState = State.Play
        world.update(10)
        getView[Position &: Velocity &: CNil] from world should contain theSameElementsAs List(
          (ball, Position(3000, 0) &: Velocity(300, 0) &: CNil),
        )
      }
    }
  }
}
