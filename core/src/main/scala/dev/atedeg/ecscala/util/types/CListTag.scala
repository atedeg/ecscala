package dev.atedeg.ecscala.util.types

import dev.atedeg.ecscala.{ &:, CList, Component }
import dev.atedeg.ecscala.util.types.given

import scala.compiletime.{ codeOf, erasedValue, error }
import scala.annotation.tailrec
import scala.quoted.{ Expr, Quotes, Type }

sealed trait CListTag[L <: CList] {
  def tags: Seq[ComponentTag[? <: Component]]
}

inline given [L <: CList]: CListTag[L] = {
  inline if containsDuplicates[L] then error("A CListTag cannot be generated for CLists with duplicate element types.")

  new CListTag[L] {
    override def tags = getTags[L]
    override def toString: String = tags.toString
    override def hashCode: Int = tags.hashCode
    override def equals(obj: Any) = obj match {
      case that: CListTag[_] => that.tags == this.tags
      case _ => false
    }
  }
}

inline private def getTags[L <: CList]: Seq[ComponentTag[? <: Component]] = {
  inline erasedValue[L] match {
    case _: (head &: tail) => summon[ComponentTag[head]] +: getTags[tail]
    case _ => Seq()
  }
}

inline private def containsDuplicates[L <: CList]: Boolean =
  inline erasedValue[L] match {
    case _: (head &: tail) => countRepetitions[L, head] > 1 || containsDuplicates[tail]
    case _ => false
  }

inline private def countRepetitions[L <: CList, C <: Component]: Int =
  inline erasedValue[L] match {
    case _: (C &: tail) => 1 + countRepetitions[tail, C]
    case _: (_ &: tail) => countRepetitions[tail, C]
    case _ => 0
  }
