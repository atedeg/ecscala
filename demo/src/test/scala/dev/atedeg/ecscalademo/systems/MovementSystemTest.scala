package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.{ &:, CNil, World }
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.{ PlayState, Point, Position, Vector, Velocity }
import dev.atedeg.ecscalademo.fixtures.{ SystemsFixture, WorldFixture }
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.util.types.given

class MovementSystemTest extends AnyWordSpec with Matchers with ECScalaDSL {

  "A MovementSystem" should {
    "not run when the game isn't playing" in new WorldFixture with SystemsFixture {
      PlayState.playing = false
      val ball = world hasAn entity withComponents { Position(Point(0, 0)) &: Velocity(Vector(300, 0)) }
      addSystemToWorld(world, movementSystem)

      movementSystem.shouldRun shouldBe false
      getView[Position &: Velocity &: CNil] from world should contain theSameElementsAs List(
        (ball, Position(Point(0, 0)) &: Velocity(Vector(300, 0)) &: CNil),
      )
    }
    "update an entity Position when the game is playing" in new WorldFixture with SystemsFixture {
      PlayState.playing = true
      val ball = world hasAn entity withComponents { Position(Point(0, 0)) &: Velocity(Vector(300, 0)) }
      addSystemToWorld(world, movementSystem)

      movementSystem.shouldRun shouldBe true
      getView[Position &: Velocity &: CNil] from world should contain theSameElementsAs List(
        (ball, Position(Point(3000, 0)) &: Velocity(Vector(300, 0)) &: CNil),
      )
    }
  }

  private def addSystemToWorld(world: World, movementSystem: MovementSystem) = {
    world hasA system(movementSystem)
    world.update(10)
  }
}