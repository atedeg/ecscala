package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.{ &:, CNil, View }
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo
import dev.atedeg.ecscalademo.{ PlayState, Point, Position, Vector, Velocity }
import dev.atedeg.ecscalademo.fixtures.{ SystemsFixture, WorldFixture }
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.util.types.given

import scala.language.implicitConversions

class FrictionSystemTest extends AnyWordSpec with Matchers with ECScalaDSL {

  "A FrictionSystem" when {
    "the simulation is playing" should {
      "update a ball's Velocity considering the friction" in new WorldFixture with SystemsFixture {
        PlayState.playing = true
        val initialVelocity = Velocity(300, 0)
        val ball = world hasAn entity withComponents {
          initialVelocity
        }
        world hasA system(frictionSystem)

        (0 to 2) foreach { _ => world.update(10) }
        val view: View[Velocity &: CNil] = getView[Velocity &: CNil] from world
        val Velocity(vector) &: CNil = view.head._2

        vector.x should be < initialVelocity.velocity.x
      }
      "don't update the component's Velocity if its initial Velocity is 0" in new WorldFixture with SystemsFixture {
        PlayState.playing = true
        val ball = world hasAn entity withComponent Velocity(0, 0)
        world hasA system(frictionSystem)

        world.update(10)

        getView[Velocity &: CNil] from world should contain theSameElementsAs List(
          (ball, Velocity(0, 0) &: CNil),
        )
      }
    }
    "the simulation is not playing" should {
      "not update the components" in new WorldFixture with SystemsFixture {
        PlayState.playing = false
        val ball = world hasAn entity withComponent Velocity(300, 0)
        world hasA system(frictionSystem)

        world.update(10)

        getView[Velocity &: CNil] from world should contain theSameElementsAs List(
          (ball, Velocity(300, 0) &: CNil),
        )
      }
    }
  }
}
