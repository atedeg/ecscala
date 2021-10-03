package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.{CNil, Entity, World}
import dev.atedeg.ecscalademo.*
import dev.atedeg.ecscalademo.fixtures.BallCreationSystemFixture
import dev.atedeg.ecscalademo.util.{ AnyValue, checkAllStates }
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock

class BallCreationSystemTest extends AnyWordSpec with Matchers with ECScalaDSL {

  "A BallCreationSystem" should {
    "run" when {
      "in an enabled state" in
        checkAllStates(BallCreationSystem(_, _, mock[StartingState]))(
          (State.AddBalls, true, AnyValue, AnyValue)
        )
    }
  }

  "A BallCreationSystem" when {
    "enabled" should {
      "create a ball in a free position" in new BallCreationSystemFixture {
        enableSystemCondition(playState, mouseState)
        simulateCreateBall(
          world,
          entity1,
          ballCreationSystem,
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
          ballCreationSystem,
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
          ballCreationSystem,
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

  private def simulateCreateBall(
      world: World,
      existingEntity: Entity,
      ballCreationSystem: BallCreationSystem,
      existingPosition: Point,
      mousePosition: Point,
      mouseState: MouseState,
      startingState: StartingState,
  ): Unit = {
    existingEntity withComponents {
      Position(existingPosition) &: Circle(startingState.startingRadius, startingState.startingColor)
    }
    mouseState.coordinates = mousePosition
    world hasA system(ballCreationSystem)
    world.update(10)
  }
}
