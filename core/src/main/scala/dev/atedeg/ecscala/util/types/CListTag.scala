package dev.atedeg.ecscala.util.types

import scala.annotation.targetName
import scala.quoted.{ Expr, Quotes, Type }
import dev.atedeg.ecscala.{ &:, CList, CNil, Component, Deletable }
import dev.atedeg.ecscala.util.types

sealed trait CListTag[L <: CList] {
  def tags: Seq[ComponentTag[Component]]
}

inline given [L <: CList]: CListTag[L] = ${ deriveCListTagImpl[L] }

extension [L <: CList: CListTag](list: L)
  def taggedWith(clt: CListTag[L]): Iterable[(Component, ComponentTag[Component])] = list zip clt.tags

extension [L <: CList: CListTag](list: Deletable[L])

  @targetName("deletableTaggedWith")
  def taggedWith(clt: CListTag[L]): Iterable[(Component, ComponentTag[Component])] = list zip clt.tags

private def deriveCListTagImpl[L <: CList: Type](using quotes: Quotes): Expr[CListTag[L]] = {
  import quotes.reflect.*
  val typeReprOfL = TypeRepr.of[L]
  if typeReprOfL =:= TypeRepr.of[Nothing] then report.error("Cannot derive CListTag for Nothing.")
  else if typeReprOfL =:= TypeRepr.of[CList] then
    report.error("Cannot derive CListTag for a generic CList, its exact components must be known at compile time.")
  else if containsDuplicates[L] then
    report.error("A CListTag cannot be derived from CLists with duplicate element types.")

  '{
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
}

inline private def getTags[L <: CList]: Seq[ComponentTag[Component]] = {
  import scala.compiletime.erasedValue
  inline erasedValue[L] match {
    case _: (head &: tail) => summon[ComponentTag[head]].asInstanceOf[ComponentTag[Component]] +: getTags[tail]
    case _ => Seq()
  }
}

private def containsDuplicates[L <: CList: Type](using quotes: Quotes): Boolean =
  Type.of[L] match {
    case '[head &: tail] => countRepetitions[L, head] > 1 || containsDuplicates[tail]
    case _ => false
  }

private def countRepetitions[L <: CList: Type, C <: Component: Type](using quotes: Quotes): Int =
  Type.of[L] match {
    case '[C &: tail] => 1 + countRepetitions[tail, C]
    case '[_ &: tail] => countRepetitions[tail, C]
    case _ => 0
  }
