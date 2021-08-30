package dev.atedeg.ecscala.util.immutable

import scala.collection.generic.DefaultSerializable
import scala.collection.{ IterableFactory, MapFactory, MapFactoryDefaults }
import scala.collection.mutable.{ Builder, ReusableBuilder }
import scala.collection.immutable.{ Iterable, Map, MapOps }

import dev.atedeg.ecscala.util.{ BaseIterableMap, BaseIterableMapBuilder }

/**
 * This trait represents an immutable [[scala.collection.immutable.Map]] that can be efficiently iterated.
 *
 * @tparam K
 *   the type of the keys contained in this map.
 * @tparam V
 *   the type of the values associated with the keys in this map.
 */
trait IterableMap[K, +V]
    extends dev.atedeg.ecscala.util.IterableMap[K, V]
    with Map[K, V]
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

  override def newBuilder[K, V]: Builder[(K, V), IterableMap[K, V]] = new IterableMapBuilderImpl()

  override def apply[K, V](elems: (K, V)*): IterableMap[K, V] = new IterableMapImpl(elems: _*)

  private class IterableMapImpl[K, V](elems: (K, V)*) extends BaseIterableMap[K, V](elems: _*) with IterableMap[K, V] {

    override type Dense[T] = Vector[T]
    override type Sparse[K, V] = Map[K, V]

    override protected def denseFactory = Vector
    override protected def sparseFactory = Map

    private def withoutElem(key: K): Seq[(K, V)] = denseKeys zip denseValues filter { _._1 != key }

    override def removed(key: K): IterableMap[K, V] = IterableMap.from(withoutElem(key))

    override def updated[V1 >: V](key: K, value: V1): IterableMap[K, V1] =
      IterableMap.from(withoutElem(key) :+ (key, value))
  }

  private class IterableMapBuilderImpl[K, V] extends BaseIterableMapBuilder[K, V, IterableMap] {

    override def emptyFactory(): IterableMap[K, V] = IterableMap.empty
    override def addElement(map: IterableMap[K, V], elem: (K, V)): IterableMap[K, V] = map + elem
  }
}