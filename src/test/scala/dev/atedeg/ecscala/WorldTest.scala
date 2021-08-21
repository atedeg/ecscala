package dev.atedeg.ecscala

import dev.atedeg.ecscala.fixtures.{ComponentsFixture, WorldFixture}
import dev.atedeg.ecscala.util.types.TypeTag
import org.scalatest.matchers.should.*
import org.scalatest.wordspec.AnyWordSpec

class WorldTest extends AnyWordSpec with Matchers {
  "A World" when {
    "empty" should {
      "have size 0" in new WorldFixture {
        world.entitiesCount shouldBe 0
      }
      "have size 1" when {
        "an entity is added" in new WorldFixture {
          world.createEntity()
          world.entitiesCount shouldBe 1
        }
      }
    }
    "has 1 entity" should {
      "have size 0" when {
        "an entity is removed" in new WorldFixture {
          val entity = world.createEntity()
          world.removeEntity(entity)
          world.entitiesCount shouldBe 0
        }
      }
    }
    "has entities with components" should {
      "return the components" in new WorldFixture with ComponentsFixture {
        import dev.atedeg.ecscala.util.types.{given TypeTag[_]}
        val entity = world.createEntity()
        entity addComponent Position(1, 1)
//        world.getComponents[Position] should contain(Map(entity -> Position(1, 1)))
      }
    }
  }
}
