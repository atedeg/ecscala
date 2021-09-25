package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.{ Entity, World }
import dev.atedeg.ecscalademo.fixture.WorldFixture
import dev.atedeg.ecscalademo.{ Circle, MouseState, PlayState, Point, Position, StartingState }
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class BallCreationSystemTest extends AnyWordSpec with Matchers {

  trait BallCreationSystemFixture extends WorldFixture {
    lazy val creationSystem: BallCreationSystem = BallCreationSystem()
  }

  "A BallCreationSystem" when {
    "the mouse is clicked and game not run" should {
      "be executed" in new BallCreationSystemFixture {
        enableSystemCondition()
        creationSystem.shouldRun shouldBe true
      }
    }
    "the mouse is not clicked and game not run" should {
      "not be executed" in new BallCreationSystemFixture {
        disableSystemCondition()
        creationSystem.shouldRun shouldBe false
      }
    }
    "enabled" should {
      "create a ball in a free position" in new BallCreationSystemFixture {
        enableSystemCondition()
        simulateCreateBall(world, entity, creationSystem, Point(0, 0), Point(100, 100))
        world.entitiesCount shouldBe 2
      }
      "not create a ball over another one" in new BallCreationSystemFixture {
        enableSystemCondition()
        simulateCreateBall(world, entity, creationSystem, Point(10, 10), Point(10, 10))
        world.entitiesCount shouldBe 1
      }
      "not create a ball when the mouse is inside other ball" in new BallCreationSystemFixture {
        enableSystemCondition()
        simulateCreateBall(world, entity, creationSystem, Point(10, 10), Point(15, 15))
        world.entitiesCount shouldBe 1
      }
    }
  }

  private def enableSystemCondition(): Unit = {
    PlayState.playing = false
    MouseState.clicked = true
  }

  private def disableSystemCondition(): Unit = {
    PlayState.playing = false
    MouseState.clicked = false
  }

  private def simulateCreateBall(
      world: World,
      existingEntity: Entity,
      system: BallCreationSystem,
      existingPosition: Point,
      mousePosition: Point,
  ): Unit = {
    existingEntity.addComponent(Position(existingPosition))
    existingEntity.addComponent(Circle(StartingState.startingRadius, StartingState.startingColor))
    world.update(10)
    MouseState.coordinates = mousePosition
    world.addSystem(system)
    world.update(10)
  }
}
