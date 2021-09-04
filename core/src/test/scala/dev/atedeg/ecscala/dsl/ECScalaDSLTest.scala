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
    "work the same way as the entity.addComponent() method" in new WorldFixture with ComponentsFixture {
      val entity1 = world hasAn entity withComponents {
        Position(1, 2) and Velocity(3, 4) and Gravity(9)
      }
      val entity2 = world hasAn entity withComponent Gravity(24)

      val world2 = World()
      val entity3 = world2 hasAn entity withComponents {
        Position(1, 2).and(Velocity(3, 4))
      }

      world.getComponents[Position] should contain(Map(entity1 -> Position(1, 2)))
      world.getComponents[Velocity] should contain(Map(entity1 -> Velocity(3, 4)))
      world.getComponents[Gravity] should contain(Map(entity1 -> Gravity(9), entity2 -> Gravity(24)))
      world2.getComponents[Position] should contain(Map(entity3 -> Position(1, 2)))
      world2.getComponents[Velocity] should contain(Map(entity3 -> Velocity(3, 4)))
    }
  }

  "The following syntax: myEntity - Component " should {
    "work the same way as the entity.removeComponent() method" in new WorldFixture with ComponentsFixture {
      val position = Position(1, 2)
      val entity1 = world hasAn entity

      entity1 + position
      world.getComponents[Position] should contain(Map(entity1 -> position))

      entity1 - position
      world.getComponents[Position] shouldBe empty
    }
  }

  "The following syntax: world hasA system[Component &: CNil] { () => {} }" should {
    "work the same way as the world.addSystem() method" in new ViewFixture {
      world hasA system[Position &: CNil] { (_, cl, _) =>
        {
          val Position(px, py) &: CNil = cl
          Position(px * 2, py * 2) &: CNil
        }
      }

      world hasA system[Position &: CNil]((_, cl, _) => {
        val Position(x, y) &: CNil = cl
        Position(x + 1, y + 1) &: CNil
      })

      world.update()

      world.getView[Position &: CNil] should contain theSameElementsAs List(
        (entity1, Position(3, 3) &: CNil),
        (entity3, Position(3, 3) &: CNil),
        (entity4, Position(3, 3) &: CNil),
      )
    }
  }
}
