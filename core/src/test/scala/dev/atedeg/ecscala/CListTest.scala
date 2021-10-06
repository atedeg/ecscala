package dev.atedeg.ecscala

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.fixtures.{ ComponentsFixture, Mass, Position, Velocity }

class CListTest extends AnyWordSpec with Matchers {

  "A CList" must {
    "be created with apply()" in {
      CList() shouldBe CNil
    }
    "be created from apply with Component" in {
      CList(Position(1, 1)) shouldBe Position(1, 1) &: CNil
    }
    "only contain elements of type T <: Component" in new ComponentsFixture {
      "val t = 1 &: CNil" shouldNot typeCheck
      "val t = Position(1, 1) &: Velocity(2, 2)" should compile
    }
    // This test is temporarily ignored due to an issue with scalatest.
    // Issue link: https://github.com/scalatest/scalatest/issues/2062
    "fail to compile when trying to do an invalid unpacking" ignore /*in new ComponentsFixture with*/ {
      "val a &: CNil = Position(1, 1) &: Velocity(1, 1) &: CNil" shouldNot typeCheck
      "val a &: b &: CNil = Position(1, 1) &: CNil" shouldNot typeCheck
    }
  }

  "A CList" when {
    "empty" should {
      "return an empty iterator" in {
        CNil.iterator.hasNext shouldBe false
        an[Exception] should be thrownBy CNil.iterator.next
        CNil.iterator shouldBe Iterator.empty
      }
      "be printed as 'CNil'" in {
        CNil.toString shouldBe "CNil"
      }
    }
    "has elements" should {
      "iterate over its elements" in new ComponentsFixture {
        val cList: Position &: Velocity &: Mass &: CNil = Position(1, 2) &: Velocity(2, 2) &: Mass(3)
        cList.iterator.hasNext shouldBe true
        cList.iterator.next shouldBe Position(1, 2)
        cList.toList shouldBe List(Position(1, 2), Velocity(2, 2), Mass(3))
      }
      "print its elements" in new ComponentsFixture {
        val cList = Position(1, 2) &: Velocity(2, 2)
        cList.toString shouldBe "Position(1,2) &: Velocity(2,2)"
      }
    }
  }
}
