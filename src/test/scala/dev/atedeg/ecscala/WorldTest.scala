package dev.atedeg.ecscala

import org.scalatest.matchers.should.*
import org.scalatest.wordspec.AnyWordSpec

class WorldTest extends AnyWordSpec with Matchers {
  "A World" when {
    "empty" should {
      "have size 0" in {
        World().size shouldBe 0
      }
      "have size 1" when {
        "an entity is added" in {
          val world = World()
          world.createEntity()
          world.size shouldBe 1
        }
      }
    }
    "has 1 entity" should {
      "have size 0" when {
        "an entity is removed" in {
          val world = World()
          val entity = world.createEntity()
          world.removeEntity(entity)
          world.size shouldBe 0
        }
      }
    }
  }
}
