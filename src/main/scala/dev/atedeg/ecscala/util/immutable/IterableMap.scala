package dev.atedeg.ecscala.util.immutable

import scala.collection.generic.DefaultSerializable
import scala.collection.{MapFactory, MapFactoryDefaults}
import scala.collection.mutable.{ReusableBuilder, Builder}
import scala.collection.immutable.{AbstractMap, Iterable, MapOps}

trait IterableMap[K, +V]
    extends AbstractMap[K, V]
    with MapOps[K, V, IterableMap, IterableMap[K, V]]
    with MapFactoryDefaults[K, V, IterableMap, Iterable]
    with DefaultSerializable {
  override def mapFactory: MapFactory[IterableMap] = IterableMap
}

object IterableMap extends MapFactory[IterableMap] {

  override def empty[K, V]: IterableMap[K, V] = new IterableMapImpl()

  override def from[K, V](it: IterableOnce[(K, V)]): IterableMap[K, V] = new IterableMapImpl(it.iterator.toSeq: _*)

  override def newBuilder[K, V]: Builder[(K, V), IterableMap[K, V]] = new IterableMapBuilderImpl

  override def apply[K, V](elems: (K, V)*): IterableMap[K, V] = new IterableMapImpl(elems: _*)

  private class IterableMapImpl[K, +V](elems: (K, V)*) extends IterableMap[K, V] {

    private val denseKeys: Vector[K] = Vector(elems map (_._1): _*)
    private val denseValues: Vector[V] = Vector(elems map (_._2): _*)
    private val sparseKeysIndices: Map[K, Int] = Map(elems.zipWithIndex.map((elem, index) => (elem._1, index)): _*)

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
  
  private class IterableMapBuilderImpl[K, V] extends ReusableBuilder[(K, V), IterableMap[K, V]] {
    private var elems: IterableMap[K, V] = IterableMap.empty
    
    override def clear(): Unit = {
      elems = IterableMap.empty
    }

    override def result(): IterableMap[K, V] = elems

    override def addOne(elem: (K, V)): this.type = {
      elems += elem
      this
    }
  }
}
