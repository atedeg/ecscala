package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.{&:, CNil, View}
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.given
import dev.atedeg.ecscalademo.{EnvironmentState, PlayState, Point, Position, State, Vector, Velocity}
import dev.atedeg.ecscalademo.fixtures.{FrictionSystemFixture, WorldFixture}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.util.{AnyValue, checkAllStates}
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar

import scala.language.implicitConversions

class FrictionSystemTest extends AnyWordSpec with Matchers with ECScalaDSL with MockitoSugar {

  "A FrictionSystem" should {
    "run" when {
      "in an enabled state" in
        checkAllStates((playState, _) => FrictionSystem(playState, mock[EnvironmentState]))(
          (State.Play, AnyValue, AnyValue, AnyValue),
        )
    }
  }

  "A FrictionSystem" when {
    "the simulation is playing" should {
      "update a ball's Velocity considering the friction" in new FrictionSystemFixture {
        playState.gameState = State.Play
        (0 to 2) foreach { _ => world.update(10) }
        val view: View[Velocity &: CNil] = getView[Velocity &: CNil] from world
        val Velocity(vector) &: CNil = view.head._2
        vector.x should be < initialVelocity.velocity.x
      }
      "not update the component's Velocity if its initial Velocity is 0" in new FrictionSystemFixture {
        ball addComponent Velocity(0, 0)
        playState.gameState = State.Play
        world.update(10)
        getView[Velocity &: CNil] from world should contain theSameElementsAs List((ball, Velocity(0, 0) &: CNil))
      }
    }
    "the simulation is not playing" should {
      "not update the components" in new FrictionSystemFixture {
        playState.gameState = State.Pause
        world.update(10)
        getView[Velocity &: CNil] from world should contain theSameElementsAs List((ball, Velocity(300, 0) &: CNil))
      }
    }
  }
}
