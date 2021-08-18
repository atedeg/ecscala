package dev.atedeg.ecscala.util.immutable

import dev.atedeg.ecscala.util.BaseIterableMapBuilder

import scala.collection.generic.DefaultSerializable
import scala.collection.{MapFactory, MapFactoryDefaults}
import scala.collection.mutable.{Builder, ReusableBuilder}
import scala.collection.immutable.{AbstractMap, Iterable, MapOps}

/**
 * This trait represents an immutable [[scala.collection.immutable.Map]] that can be efficiently iterated.
 *
 * @tparam K
 *   the type of the keys contained in this map.
 * @tparam V
 *   the type of the values associated with the keys in this map.
 */
trait IterableMap[K, +V]
    extends AbstractMap[K, V]
    with MapOps[K, V, IterableMap, IterableMap[K, V]]
    with MapFactoryDefaults[K, V, IterableMap, Iterable]
    with DefaultSerializable {
  override def mapFactory: MapFactory[IterableMap] = IterableMap
}

/**
 * This object provides a set of operations to create [[dev.atedeg.ecscala.util.immutable.IterableMap]] values.
 */
object IterableMap extends MapFactory[IterableMap] {

  override def empty[K, V]: IterableMap[K, V] = new IterableMapImpl()

  override def from[K, V](it: IterableOnce[(K, V)]): IterableMap[K, V] = new IterableMapImpl(it.iterator.toSeq: _*)

  override def newBuilder[K, V]: Builder[(K, V), IterableMap[K, V]] = new IterableMapBuilderImpl

  override def apply[K, V](elems: (K, V)*): IterableMap[K, V] = new IterableMapImpl(elems: _*)

  private class IterableMapImpl[K, +V](elems: (K, V)*) extends IterableMap[K, V] {

    private val denseKeys: Vector[K] = Vector.from(elems map (_._1))
    private val denseValues: Vector[V] = Vector.from(elems map (_._2))
    private val sparseKeysIndices: Map[K, Int] = Map.from(elems.zipWithIndex.map((elem, index) => (elem._1, index)))

    override def removed(key: K): IterableMap[K, V] = IterableMap(denseKeys zip denseValues filter (_._1 != key): _*)

    override def updated[V1 >: V](key: K, value: V1): IterableMap[K, V1] = IterableMap(
      (denseKeys zip denseValues filter (_._1 != key)) :+ (key, value): _*
    )

    override def get(key: K): Option[V] = sparseKeysIndices get key map { denseValues(_) }

    override def iterator: Iterator[(K, V)] = (denseKeys zip denseValues).iterator

    override def keys: scala.Iterable[K] = denseKeys

    override def keySet: Set[K] = Set(denseKeys: _*)

    override def keysIterator: Iterator[K] = denseKeys.iterator

    override def values: scala.Iterable[V] = denseValues

    override def valuesIterator: Iterator[V] = denseValues.iterator
  }

  private class IterableMapBuilderImpl[K, V] extends BaseIterableMapBuilder[K, V, IterableMap] {
    override def emptyFactory(): IterableMap[K, V] = IterableMap.empty
    override def addElement(map: IterableMap[K, V], elem: (K, V)): IterableMap[K, V] = map + elem
  }
}
