package dev.atedeg.ecscala.util

import org.scalatest.matchers.should.*
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.{Map, MapFactory}

abstract class BaseIterableMapTests[CC[K, V] <: Map[K, V]] extends AnyWordSpec with Matchers {
  def iterableMapFactory(): MapFactory[CC]
  def add[K, V](map: CC[K, V], elem: (K, V)): CC[K, V]
  def remove[K, V](map: CC[K, V], key: K): CC[K, V]

  "An iterable map" when {
    "empty" should {
      "have size 0" in {
        iterableMapFactory().empty.size shouldBe 0
      }
      "have size 1" when {
        "adding an element" in {
          var map: CC[String, Int] = iterableMapFactory().empty
          map = add(map, "test" -> 1)
          map.size shouldBe 1
        }
      }
    }
    "has 1 element" should {
      "have size 0" when {
        "removing an element" in {
          var map: CC[String, Int] = iterableMapFactory()("test" -> 1)
          map = remove(map, "test")
          map.size shouldBe 0
        }
      }
    }
    "has 100 elements" should {
      trait Fixture {
        val map: CC[Int, Int] = iterableMapFactory().from((0 until 100) map { i => (i, 2 * i) })
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
      val builder = iterableMapFactory().newBuilder[String, Int]
      builder += ("test1" -> 1)
      builder += ("test2" -> 2)
      builder += ("test3" -> 3)
      builder.result shouldBe iterableMapFactory()("test1" -> 1, "test2" -> 2, "test3" -> 3)
      builder.clear()
      builder.result shouldBe iterableMapFactory().empty
    }
  }
}
