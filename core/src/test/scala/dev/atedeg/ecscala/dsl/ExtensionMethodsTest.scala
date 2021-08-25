package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.{ Component, Entity, World }
import dev.atedeg.ecscala.fixtures.{ ComponentsFixture, WorldFixture }
import dev.atedeg.ecscala.util.types.TypeTag
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.util.types

class ExtensionMethodsTest extends AnyWordSpec with Matchers {

  "The dsl syntax to add components to an entity" should {
    "work the same way as the addComponent() method" in new WorldFixture with ComponentsFixture {
      import dev.atedeg.ecscala.util.types.given
      var n = 0
      val entity = world.createEntity()
      (0 until 100) foreach { _ => entity.onAddedComponent(_ => n += 1) }
      entity withComponents { Seq(Position(1, 2), Velocity(2, 3)) }
      n shouldBe 200
    }
  }
}
