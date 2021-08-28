package dev.atedeg.ecscala

import scala.annotation.{ showAsInfix, tailrec }
import scala.collection.IterableOps
import dev.atedeg.ecscala.Component
import dev.atedeg.ecscala.util.types.TypeTag

/**
 * A List of elements whose type must be a subtype of [[Component]].
 */
sealed trait CList extends Iterable[Component]

object CList {

  implicit class CListOps[L <: CList](list: L) {
    def &:[H <: Component: TypeTag](head: H): H &: L = dev.atedeg.ecscala.&:(head, list)
  }
  // We ended up using the "old" implicit syntax since using the extension method
  // IntelliJ cannot correctly infer the type of a CList like
  //     val l = Position(1, 2) &: Velocity(1, 2) &: CNil
  // and considers it as of type Any; however, the compiler seems to correctly infer its type.
  // We believe it may be due to a problem with IntelliJ Scala 3 plugin.
  //
  // The Scala 3 idiomatic way to write the same method could be:
  // extension [H <: Component, T <: CList](head: H) def &:(tail: T): H &: T = &:(head, tail)
}

/**
 * An empty [[CList]].
 */
sealed trait CNil extends CList {
  def &:[H <: Component: TypeTag](head: H): H &: CNil = dev.atedeg.ecscala.&:(head, this)
}

case object CNil extends CNil {
  override def iterator = Iterator.empty
}

/**
 * Constructor of a [[CList]] consisting of a head and another CList as tail.
 * @param h
 *   the head of the [[CList]].
 * @param t
 *   the tail of the [[CList]].
 * @tparam H
 *   the type of the head of the [[CList]].
 * @tparam T
 *   the type of the tail of the [[CList]].
 */
@showAsInfix
case class &:[H <: Component: TypeTag, T <: CList](h: H, t: T) extends CList {

  override def iterator = new Iterator[Component] {
    private var list: CList = h &: t

    override def hasNext = list != CNil

    override def next() = {
      val head &: tail = list
      list = tail
      head
    }
  }
}
