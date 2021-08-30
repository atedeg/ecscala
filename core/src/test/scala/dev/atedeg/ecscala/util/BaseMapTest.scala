package dev.atedeg.ecscala.util

import org.scalatest.matchers.should.*
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.{ Map, MapFactory }

abstract class BaseMapTest[CC[K, V] <: Map[K, V]] extends AnyWordSpec with Matchers {

  def name: String
  def mapFactory(): MapFactory[CC]
  def add[K, V](map: CC[K, V], elem: (K, V)): CC[K, V]
  def remove[K, V](map: CC[K, V], key: K): CC[K, V]

  s"$name" when {
    "empty" should {
      "have size 0" in {
        mapFactory().empty.size shouldBe 0
      }
      "have size 1" when {
        "adding an element" in {
          var map: CC[String, Int] = mapFactory().empty
          map = add(map, "test" -> 1)
          map.size shouldBe 1
        }
      }
    }
    "has 1 element" should {
      "have size 0" when {
        "removing an element" in {
          var map: CC[String, Int] = mapFactory()("test" -> 1)
          map = remove(map, "test")
          map.size shouldBe 0
        }
      }
      "have size 1" when {
        "removing a non-existing element" in {
          var map: CC[String, Int] = mapFactory()("key" -> 1)
          map = remove(map, "test")
          map.size shouldBe 1
        }
        "updating an element" in {
          var map: CC[String, Int] = mapFactory()("key" -> 1)
          map = add(map, "key" -> 2)
          map.size shouldBe 1
          map get "key" shouldBe Some(2)
        }
      }
    }
    "has 100 elements" should {
      trait Fixture {
        val map: CC[Int, Int] = mapFactory().from((0 until 100) map { i => (i, 2 * i) })
      }
      "find an existing element" in new Fixture {
        map get 50 shouldBe Some(100)
      }
      "return None for a non-existing element" in new Fixture {
        map get 1000 shouldBe None
      }
      "return the correct element" when {
        "deleting the last one" in new Fixture {
          val removed = remove(map, 99)
          removed get 99 shouldBe None
        }
        "deleting the middle one" in new Fixture {
          val removed = remove(map, 50)
          removed get 50 shouldBe None
          removed get 99 shouldBe Some(198)
        }
      }
      "return all the keys" in new Fixture {
        map.keys should contain theSameElementsAs (0 until 100)
        map.keysIterator.to(LazyList) should contain theSameElementsAs (0 until 100).iterator.to(LazyList)
      }
      "return a set with all the keys" in new Fixture {
        map.keySet should contain theSameElementsAs (0 until 100)
      }
      "return all the values" in new Fixture {
        map.values should contain theSameElementsAs (0 until 200 by 2)
        map.valuesIterator.to(LazyList) should contain theSameElementsAs (0 until 200 by 2).iterator.to(LazyList)
      }
    }
  }

  s"$name builder" should {
    "create an iterable map" in {
      val builder = mapFactory().newBuilder[String, Int]
      builder += ("test1" -> 1)
      builder += ("test2" -> 2)
      builder += ("test3" -> 3)
      builder.result shouldBe mapFactory()("test1" -> 1, "test2" -> 2, "test3" -> 3)
      builder.clear()
      builder.result shouldBe mapFactory().empty
    }
  }
}
