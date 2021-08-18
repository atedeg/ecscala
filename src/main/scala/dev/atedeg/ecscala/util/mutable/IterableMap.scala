package dev.atedeg.ecscala.util.mutable

import dev.atedeg.ecscala.util.BaseIterableMapBuilder

import scala.collection.generic.DefaultSerializable
import scala.collection.{MapFactory, MapFactoryDefaults, mutable}
import scala.collection.mutable.{AbstractMap, ArrayBuffer, Builder, Iterable, Map, MapOps, ReusableBuilder}

/**
 * This trait represents a mutable [[scala.collection.mutable.Map]] that can be efficiently iterated.
 *
 * @tparam K
 *   the type of the keys contained in this map.
 * @tparam V
 *   the type of the values associated with the keys in this map.
 */
trait IterableMap[K, V]
    extends AbstractMap[K, V]
    with MapOps[K, V, IterableMap, IterableMap[K, V]]
    with MapFactoryDefaults[K, V, IterableMap, Iterable]
    with DefaultSerializable {
  override def mapFactory: MapFactory[IterableMap] = IterableMap
}

/**
 * This object provides a set of operations to create [[dev.atedeg.ecscala.util.mutable.IterableMap]] values.
 */
object IterableMap extends MapFactory[IterableMap] {

  override def empty[K, V]: IterableMap[K, V] = new IterableMapImpl()

  override def from[K, V](it: IterableOnce[(K, V)]): IterableMap[K, V] = new IterableMapImpl(it.iterator.toSeq: _*)

  override def newBuilder[K, V]: mutable.Builder[(K, V), IterableMap[K, V]] = new IterableMapBuilderImpl()

  override def apply[K, V](elems: (K, V)*): IterableMap[K, V] = new IterableMapImpl(elems: _*)

  private class IterableMapImpl[K, V](elems: (K, V)*) extends IterableMap[K, V] {

    private val denseKeys: ArrayBuffer[K] = ArrayBuffer.from(elems map { _._1 })
    private val denseValues: ArrayBuffer[V] = ArrayBuffer.from(elems map { _._2 })
    private val sparseKeysIndices: Map[K, Int] = Map.from(denseKeys.zipWithIndex)

    override def addOne(elem: (K, V)): this.type = {
      val (key, value) = elem
      val denseIndex = denseKeys.size + 1
      denseKeys += key
      denseValues += value
      sparseKeysIndices += (key -> denseIndex)
      this
    }

    override def subtractOne(elem: K): this.type = {
      // We switch the last item in place of the one we deleted
      val elemIndex = sparseKeysIndices get elem
      elemIndex match {
        case Some(index) => {
          sparseKeysIndices -= elem
          if (index != denseKeys.size - 1) {
            denseKeys(index) = denseKeys.last
            denseValues(index) = denseValues.last
            sparseKeysIndices += (denseKeys.last -> index)
          }
          denseKeys.dropRightInPlace(1)
          denseValues.dropRightInPlace(1)
          this
        }
        case None => this
      }
    }

    override def get(key: K): Option[V] = sparseKeysIndices get key map { denseValues(_) }

    override def iterator: Iterator[(K, V)] = (denseKeys zip denseValues).iterator

    override def keys: scala.Iterable[K] = denseKeys

    override def keySet: Set[K] = Set.from(denseKeys)

    override def keysIterator: Iterator[K] = denseKeys.iterator

    override def values: scala.Iterable[V] = denseValues

    override def valuesIterator: Iterator[V] = denseValues.iterator
  }

  private class IterableMapBuilderImpl[K, V] extends BaseIterableMapBuilder[K, V, IterableMap] {
    override def emptyFactory(): IterableMap[K, V] = IterableMap.empty
    override def addElement(map: IterableMap[K, V], elem: (K, V)): IterableMap[K, V] = map.addOne(elem)
  }
}
