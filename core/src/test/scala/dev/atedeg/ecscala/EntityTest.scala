package dev.atedeg.ecscala

import dev.atedeg.ecscala.fixtures.{ ComponentsFixture, Mass, Position, WorldFixture }
import dev.atedeg.ecscala.util.types.given
import org.scalatest.matchers.should.*
import org.scalatest.wordspec.AnyWordSpec

class EntityTest extends AnyWordSpec with Matchers {

  "An Entity" should {
    "have unique id" when {
      "generated from the world" in new WorldFixture {
        val entities = (0 until 1000) map { _ => world.createEntity() }
        entities shouldBe entities.distinct
      }
    }
    "remove its component correctly" when {
      def testComponentRemoval(removal: (Entity, Position) => Unit) = new WorldFixture {
        val entity = world.createEntity()
        val component = Position(1, 1)
        component.entity shouldBe empty
        entity addComponent component
        component.entity should contain(entity)
        removal(entity, component)
        component.entity shouldBe empty
      }
      "removing them by type" in {
        testComponentRemoval((entity, _) => entity.removeComponent[Position])
      }
      "removing them by reference" in new WorldFixture {
        testComponentRemoval(_ removeComponent _)
      }
    }
    "do nothing when removing components that do not belong to it" in new WorldFixture {
      val entity = world.createEntity()
      entity.removeComponent[Position]
      entity removeComponent Position(1, 1)
      world.getComponents[Position] shouldBe empty
    }
  }
}
