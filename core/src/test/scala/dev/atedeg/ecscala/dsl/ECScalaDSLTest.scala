package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.{ &:, fixtures, CNil, Component, Entity, World }
import dev.atedeg.ecscala.fixtures.{ ComponentsFixture, Gravity, Position, Velocity, ViewFixture, WorldFixture }
import dev.atedeg.ecscala.util.types.ComponentTag
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.dsl.ECScalaDSL

class ECScalaDSLTest extends AnyWordSpec with Matchers with ECScalaDSL {

  "The following syntax: world hasAn entity withComponents { Component 1 and Component2 }" should {
    "work the same way as the world.createEntity() and entity.addComponent() methods" in new WorldFixture
      with ComponentsFixture {
      val entity1 = world hasAn entity withComponents {
        Position(1, 2) and Velocity(3, 4) and Gravity(9)
      }
      val entity2 = world hasAn entity withComponent Gravity(24)

      val world2 = World()
      val entity3 = world2 hasAn entity withComponents {
        Position(1, 2) and Velocity(3, 4)
      }

      world.getComponents[Position] should contain(Map(entity1 -> Position(1, 2)))
      world.getComponents[Velocity] should contain(Map(entity1 -> Velocity(3, 4)))
      world.getComponents[Gravity] should contain(Map(entity1 -> Gravity(9), entity2 -> Gravity(24)))
      world2.getComponents[Position] should contain(Map(entity3 -> Position(1, 2)))
      world2.getComponents[Velocity] should contain(Map(entity3 -> Velocity(3, 4)))
    }
  }

  "The following syntax: myEntity - Component" should {
    "work the same way as the entity.removeComponent() method" in new WorldFixture with ComponentsFixture {
      val position = Position(1, 2)
      val velocity = Velocity(3, 4)
      val entity1 = world hasAn entity

      entity1 + position
      world.getComponents[Position] should contain(Map(entity1 -> position))

      entity1 - position
      world.getComponents[Position] shouldBe empty
    }
  }

  "The following syntax: remove (Component) from world" should {
    "work the same way as the entity.removeComponent() method as well" in new WorldFixture with ComponentsFixture {
      val position = Position(1, 2)
      val velocity = Velocity(3, 4)
      val entity1 = world hasAn entity

      entity1 withComponents { position and velocity }
      
      world.getComponents[Position] should contain(Map(entity1 -> position))
      world.getComponents[Velocity] should contain(Map(entity1 -> velocity))

      remove { position and velocity } from entity1
      
      world.getComponents[Position] shouldBe empty
      world.getComponents[Velocity] shouldBe empty

      entity1 withComponents { position and velocity }
      remove(position) from entity1

      world.getComponents[Velocity] should contain(Map(entity1 -> velocity))

    }
  }

  "The following syntax world - myEntity" should {
    "work the same way as the world.removeEntity() method" in new WorldFixture {
      val entity1 = world hasAn entity
      world - entity1
      world.entitiesCount shouldBe 0
    }
  }

  "The following syntax: remove (entity1, entity2) from world" should {
    "work the same way ad the world.removeEntity() method" in new WorldFixture {
      val entity1 = world hasAn entity
      val entity2 = world hasAn entity

      remove(entity1) from world
      world.entitiesCount shouldBe 1

      val entity3 = world hasAn entity
      val entity4 = world hasAn entity

      remove(entity2, entity3, entity4) from world
      world.entitiesCount shouldBe 0
    }
  }

  "The following syntax: world hasA system[Component &: CNil] { () => {} }" should {
    "work the same way as the world.addSystem() method" in new ViewFixture {
      world hasA system[Position &: CNil] { (_, cl, _, _, _) =>
        {
          val Position(px, py) &: CNil = cl
          Position(px * 2, py * 2) &: CNil
        }
      }
      world hasA system[Position &: CNil]((_, cl, _, _, _) => {
        val Position(x, y) &: CNil = cl
        Position(x + 1, y + 1) &: CNil
      })

      world.update(10)

      world.getView[Position &: CNil] should contain theSameElementsAs List(
        (entity1, Position(3, 3) &: CNil),
        (entity3, Position(3, 3) &: CNil),
        (entity4, Position(3, 3) &: CNil),
      )
    }
  }

  "The following syntax: getView[Position &: CNil] from world" should {
    "work the same way as the world.getView[Position &: CNil] method" in new ViewFixture with WorldFixture {
      val view = getView[Position &: Velocity &: CNil] from world

      view should contain theSameElementsAs List(
        (entity1, Position(1, 1) &: Velocity(1, 1) &: CNil),
        (entity3, Position(1, 1) &: Velocity(1, 1) &: CNil),
        (entity4, Position(1, 1) &: Velocity(1, 1) &: CNil),
      )
    }
  }
}
