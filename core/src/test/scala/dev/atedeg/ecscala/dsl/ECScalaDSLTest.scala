package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala
import dev.atedeg.ecscala.{
  &:,
  fixtures,
  CNil,
  Component,
  Deletable,
  DeltaTime,
  Entity,
  System,
  SystemBuilder,
  View,
  World,
}
import dev.atedeg.ecscala.fixtures.{
  ComponentsFixture,
  Gravity,
  Mass,
  Position,
  SystemBuilderFixture,
  SystemFixture,
  Velocity,
  ViewFixture,
  WorldFixture,
}
import dev.atedeg.ecscala.util.types.ComponentTag
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.dsl.ECScalaDSL

import scala.language.implicitConversions

class ECScalaDSLTest extends AnyWordSpec with Matchers with ECScalaDSL {

  "world hasAn entity withComponents { Component1 &: Component2 }" should {
    "work the same way as the world.createEntity() &: entity.addComponent() methods" in new WorldFixture
      with ComponentsFixture {
      val entity1 = world hasAn entity withComponents { Position(1, 2) &: Velocity(3, 4) &: Gravity(9) }
      val entity2 = world hasAn entity withComponent Gravity(24)

      val world2 = World()
      val entity3 = world2 hasAn entity withComponents {
        Position(1, 2) &: Velocity(3, 4)
      }

      world.getComponents[Position] should contain(Map(entity1 -> Position(1, 2)))
      world.getComponents[Velocity] should contain(Map(entity1 -> Velocity(3, 4)))
      world.getComponents[Gravity] should contain(Map(entity1 -> Gravity(9), entity2 -> Gravity(24)))
      world2.getComponents[Position] should contain(Map(entity3 -> Position(1, 2)))
      world2.getComponents[Velocity] should contain(Map(entity3 -> Velocity(3, 4)))
    }
  }

  "myEntity -= myComponent" should {
    "work the same way as the entity.removeComponent() method" in new WorldFixture with ComponentsFixture {
      val position = Position(1, 2)
      val velocity = Velocity(3, 4)
      val entity1 = world hasAn entity

      entity1 += position
      world.getComponents[Position] should contain(Map(entity1 -> position))

      entity1 -= position
      world.getComponents[Position] shouldBe empty
    }
  }

  "remove (myComponent) from world" should {
    "work the same way as the entity.removeComponent() method" in new WorldFixture with ComponentsFixture {
      val position = Position(1, 2)
      val velocity = Velocity(3, 4)
      val entity1 = world hasAn entity

      entity1 withComponents { position &: velocity }

      remove { position } from entity1

      world.getComponents[Velocity] should contain(Map(entity1 -> velocity))
    }
  }

  "remove[Component] from world" should {
    "work the same way as the entity.removeComponent() method" in new WorldFixture with ComponentsFixture {
      val entity1 = world hasAn entity withComponents { Position(1, 2) &: Velocity(3, 4) }
      remove[Position] from entity1

      world.getComponents[Position] shouldBe empty
      world.getComponents[Velocity] should contain(Map(entity1 -> Velocity(3, 4)))
    }
  }

  "remove { myComponent1 &: myComponent2 } from world" should {
    "work the same way as multiple entity.removeComponent() method calls" in new WorldFixture with ComponentsFixture {
      val position = Position(1, 2)
      val velocity = Velocity(3, 4)
      val entity1 = world hasAn entity

      entity1 withComponents { position &: velocity }
      remove { position &: velocity } from entity1

      world.getComponents[Position] shouldBe empty
      world.getComponents[Velocity] shouldBe empty
    }
  }

  "remove[Component1 &: Component2 &: CNil] from world" should {
    "work the same way as multiple entity.removeComponent() method calls" in new WorldFixture with ComponentsFixture {
      val entity1 = world hasAn entity withComponents { Position(1, 2) &: Velocity(3, 4) }
      remove[Position &: Velocity &: CNil] from entity1

      world.getComponents[Position] shouldBe empty
      world.getComponents[Velocity] shouldBe empty
    }
  }

  "world -= myEntity" should {
    "work the same way as the world.removeEntity() method" in new WorldFixture {
      val entity1 = world hasAn entity
      world -= entity1
      world.entitiesCount shouldBe 0
    }
  }

  "remove (Seq(entity1, entity2)) from world" should {
    "work the same way as the world.removeEntity() method" in new WorldFixture {
      val entity1 = world hasAn entity
      val entity2 = world hasAn entity

      remove { entity1 } from world
      world.entitiesCount shouldBe 1

      val entity3 = world hasAn entity
      val entity4 = world hasAn entity

      remove { List(entity2, entity3, entity4) } from world
      world.entitiesCount shouldBe 0
    }
  }

  "world hasA system[Component &: CNil] { () => {} }" should {
    "work the same way as the world.addSystem() method" in new ViewFixture {
      world hasA system[Position &: CNil](System((_, comps, _) => {
        val Position(px, py) &: CNil = comps
        Position(px * 2, py * 2) &: CNil
      }))

      world hasA system[Position &: CNil](System((_, comps, _) => {
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

  def testAddSystem(world: World): Unit = {
    val entity1 = world hasAn entity withComponent Position(1, 1)
    world.update(10)
    world.getView[Position &: CNil] should contain theSameElementsAs List(
      (entity1, Position(4, 4) &: CNil),
    )
  }

  "world hasA system(mySystem)" should {
    "work the same way as the world.addSystem() method" in new SystemFixture with WorldFixture {
      world hasA system(mySystem1)
      testAddSystem(world)
    }
  }

  "world += mySystem" should {
    "work the same way as the world.addSystem() method" in new SystemFixture with WorldFixture {
      world += mySystem1
      testAddSystem(world)
    }
  }

  def testRemoveSystem(world: World): Unit = {
    val entity1 = world hasAn entity withComponent Position(1, 1)
    world.update(10)
    world.getView[Position &: CNil].toList shouldBe List((entity1, Position(1, 1) &: CNil))
  }

  "remove (mySystem) from world" should {
    "work the same way as the world.removeSystem method" in new SystemFixture with WorldFixture {
      world hasA system(mySystem2)
      remove(mySystem2) from world
      testRemoveSystem(world)
    }
  }

  "world -= mySystem" should {
    "work the same way as the world.addSystem() method" in new SystemFixture with WorldFixture {
      world hasA system(mySystem2)
      world -= mySystem2
      testRemoveSystem(world)
    }
  }

  "getView[Position &: CNil] from world" should {
    "work the same way as the world.getView[Position &: CNil] method" in new ViewFixture {
      val view = getView[Position &: Velocity &: CNil] from world

      view should contain theSameElementsAs List(
        (entity1, Position(1, 1) &: Velocity(1, 1) &: CNil),
        (entity3, Position(1, 1) &: Velocity(1, 1) &: CNil),
        (entity4, Position(1, 1) &: Velocity(1, 1) &: CNil),
      )
    }
  }

  "geView[Position &: CNil].excluding[Velocity &: CNil] from world" should {
    "work the same way as the world.getView[]" in new ViewFixture {
      val view = getView[Position &: Velocity &: CNil].excluding[Mass &: CNil] from world

      view should contain theSameElementsAs List(
        (entity1, Position(1, 1) &: Velocity(1, 1) &: CNil),
        (entity4, Position(1, 1) &: Velocity(1, 1) &: CNil),
      )

      val view2 = getView[Velocity &: CNil].excluding[Position &: CNil] from world
      view2 shouldBe empty
    }
  }

  "clearAllEntities from world" should {
    "work the same way as the world.clearEntities method" in new WorldFixture {
      val entity1 = world hasAn entity withComponent Position(1, 2)
      val entity2 = world hasAn entity withComponent Position(3, 4)

      clearAllEntities from world

      world.entitiesCount shouldBe 0
      world.getComponents[Position] shouldBe empty
    }
  }
}
