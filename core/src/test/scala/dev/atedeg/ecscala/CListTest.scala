package dev.atedeg.ecscala

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.fixtures.ComponentsFixture
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CListTest extends AnyWordSpec with Matchers {

  "A CList" must {
    "only contain elements of type T <: Component" in new ComponentsFixture {
      "val t = 1 &: CNil" shouldNot typeCheck
      "val t = Position(1, 1) &: Velocity(2, 2)" should compile
    }
  }

  "A CList" when {
    "empty" should {
      "return an empty iterator" in {
        CNil.iterator shouldBe Iterator.empty
      }
    }
    "has elements" should {
      "iterate over its elements" in new ComponentsFixture {
        val cList: Position &: Velocity &: Mass &: CNil = Position(1, 2) &: Velocity(2, 2) &: Mass(3)
        cList.toList shouldBe List(Position(1, 2), Velocity(2, 2), Mass(3))
      }
    }
  }
}
