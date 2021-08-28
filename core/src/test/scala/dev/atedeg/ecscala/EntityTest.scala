package dev.atedeg.ecscala

import dev.atedeg.ecscala.fixtures.{ ComponentsFixture, WorldFixture }
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
  }
}
