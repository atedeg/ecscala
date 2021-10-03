package dev.atedeg.ecscala

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.fixtures.{ ComponentsFixture, Gravity, Mass, Position, Velocity, ViewFixture, WorldFixture }
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ViewTest extends AnyWordSpec with Matchers {

  "A view" should {
    "be empty" when {
      "defined with a CNil components list" in new ViewFixture {
        world.getView[CNil] shouldBe empty
      }
    }
    "iterate over the correct entities" in new ViewFixture {
      world.getView[Mass &: CNil] should contain theSameElementsAs List(
        (entity2, Mass(1) &: CNil),
        (entity3, Mass(1) &: CNil),
        (entity5, Mass(1) &: CNil),
      )
      world.getView[Position &: CNil] should contain theSameElementsAs List(
        (entity1, Position(1, 1) &: CNil),
        (entity3, Position(1, 1) &: CNil),
        (entity4, Position(1, 1) &: CNil),
        (entity5, Position(1, 1) &: CNil),
      )
      world.getView[Velocity &: CNil] should contain theSameElementsAs List(
        (entity1, Velocity(1, 1) &: CNil),
        (entity3, Velocity(1, 1) &: CNil),
        (entity4, Velocity(1, 1) &: CNil),
      )
      world.getView[Velocity &: Mass &: CNil] should contain theSameElementsAs List(
        (entity3, Velocity(1, 1) &: Mass(1)),
      )
      world.getView[Position &: Mass &: CNil] should contain theSameElementsAs List(
        (entity3, Position(1, 1) &: Mass(1) &: CNil),
        (entity5, Position(1, 1) &: Mass(1) &: CNil),
      )
      world.getView[Position &: Velocity &: CNil] should contain theSameElementsAs List(
        (entity1, Position(1, 1) &: Velocity(1, 1) &: CNil),
        (entity3, Position(1, 1) &: Velocity(1, 1) &: CNil),
        (entity4, Position(1, 1) &: Velocity(1, 1) &: CNil),
      )
      world.getView[Position &: Mass &: Velocity &: CNil] should contain theSameElementsAs List(
        (entity3, Position(1, 1) &: Mass(1) &: Velocity(1, 1)),
      )
      world.getView[Gravity &: CNil] shouldBe empty
    }
    "be commutative" in new ViewFixture {
      (world.getView[Mass &: Velocity &: CNil] map (_.head)) should contain theSameElementsAs
        (world.getView[Velocity &: Mass &: CNil] map (_.head))
    }
    "allow to change the entities and reflect the changes on successive iteration" in new ViewFixture {
      val view = world.getView[Velocity &: Mass &: CNil]
      view foreach (_.head setComponent Mass(11))
      view should contain theSameElementsAs List((entity3, Velocity(1, 1) &: Mass(11)))
    }
  }

  "An excluding view" should {
    "iterate over the correct entities" in new ViewFixture {
      world.getView[Position &: Velocity &: CNil, Mass &: CNil] should contain theSameElementsAs List(
        (entity1, Position(1, 1) &: Velocity(1, 1) &: CNil),
        (entity4, Position(1, 1) &: Velocity(1, 1) &: CNil),
      )
      world.getView[Mass &: CNil, Position &: Velocity &: CNil] should contain theSameElementsAs List(
        (entity2, Mass(1) &: CNil),
      )
      world.getView[Velocity &: CNil, Position &: CNil] shouldBe empty
    }
    "return nothing" when {
      "including and excluding the same component type" in new ViewFixture {
        world.getView[Position &: Velocity &: CNil, Position &: CNil] shouldBe empty
      }
    }
  }
}
