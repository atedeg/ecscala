package dev.atedeg.ecscala.util.mutable

import scala.collection.{ mutable, IterableOps, StrictOptimizedIterableOps }
import scala.collection.*
import scala.collection.mutable.{ ArrayBuffer, Builder, GrowableBuilder }

class LongIterableMap[A](elems: (Long, A)*)
    extends mutable.Map[Long, A]
    with mutable.MapOps[Long, A, mutable.Map, LongIterableMap[A]]
    with StrictOptimizedIterableOps[(Long, A), mutable.Iterable, LongIterableMap[A]] {

  // Fields
  protected val denseKeys: ArrayBuffer[Long] = ArrayBuffer.from(elems map { _._1 })
  protected val denseValues: ArrayBuffer[A] = ArrayBuffer.from(elems map { _._2 })
  protected val sparseKeysIndices: mutable.LongMap[Int] = mutable.LongMap.from(denseKeys.zipWithIndex)

  override def get(s: Long): Option[A] = sparseKeysIndices get s map { denseValues(_) }

  override def iterator: Iterator[(Long, A)] = new Iterator[(Long, A)] {
    private var nextIndex = 0

    override def hasNext: Boolean = nextIndex < math.min(denseKeys.size, denseValues.size)

    override def next(): (Long, A) = {
      val kv = (denseKeys(nextIndex), denseValues(nextIndex))
      nextIndex += 1
      kv
    }
  }

  override def addOne(kv: (Long, A)): this.type = {
    val (key, value) = kv
    sparseKeysIndices get key match {
      // We don't need to change the sparse key index because we replace key and value in the dense arrays
      case Some(keyIndex) => denseValues(keyIndex) = value
      case None => {
        val denseIndex = denseKeys.size
        denseKeys += key
        denseValues += value
        sparseKeysIndices += (key -> denseIndex)
      }
    }
    this
  }

  override def subtractOne(s: Long): this.type = {
    // We switch the last item in place of the one we deleted
    val elemIndex = sparseKeysIndices get s
    elemIndex match {
      case Some(index) => {
        sparseKeysIndices -= s
        if (index != denseKeys.size - 1) replaceWithLast(index)
        removeLast()
        this
      }
      case None => this
    }
  }

  // Overloading of transformation methods that should return a PrefixMap
  def map[B](f: ((Long, A)) => (Long, B)): LongIterableMap[B] =
    strictOptimizedMap(LongIterableMap.newBuilder, f)

  def flatMap[B](f: ((Long, A)) => IterableOnce[(Long, B)]): LongIterableMap[B] =
    strictOptimizedFlatMap(LongIterableMap.newBuilder, f)

  // Override `concat` and `empty` methods to refine their return type
  override def concat[B >: A](suffix: IterableOnce[(Long, B)]): LongIterableMap[B] =
    strictOptimizedConcat(suffix, LongIterableMap.newBuilder)
  override def empty: LongIterableMap[A] = new LongIterableMap

  // Members declared in scala.collection.mutable.Clearable
  override def clear(): Unit = {
    denseKeys.clear()
    denseValues.clear()
    sparseKeysIndices.clear()
  }
  // Members declared in scala.collection.IterableOps
  override protected def fromSpecific(coll: IterableOnce[(Long, A)]): LongIterableMap[A] = LongIterableMap.from(coll)
  override protected def newSpecificBuilder: mutable.Builder[(Long, A), LongIterableMap[A]] = LongIterableMap.newBuilder

  override def className = "LIterableMap"

  private def replaceWithLast(index: Int): Unit = {
    denseKeys(index) = denseKeys.last
    denseValues(index) = denseValues.last
    sparseKeysIndices += (denseKeys.last -> index)
  }

  private def removeLast(): Unit = {
    denseKeys.dropRightInPlace(1)
    denseValues.dropRightInPlace(1)
  }
}

object LongIterableMap {
  def empty[A] = new LongIterableMap[A]

  def from[A](source: IterableOnce[(Long, A)]): LongIterableMap[A] =
    source match {
      case pm: LongIterableMap[A] => pm
      case _ => (newBuilder ++= source).result()
    }

  def apply[A](kvs: (Long, A)*): LongIterableMap[A] = from(kvs)

  def newBuilder[A]: mutable.Builder[(Long, A), LongIterableMap[A]] =
    new mutable.GrowableBuilder[(Long, A), LongIterableMap[A]](empty)

  //import scala.language.implicitConversions

  //given [A] =>> Conversion[this.type, Factory[(Long, _), LIterableMap[_]]] = ???
  given toFactory[A](using self: this.type): Factory[(Long, A), LongIterableMap[A]] =
    new Factory[(Long, A), LongIterableMap[A]] {
      def fromSpecific(it: IterableOnce[(Long, A)]): LongIterableMap[A] = self.from(it)
      def newBuilder: mutable.Builder[(Long, A), LongIterableMap[A]] = self.newBuilder
    }
}
