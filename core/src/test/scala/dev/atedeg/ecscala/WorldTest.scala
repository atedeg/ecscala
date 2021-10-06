package dev.atedeg.ecscala

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.fixtures.{ ComponentsFixture, Position, ViewFixture, WorldFixture }
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.util.types.ComponentTag

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
      "remove all its components" when {
        "it is cleared" in new WorldFixture {
          val entity = world.createEntity()
          val position = Position(1, 1)
          entity.setComponent(position)
          world.clearEntities()
          position.entity shouldBe empty
          entity.getComponent[Position] shouldBe empty
        }
      }
      "have size 0" when {
        "an entity is removed" in new WorldFixture {
          val entity = world.createEntity()
          world.removeEntity(entity)
          world.entitiesCount shouldBe 0
        }
      }
      "not have a component of a removed entity" in new WorldFixture {
        val entity = world.createEntity()
        val entity1 = world.createEntity()
        entity setComponent Position(1, 2)
        entity1 setComponent Position(1, 2)
        world.removeEntity(entity)

        world.getComponents[Position] should contain(Map(entity1 -> Position(1, 2)))
      }
    }
    "has 3 entities" should {
      "have size 0" when {
        "all entities are removed" in new WorldFixture {
          val entity = world.createEntity()
          val entity1 = world.createEntity()
          val entity2 = world.createEntity()

          world.clearEntities()

          world.entitiesCount shouldBe 0
        }
      }
      "not have the components from the removed entities" in new WorldFixture {
        val entity = world.createEntity()
        val entity1 = world.createEntity()
        entity setComponent Position(1, 2)
        entity1 setComponent Position(3, 4)

        world.clearEntities()

        world.getComponents[Position] shouldBe empty
      }
    }
    "has entities with components" should {
      "return the components" in new WorldFixture with ComponentsFixture {
        val entity = world.createEntity()
        entity setComponent Position(1, 1)
        world.getComponents[Position] should contain(Map(entity -> Position(1, 1)))
      }
    }
    "components are removed from its enities" should {
      "not return the components" in new WorldFixture with ComponentsFixture {
        val entity = world.createEntity()
        val component = Position(1, 1)
        entity setComponent component
        entity removeComponent component
        entity setComponent Position(1, 1)
        entity.removeComponent[Position]

        world.getComponents[Position] shouldBe empty
      }
    }
    "update is called" should {
      "execute all systems in the same order as they were added" in new ViewFixture {
        world.addSystem(IteratingSystem[Position &: CNil]((_, comps, _) => {
          val Position(px, py) &: CNil = comps
          Position(px * 2, py * 2) &: CNil
        }))

        world.addSystem(IteratingSystem[Position &: CNil]((_, comps, _) => {
          val Position(x, y) &: CNil = comps
          Position(x + 1, y + 1) &: CNil
        }))

        world.update(10)

        world.getView[Position &: CNil] should contain theSameElementsAs List(
          (entity1, Position(3, 3) &: CNil),
          (entity3, Position(3, 3) &: CNil),
          (entity4, Position(3, 3) &: CNil),
          (entity5, Position(3, 3) &: CNil),
        )
      }
    }
    "a System is removed" should {
      "not execute its update" in new WorldFixture {
        val entity = world.createEntity()
        entity setComponent Position(1, 1)
        val system = SystemBuilder[Position &: CNil].withBefore { (_, _, _) => () }.withAfter { (_, _, _) =>
          ()
        }.withUpdate { (_, c, _) =>
          val Position(x, y) &: CNil = c
          Position(x + 1, y + 1) &: CNil
        }

        world.addSystem(system)
        world.removeSystem(system)
        world.update(10)

        world.getView[Position &: CNil].toList shouldBe List((entity, Position(1, 1) &: CNil))
      }
    }
  }
}
