package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.{ Component, Entity, World }
import dev.atedeg.ecscala.fixtures.{ ComponentsFixture, WorldFixture }
import dev.atedeg.ecscala.util.types.TypeTag
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.util.types
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.dsl.EcscalaDSL

class EcscalaDSLTest extends AnyWordSpec with Matchers with EcscalaDSL {

  "The dsl syntax to add components to an entity" should {
    "work the same way as the addComponent() method" in new WorldFixture with ComponentsFixture {
      val entity1 =
        world hasAn entity withComponent Position(1, 2) withComponent Velocity(2, 4) withComponent Gravity(9.8)

      world.getComponents[Position] should contain(Map(entity1 -> Position(1, 2)))
      world.getComponents[Velocity] should contain(Map(entity1 -> Velocity(2, 4)))
      world.getComponents[Gravity] should contain(Map(entity1 -> Gravity(9.8)))
    }

    "work the same way as the removeComponent() method" in new WorldFixture with ComponentsFixture {
      val position = Position(1, 2)
      val entity1 =
        world hasAn entity withComponent position withComponent Velocity(2, 4) withComponent Gravity(9.8)
      entity1 - position

      world.getComponents[Position] should not contain (Map(entity1 -> position))
    }
  }

}
