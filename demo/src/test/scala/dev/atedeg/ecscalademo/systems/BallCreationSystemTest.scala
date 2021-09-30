package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.{ CNil, Entity, World }
import dev.atedeg.ecscalademo.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock

class BallCreationSystemTest extends AnyWordSpec with Matchers with ECScalaDSL {

  trait BallCreationSystemFixture {
    val world = World()
    val entity1 = world hasAn entity
    val playState = PlayState()
    val mouseState = MouseState()
    val startingState = StartingState(mock[ECSCanvas])
    lazy val creationSystem: BallCreationSystem = new BallCreationSystem(playState, mouseState, startingState)
  }

  "A BallCreationSystem" when {
    "the mouse is clicked and the game is not running" should {
      "be executed" in new BallCreationSystemFixture {
        enableSystemCondition(playState, mouseState)
        creationSystem.shouldRun shouldBe true
      }
    }
    "the mouse is not clicked and game is not running" should {
      "not be executed" in new BallCreationSystemFixture {
        disableSystemCondition(playState, mouseState)
        creationSystem.shouldRun shouldBe false
      }
    }
    "enabled" should {
      "create a ball in a free position" in new BallCreationSystemFixture {
        enableSystemCondition(playState, mouseState)
        simulateCreateBall(
          world,
          entity1,
          creationSystem,
          Point(0.0, 0.0),
          Point(100.0, 100.0),
          mouseState,
          startingState,
        )
        world.entitiesCount shouldBe 2
      }
      "not create a ball over another one" in new BallCreationSystemFixture {
        enableSystemCondition(playState, mouseState)
        simulateCreateBall(
          world,
          entity1,
          creationSystem,
          Point(10.0, 10.0),
          Point(10.0, 10.0),
          mouseState,
          startingState,
        )
        world.entitiesCount shouldBe 1
      }
      "not create a ball when the mouse is inside another ball" in new BallCreationSystemFixture {
        enableSystemCondition(playState, mouseState)
        simulateCreateBall(
          world,
          entity1,
          creationSystem,
          Point(10.0, 10.0),
          Point(15.0, 15.0),
          mouseState,
          startingState,
        )
        world.entitiesCount shouldBe 1
      }
    }
  }

  private def enableSystemCondition(playState: PlayState, mouseState: MouseState): Unit = {
    playState.gameState = State.AddBalls
    mouseState.clicked = true
  }

  private def disableSystemCondition(playState: PlayState, mouseState: MouseState): Unit = {
    playState.gameState = State.Play
    mouseState.clicked = false
  }

  private def simulateCreateBall(
      world: World,
      existingEntity: Entity,
      system: BallCreationSystem,
      existingPosition: Point,
      mousePosition: Point,
      mouseState: MouseState,
      startingState: StartingState,
  ): Unit = {
    existingEntity withComponents {
      Position(existingPosition) &: Circle(startingState.startingRadius, startingState.startingColor) &: CNil
    }
    world.update(10)
    mouseState.coordinates = mousePosition
    world.addSystem(system)
    world.update(10)
  }
}
