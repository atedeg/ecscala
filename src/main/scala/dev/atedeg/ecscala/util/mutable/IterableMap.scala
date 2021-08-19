package dev.atedeg.ecscala.util.mutable

import scala.collection.generic.DefaultSerializable
import scala.collection.{MapFactory, MapFactoryDefaults, SeqFactory, mutable}
import scala.collection.mutable.{AbstractMap, ArrayBuffer, Builder, Iterable, Map, MapOps, ReusableBuilder}
import dev.atedeg.ecscala.util.{BaseIterableMap, BaseIterableMapBuilder}

/**
 * This trait represents a mutable [[scala.collection.mutable.Map]] that can be efficiently iterated.
 *
 * @tparam K
 *   the type of the keys contained in this map.
 * @tparam V
 *   the type of the values associated with the keys in this map.
 */
trait IterableMap[K, V]
    extends dev.atedeg.ecscala.util.IterableMap[K, V]
    with Map[K, V]
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

  private class IterableMapImpl[K, V](elems: (K, V)*) extends BaseIterableMap[K, V](elems: _*) with IterableMap[K, V] {

    override protected type Dense[T] = ArrayBuffer[T]
    override protected type Sparse[K, V] = Map[K, V]

    override protected def denseFactory = ArrayBuffer
    override protected def sparseFactory = Map

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
  }

  private class IterableMapBuilderImpl[K, V] extends BaseIterableMapBuilder[K, V, IterableMap] {

    override def emptyFactory(): IterableMap[K, V] = IterableMap.empty
    override def addElement(map: IterableMap[K, V], elem: (K, V)): IterableMap[K, V] = map.addOne(elem)
  }
}
