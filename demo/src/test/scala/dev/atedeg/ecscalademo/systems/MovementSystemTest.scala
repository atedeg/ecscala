package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.{ &:, CNil, World }
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.{ PlayState, Point, Position, State, Vector, Velocity }
import dev.atedeg.ecscalademo.fixtures.{ MovementSystemFixture, WorldFixture }
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.util.types.given
import org.mockito.Mockito.when

class MovementSystemTest extends AnyWordSpec with Matchers with ECScalaDSL {

  "A MovementSystem" should {
    "not run when the game isn't playing" in new MovementSystemFixture {
      playState.gameState = State.Pause
      val ball = world hasAn entity withComponents { Position(0, 0) &: Velocity(300, 0) }
      world.update(10)

      movementSystem.shouldRun shouldBe false
      getView[Position &: Velocity &: CNil] from world should contain theSameElementsAs List(
        (ball, Position(0, 0) &: Velocity(300, 0) &: CNil),
      )
    }
    "update an entity Position when the game is playing" in new MovementSystemFixture {
      playState.gameState = State.Play
      val ball = world hasAn entity withComponents { Position(0, 0) &: Velocity(300, 0) }
      world.update(10)

      movementSystem.shouldRun shouldBe true
      getView[Position &: Velocity &: CNil] from world should contain theSameElementsAs List(
        (ball, Position(3000, 0) &: Velocity(300, 0) &: CNil),
      )
    }
  }
}
