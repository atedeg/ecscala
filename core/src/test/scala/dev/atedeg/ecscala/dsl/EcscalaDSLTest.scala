package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.{ Component, Entity, World }
import dev.atedeg.ecscala.fixtures.{ ComponentsFixture, Gravity, Position, Velocity, WorldFixture }
import dev.atedeg.ecscala.util.types.TypeTag
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.dsl.ECScalaDSL

class ECScalaDSLTest extends AnyWordSpec with Matchers with ECScalaDSL {

  "The dsl syntax" should {
    "work the same way as the entity.addComponent() method" in new WorldFixture with ComponentsFixture {
      val entity1 = world hasAn entity withComponents {
        Position(1, 2) and Velocity(3, 4) and Gravity(9)
      }
      val entity2 = world hasAn entity withComponent Gravity(24)

      val world2 = World()
      val entity3 = world2 hasAn entity withComponents {
        Position(1, 2) and Velocity(3, 4)
      }

      world.getComponents[Position] should contain(Map(entity1 -> Position(1, 2)))
      world.getComponents[Velocity] should contain(Map(entity1 -> Velocity(3, 4)))
      world.getComponents[Gravity] should contain(Map(entity1 -> Gravity(9), entity2 -> Gravity(24)))
      world2.getComponents[Position] should contain(Map(entity3 -> Position(1, 2)))
      world2.getComponents[Velocity] should contain(Map(entity3 -> Velocity(3, 4)))
    }

    "work the same way as the entity.removeComponent() method" in new WorldFixture with ComponentsFixture {
      val position = Position(1, 2)
      val entity1 = world hasAn entity

      entity1 + position
      world.getComponents[Position] should contain(Map(entity1 -> position))

      entity1 - position
      world.getComponents[Position] shouldBe empty
    }
  }
}
