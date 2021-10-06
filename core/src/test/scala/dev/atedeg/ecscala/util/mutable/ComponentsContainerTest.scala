package dev.atedeg.ecscala.util.mutable

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.fixtures.{ ComponentsFixture, Position, Velocity, WorldFixture }
import dev.atedeg.ecscala.util.mutable.ComponentsContainer
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.util.types.ComponentTag

class ComponentsContainerTest extends AnyWordSpec with Matchers {

  "A ComponentsContainer" when {
    "added an element" which {
      "was not present" should {
        "return it with apply" in new WorldFixture with ComponentsFixture {
          val entity = world.createEntity()
          componentsContainer += (entity -> Position(1, 2))

          componentsContainer[Position] should contain(Map(entity -> Position(1, 2)))
        }
      }
      "was present" should {
        "return the updated version" in new WorldFixture with ComponentsFixture {
          val entity = world.createEntity()
          componentsContainer += (entity -> Position(1, 2))
          componentsContainer += (entity -> Position(2, 3))

          componentsContainer[Position] should contain(Map(entity -> Position(2, 3)))
        }
      }
    }
    "added multiple entities with the same Component type" should {
      "return a map of all added entities" in new WorldFixture with ComponentsFixture {
        val entities = (0 until 100) map (_ => world.createEntity())
        val entityComponentPairs = entities map (_ -> Position(1, 2))
        entityComponentPairs foreach (componentsContainer += _)

        componentsContainer[Position] should contain(Map.from(entityComponentPairs))
      }
    }
    "removed a component" should {
      "no longer return it with apply" in new WorldFixture with ComponentsFixture {
        val entity1 = world.createEntity()
        val component1 = Position(1, 2)
        val entity2 = world.createEntity()
        componentsContainer += (entity1 -> component1)
        componentsContainer += (entity2 -> Position(2, 3))
        componentsContainer -= (entity1 -> component1)

        componentsContainer[Position] should contain(Map(entity2 -> Position(2, 3)))
      }
    }
    "removed the last component of a type" should {
      "no longer return the type's map with apply" in new WorldFixture with ComponentsFixture {
        val entity = world.createEntity()
        val component = Position(1, 2)
        componentsContainer += (entity -> component)
        componentsContainer -= (entity -> component)

        componentsContainer[Position] shouldBe empty
      }
    }
    "trying to remove an entity-component pair that does not exist" should {
      "not remove anything" in new WorldFixture with ComponentsFixture {
        val entity1 = world.createEntity()
        val component = Position(1, 2)
        val entity2 = world.createEntity()
        componentsContainer += entity1 -> component
        componentsContainer -= entity2 -> component

        componentsContainer[Position] should contain(Map(entity1 -> Position(1, 2)))
      }
    }
    "trying to remove a component" which {
      "is a case class" should {
        "remove it only if the references are the same" in new WorldFixture with ComponentsFixture {
          val entity = world.createEntity()
          componentsContainer += entity -> Position(1, 2)
          componentsContainer -= entity -> Position(1, 2)

          componentsContainer[Position] should contain(Map(entity -> Position(1, 2)))
        }
      }
    }
    "trying to remove a component of a type that was never added" should {
      "not remove anything" in new WorldFixture with ComponentsFixture {
        val entity = world.createEntity()
        componentsContainer += entity -> Position(1, 2)
        componentsContainer -= entity -> Velocity(1, 2)

        componentsContainer[Position] should contain(Map(entity -> Position(1, 2)))
        componentsContainer[Velocity] shouldBe empty
      }
    }
    "removing an entity" which {
      "has multiple components" should {
        "remove all its components" in new WorldFixture with ComponentsFixture {
          val entity = world.createEntity()
          componentsContainer += entity -> Position(1, 2)
          componentsContainer += entity -> Velocity(2, 3)
          componentsContainer -= entity

          componentsContainer[Position] shouldBe empty
          componentsContainer[Velocity] shouldBe empty
        }
        "not remove any other entities" in new WorldFixture with ComponentsFixture {
          val entity1 = world.createEntity()
          val entity2 = world.createEntity()
          componentsContainer += entity1 -> Position(1, 2)
          componentsContainer += entity1 -> Velocity(2, 3)
          componentsContainer += entity2 -> Position(1, 2)
          componentsContainer += entity2 -> Velocity(2, 3)
          componentsContainer -= entity1

          componentsContainer[Position] should contain(Map(entity2 -> Position(1, 2)))
          componentsContainer[Velocity] should contain(Map(entity2 -> Velocity(2, 3)))
        }
      }
      "was not added" should {
        "not remove anything" in new WorldFixture with ComponentsFixture {
          val entity1 = world.createEntity()
          val entity2 = world.createEntity()
          componentsContainer += entity1 -> Position(1, 2)
          componentsContainer += entity1 -> Velocity(2, 3)
          componentsContainer -= entity2

          componentsContainer[Position] should contain(Map(entity1 -> Position(1, 2)))
          componentsContainer[Velocity] should contain(Map(entity1 -> Velocity(2, 3)))
        }
      }
    }
  }
}
