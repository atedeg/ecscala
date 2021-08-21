package dev.atedeg.ecscala

import dev.atedeg.ecscala.fixtures.{ComponentsFixture, WorldFixture}
import dev.atedeg.ecscala.util.types.TypeTag
import dev.atedeg.ecscala.util.types.{given TypeTag[_]}
import dev.atedeg.ecscala.util.immutable.ComponentsContainer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ComponentsContainerTest extends AnyWordSpec with Matchers {
  "A ComponentsContainer" when {
    "added an element" which {
      "was not present" should {
        "return it with apply" in new WorldFixture with ComponentsFixture {
          var container = ComponentsContainer()
          val entity = world.createEntity()
          container += (entity -> Position(1, 2))
          container[Position] match {
            case Some(map) => map shouldEqual Map(entity -> Position(1, 2))
            case None      => fail()
          }
        }
      }
      "was present" should {
        "return the updated version" in new WorldFixture with ComponentsFixture {
          var container = ComponentsContainer()
          val entity = world.createEntity()
          container += (entity -> Position(1, 2))
          container += (entity -> Position(2, 3))
          container[Position] match {
            case Some(map) => map shouldEqual Map(entity -> Position(2, 3))
            case None      => fail()
          }
        }
      }
    }
    "added multiple entities with the same Component type" should {
      "return a map of all added entities" in new WorldFixture with ComponentsFixture {
        var container = ComponentsContainer()
        val entities = (0 until 100) map { _ => world.createEntity() }
        val entityComponentPairs = entities map (_ -> Position(1, 2))
        entityComponentPairs foreach (container += _)
        container[Position] match {
          case Some(map) => map shouldEqual Map.from(entityComponentPairs)
          case None      => fail()
        }
      }
    }
    "removed a component" should {
      "no longer return it with apply" in new WorldFixture with ComponentsFixture {
        var container = ComponentsContainer()
        val entity1 = world.createEntity()
        val component1 = Position(1, 2)
        val entity2 = world.createEntity()
        val component2 = Position(2, 3)

        container += (entity1 -> component1)
        container += (entity2 -> component2)
        container -= (entity1 -> component1)
        container[Position] match {
          case Some(map) => map shouldEqual Map(entity2 -> component2)
          case None      => fail()
        }
      }
    }
    "removed the last component of a type" should {
      "no longer return a map with apply" in new WorldFixture with ComponentsFixture {
        var container = ComponentsContainer()
        val entity = world.createEntity()
        val component = Position(1, 2)
        container += (entity -> component)
        container -= (entity -> component)
        container[Position] match {
          case Some(_) => fail()
          case None    => ()
        }
      }
    }
    "trying to remove an entity-component pair that does not exist" should {
      "not remove anything" in new WorldFixture with ComponentsFixture {
        var container = ComponentsContainer()
        val entity = world.createEntity()
        container += entity -> Position(1, 2)
        container -= entity -> Position(1, 2)
        container[Position] match {
          case Some(map) => map shouldEqual Map(entity -> Position(1, 2))
          case None      => fail()
        }
      }
    }
    "trying to remove a component of a type that was never added" should {
      "not remove anything" in new WorldFixture with ComponentsFixture {
        var container = ComponentsContainer()
        val entity = world.createEntity()
        container += entity -> Position(1, 2)
        container -= entity -> Velocity(1, 2)
        container[Position] match {
          case Some(map) => map shouldEqual Map(entity -> Position(1, 2))
          case None      => fail()
        }
        container[Velocity] match {
          case Some(_) => fail()
          case None    => ()
        }
      }
    }
    "removing an entity" which {
      "has multiple components" should {
        "remove all its components" in new WorldFixture with ComponentsFixture {
          var container = ComponentsContainer()
          val entity = world.createEntity()
          container += entity -> Position(1, 2)
          container += entity -> Velocity(2, 3)
          container = container.removeEntity(entity)
          container[Position] match {
            case Some(m) => fail(m.toString)
            case None    => ()
          }
          container[Velocity] match {
            case Some(_) => fail()
            case None    => ()
          }
        }
        "not remove any other entities" in new WorldFixture with ComponentsFixture {
          var container = ComponentsContainer()
          val entity1 = world.createEntity()
          val entity2 = world.createEntity()
          container += entity1 -> Position(1, 2)
          container += entity1 -> Velocity(2, 3)
          container += entity2 -> Position(1, 2)
          container += entity2 -> Velocity(2, 3)
          container -= entity1
          container[Position] match {
            case Some(map) => map shouldEqual Map(entity2 -> Position(1, 2))
            case None      => fail()
          }
          container[Velocity] match {
            case Some(map) => map shouldEqual Map(entity2 -> Velocity(2, 3))
            case None      => fail()
          }
        }
      }
      "is was not added" should {
        "not remove anything" in new WorldFixture with ComponentsFixture {
          var container = ComponentsContainer()
          val entity1 = world.createEntity()
          val entity2 = world.createEntity()
          container += entity1 -> Position(1, 2)
          container += entity1 -> Velocity(2, 3)
          container -= entity2
          container[Position] match {
            case Some(map) => map shouldEqual Map(entity1 -> Position(1, 2))
            case None      => fail()
          }
          container[Velocity] match {
            case Some(map) => map shouldEqual Map(entity1 -> Velocity(2, 3))
            case None      => fail()
          }
        }
      }
    }
  }
}
