package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.{ &:, CNil, View }
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo
import dev.atedeg.ecscalademo.{ PlayState, Point, Position, State, Vector, Velocity }
import dev.atedeg.ecscalademo.fixtures.{ FrictionSystemFixture, SystemsFixture, WorldFixture }
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.util.types.given
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar

import scala.language.implicitConversions

class FrictionSystemTest extends AnyWordSpec with Matchers with ECScalaDSL with MockitoSugar {

  "A FrictionSystem" when {
    "the simulation is playing" should {
      "update a ball's Velocity considering the friction" in new FrictionSystemFixture {
        playState.gameState = State.Play
        val initialVelocity = Velocity(300, 0)
        val ball = world hasAn entity withComponent initialVelocity

        (0 to 2) foreach { _ => world.update(10) }
        val view: View[Velocity &: CNil] = getView[Velocity &: CNil] from world
        val Velocity(vector) &: CNil = view.head._2

        vector.x should be < initialVelocity.velocity.x
      }
      "don't update the component's Velocity if its initial Velocity is 0" in new FrictionSystemFixture {
        playState.gameState = State.Play
        val ball = world hasAn entity withComponent Velocity(0, 0)

        world.update(10)

        getView[Velocity &: CNil] from world should contain theSameElementsAs List(
          (ball, Velocity(0, 0) &: CNil),
        )
      }
    }
    "the simulation is not playing" should {
      "not update the components" in new FrictionSystemFixture {
        playState.gameState = State.Pause
        val ball = world hasAn entity withComponent Velocity(300, 0)

        world.update(10)

        getView[Velocity &: CNil] from world should contain theSameElementsAs List(
          (ball, Velocity(300, 0) &: CNil),
        )
      }
    }
  }
}
