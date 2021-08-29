package dev.atedeg.ecscala

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.fixtures.*
import dev.atedeg.ecscala.fixtures.{ ComponentsFixture, ViewFixture, WorldFixture }
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ViewTest extends AnyWordSpec with Matchers {

  "A view" should {
    "iterate over the correct entities" in new ViewFixture {
      world.getView[Position] should contain theSameElementsAs List((entity1, Position(1, 1) &: CNil))
      world.getView[Velocity] should contain theSameElementsAs List(
        (entity1, Velocity(2, 2) &: CNil),
        (entity2, Velocity(3, 5) &: CNil),
      )
      world.getView[Velocity &: Mass &: CNil] should contain theSameElementsAs List(
        (entity2, Velocity(3, 5) &: Mass(3)),
      )
      world.getView[Position &: Mass &: CNil] shouldBe empty
      world.getView[Position &: Velocity &: CNil] should contain theSameElementsAs List(
        (entity1, Position(1, 1) &: Velocity(2, 2)),
      )
      world.getView[Position &: Mass &: Velocity &: CNil] shouldBe empty
    }
    "be commutative" in new ViewFixture {
      (world.getView[Mass &: Velocity &: CNil] map (_.head)) should contain theSameElementsAs
        (world.getView[Velocity &: Mass &: CNil] map (_.head))
    }
  }
}
