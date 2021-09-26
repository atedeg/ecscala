package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.{ Entity, World }
import dev.atedeg.ecscalademo.fixtures.WorldFixture
import dev.atedeg.ecscalademo.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class BallCreationSystemTest extends AnyWordSpec with Matchers {

  trait BallCreationSystemFixture extends WorldFixture {
    lazy val creationSystem: BallCreationSystem = BallCreationSystem()
  }

  "A BallCreationSystem" when {
    "the mouse is clicked and the game is not running" should {
      "be executed" in new BallCreationSystemFixture {
        enableSystemCondition()
        creationSystem.shouldRun shouldBe true
      }
    }
    "the mouse is not clicked and game is not running" should {
      "not be executed" in new BallCreationSystemFixture {
        disableSystemCondition()
        creationSystem.shouldRun shouldBe false
      }
    }
    "enabled" should {
      "create a ball in a free position" in new BallCreationSystemFixture {
        enableSystemCondition()
        simulateCreateBall(world, entity, creationSystem, Point(0.0, 0.0), Point(100.0, 100.0))
        world.entitiesCount shouldBe 2
      }
      "not create a ball over another one" in new BallCreationSystemFixture {
        enableSystemCondition()
        simulateCreateBall(world, entity, creationSystem, Point(10.0, 10.0), Point(10.0, 10.0))
        world.entitiesCount shouldBe 1
      }
      "not create a ball when the mouse is inside another ball" in new BallCreationSystemFixture {
        enableSystemCondition()
        simulateCreateBall(world, entity, creationSystem, Point(10.0, 10.0), Point(15.0, 15.0))
        world.entitiesCount shouldBe 1
      }
    }
  }

  private def enableSystemCondition(): Unit = {
    PlayState.addBallMode = true
    MouseState.clicked = true
  }

  private def disableSystemCondition(): Unit = {
    PlayState.addBallMode = true
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
