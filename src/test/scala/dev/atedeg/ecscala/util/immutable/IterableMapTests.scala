package dev.atedeg.ecscala.util.immutable

import org.scalatest.matchers.should.*
import org.scalatest.wordspec.AnyWordSpec

class IterableMapTests extends AnyWordSpec with Matchers {
  "An immutable iterable map" when {
    "empty" should {
      "have size 0" in {
        IterableMap[String, Int]().size shouldBe 0
      }
      "have size 1" when {
        "adding an element" in {
          var map: Map[String, Int] = IterableMap.empty
          map = map + ("test" -> 1)
          map.size shouldBe 1
        }
      }
    }
    "has 1 element" should {
      "have size 0" when {
        "removing an element" in {
          var map: Map[String, Int] = IterableMap("test" -> 1)
          map = map - "test"
          map.size shouldBe 0
        }
      }
    }
    "has 100 elements" should {
      trait Fixture {
        val map: Map[Int, Int] = IterableMap.from((0 until 100) map { i => (i, 2 * i) })
      }
      "find an existing element" in new Fixture {
        map get 50 shouldBe Some(100)
      }
      "return None for a non-existing element" in new Fixture {
        map get 1000 shouldBe None
      }
      "return all the keys" in new Fixture {
        map.keys shouldBe (0 until 100)
        map.keysIterator.to(LazyList) shouldBe (0 until 100).iterator.to(LazyList)
      }
      "return a set with all the keys" in new Fixture {
        map.keySet should contain theSameElementsAs (0 until 100)
      }
      "return all the values" in new Fixture {
        map.values shouldBe (0 until 200 by 2)
        map.valuesIterator.to(LazyList) shouldBe (0 until 200 by 2).iterator.to(LazyList)
      }
    }
  }
  "An immutable iterable map builder" should {
    "create an iterable map" in {
      val builder = IterableMap.newBuilder[String, Int]
      builder += ("test1" -> 1)
      builder += ("test2" -> 2)
      builder += ("test3" -> 3)
      builder.result shouldBe IterableMap("test1" -> 1, "test2" -> 2, "test3" -> 3)
      builder.clear()
      builder.result shouldBe IterableMap.empty
    }
  }
}
