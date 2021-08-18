package dev.atedeg.ecscala.util.mutable

import dev.atedeg.ecscala.util.BaseMapTests

import scala.collection.MapFactory
import scala.collection.{Map, MapFactory}

class IterableMapTests extends BaseMapTests[IterableMap] {

  override def name: String = "A mutable iterable map"

  override def mapFactory(): MapFactory[IterableMap] = IterableMap

  override def add[K, V](map: IterableMap[K, V], elem: (K, V)): IterableMap[K, V] = {
    map += elem
    map
  }

  override def remove[K, V](map: IterableMap[K, V], key: K): IterableMap[K, V] = {
    map -= key
    map
  }
}
