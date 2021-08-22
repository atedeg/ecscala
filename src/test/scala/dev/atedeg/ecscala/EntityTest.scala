package dev.atedeg.ecscala

import dev.atedeg.ecscala.fixtures.{ComponentsFixture, WorldFixture}
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
  "An Entity" when {
    "added handlers" should {
      "execute all of them when a component is added" in new WorldFixture with ComponentsFixture {
        var n = 0
        var entity = world.createEntity()
        (0 until 100) foreach { _ => entity.onAddedComponent(_ => n += 1) }
        entity addComponent Position(1, 1)
        n shouldBe 100
      }
    }
  }
}
