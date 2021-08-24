package dev.atedeg.ecscala.util.mutable

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable

class LongIterableMapTest extends AnyWordSpec with Matchers {

  "LIterableMap" when {
    "empty" should {
      "have size 0" in {
        LongIterableMap().size shouldBe 0
      }
    }
    "have size 1" when {
      "adding an element" in {
        var map = LongIterableMap.empty[String]
        map += (1L -> "1")
        map.size shouldBe 1
      }
    }
    "has 1 element" should {
      "have size 0" when {
        "removing an element" in {
          var map: mutable.Map[Long, String] = LongIterableMap(1L -> "1")
          map -= 1L
          map.size shouldBe 0
        }
      }
      "have size 1" when {
        "removing a non-existing element" in {
          var map = LongIterableMap(1L -> "1")
          map -= 2L
          map.size shouldBe 1
        }
      }
    }
    "has 100 elements" should {
      trait MapFixture {
        val map: mutable.Map[Long, Int] = LongIterableMap.from((0 until 100) map { i => (i, 2 * i) })
      }
      "find an existing element" in new MapFixture {
        map get 50 shouldBe Some(100)
      }
      "return None for a non-existing element" in new MapFixture {
        map get 1000 shouldBe None
      }
      "return the correct element" when {
        "deleting the last one" in new MapFixture {
          map -= 99L
          map get 99 shouldBe None
        }
        "deleting the middle one" in new MapFixture {
          map -= 50
          map get 50 shouldBe None
          map get 99 shouldBe Some(198)
        }
      }
      "return all the keys" in new MapFixture {
        map.keys should contain theSameElementsAs (0 until 100)
        map.keysIterator.to(LazyList) should contain theSameElementsAs (0 until 100).iterator.to(LazyList)
      }
      "return a set with all the keys" in new MapFixture {
        map.keySet should contain theSameElementsAs (0 until 100)
      }
      "return all the values" in new MapFixture {
        map.values should contain theSameElementsAs (0 until 200 by 2)
        map.valuesIterator.to(LazyList) should contain theSameElementsAs (0 until 200 by 2).iterator.to(LazyList)
      }
      "have size 0 after clear" in new MapFixture {
        map.clear
        map.size shouldBe 0
      }
    }
  }
}
