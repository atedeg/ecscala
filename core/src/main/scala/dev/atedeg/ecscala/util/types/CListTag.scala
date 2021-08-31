package dev.atedeg.ecscala.util.types

import dev.atedeg.ecscala.util.macros.Debug
import dev.atedeg.ecscala.{ &:, CList, Component }
import dev.atedeg.ecscala.util.types.given

import scala.quoted.{ Expr, Quotes, Type }

sealed trait CListTag[L <: CList] {
  def tags: Seq[ComponentTag[? <: Component]]
}

inline given [L <: CList]: CListTag[L] = {
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

inline private def getTags[C <: CList]: Seq[ComponentTag[? <: Component]] = {
  import scala.compiletime.erasedValue
  inline erasedValue[C] match {
    case _: (head &: tail) => summon[ComponentTag[head]] +: getTags[tail]
    case _ => Seq()
  }
}
