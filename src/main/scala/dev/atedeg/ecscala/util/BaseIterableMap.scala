package dev.atedeg.ecscala.util

import scala.collection.Map
import scala.collection.mutable

private[util] abstract class BaseIterableMapBuilder[K, V, CC[K, V] <: Map[K, V]]
    extends mutable.ReusableBuilder[(K, V), CC[K, V]] {

  def emptyFactory(): CC[K, V]
  def addElement(map: CC[K, V], elem: (K, V)): CC[K, V]

  private var elems: CC[K, V] = emptyFactory()

  override def clear(): Unit = {
    elems = emptyFactory()
  }

  override def result(): CC[K, V] = elems

  override def addOne(elem: (K, V)): this.type = {
    elems = addElement(elems, elem)
    this
  }
}
