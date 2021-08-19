package dev.atedeg.ecscala.util

import scala.collection.*
import scala.collection.mutable.ReusableBuilder
import scala.collection.generic.DefaultSerializable

trait IterableMap[K, +V]
    extends Map[K, V]
    with MapOps[K, V, IterableMap, IterableMap[K, V]]
    with MapFactoryDefaults[K, V, IterableMap, Iterable] {
  override def mapFactory: MapFactory[IterableMap] = IterableMap
}

private[util] abstract class BaseIterableMap[K, V](elems: (K, V)*) extends IterableMap[K, V] {

  protected type Dense[T] <: IndexedSeq[T] with SeqOps[T, IndexedSeq, IndexedSeq[T]]
  protected type Sparse[K, V] <: Map[K, V]

  protected def denseFactory: SeqFactory[Dense]
  protected def sparseFactory: MapFactory[Sparse]

  protected val denseKeys: Dense[K] = denseFactory.from(elems map { _._1 })
  protected val denseValues: Dense[V] = denseFactory.from(elems map { _._2 })
  protected val sparseKeysIndices: Sparse[K, Int] = sparseFactory.from(denseKeys.zipWithIndex)

  override def mapFactory: MapFactory[IterableMap] = IterableMap

  override def get(key: K): Option[V] = sparseKeysIndices get key map { denseValues(_) }

  override def iterator: Iterator[(K, V)] = (denseKeys zip denseValues).iterator

  override def keys: scala.Iterable[K] = denseKeys

  override def keySet: Set[K] = Set.from(denseKeys)

  override def keysIterator: Iterator[K] = denseKeys.iterator

  override def values: scala.Iterable[V] = denseValues

  override def valuesIterator: Iterator[V] = denseValues.iterator
}

object IterableMap extends MapFactory.Delegate[IterableMap](dev.atedeg.ecscala.util.immutable.IterableMap)

private[util] abstract class BaseIterableMapBuilder[K, V, CC[K, V] <: Map[K, V]]
    extends ReusableBuilder[(K, V), CC[K, V]] {

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
