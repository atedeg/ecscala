package dev.atedeg.ecscala

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.fixtures.{ ComponentsFixture, Mass, Position, WorldFixture }
import dev.atedeg.ecscala.util.types.given

class EntityTest extends AnyWordSpec with Matchers {

  "An Entity" should {
    "have unique id" when {
      "generated from the world" in new WorldFixture {
        val entities = (0 until 1000) map { _ => world.createEntity() }
        entities shouldBe entities.distinct
      }
    }
    "forbid having an already assigned component" in new WorldFixture {
      val component = Position(0, 0)
      val entity1 = world.createEntity()
      val entity2 = world.createEntity()
      entity1 setComponent component
      an[IllegalArgumentException] shouldBe thrownBy(entity2 setComponent component)
    }
    "get its components correctly" in new WorldFixture {
      val entity1 = world.createEntity()
      val entity2 = world.createEntity()
      val c1 = Position(1, 1)
      val c2 = Position(2, 2)
      entity1 setComponent c1
      entity2 setComponent c2
      entity1.getComponent[Position] shouldBe Some(c1)
      entity1.getComponent[Position] flatMap (_.entity) shouldBe Some(entity1)
      entity2.getComponent[Position] shouldBe Some(c2)
      entity2.getComponent[Position] flatMap (_.entity) shouldBe Some(entity2)
      entity1.getComponent[Mass] shouldBe None
    }
    "remove its component correctly" when {
      def testComponentRemoval(removal: (Entity, Position) => Unit) = new WorldFixture {
        val entity = world.createEntity()
        val component = Position(1, 1)
        component.entity shouldBe empty
        entity setComponent component
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
