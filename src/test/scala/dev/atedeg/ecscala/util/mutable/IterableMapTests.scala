package dev.atedeg.ecscala.util.mutable

import dev.atedeg.ecscala.util.BaseIterableMapTests

import scala.collection.MapFactory
import scala.collection.{Map, MapFactory}

class IterableMapTests extends BaseIterableMapTests[IterableMap] {

  override def iterableMapFactory(): MapFactory[IterableMap] = IterableMap

  override def add[K, V](map: IterableMap[K, V], elem: (K, V)): IterableMap[K, V] = {
    map += elem
    map
  }

  override def remove[K, V](map: IterableMap[K, V], key: K): IterableMap[K, V] = {
    map -= key
    map
  }
}
