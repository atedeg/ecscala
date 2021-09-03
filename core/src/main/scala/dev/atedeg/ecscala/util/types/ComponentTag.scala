package dev.atedeg.ecscala.util.types

import dev.atedeg.ecscala.{ CList, Component }

import scala.quoted.*

/**
 * A ComponentTag is a trait used to describe types keeping information about the type that would otherwise be erased at
 * runtime.
 *
 * @tparam C
 *   the type whose compiletime information are stored in the ComponentTag.
 */
sealed trait ComponentTag[C]

inline given [C]: ComponentTag[C] = ${ deriveComponentTagImpl[C] }

private def deriveComponentTagImpl[C: Type](using quotes: Quotes): Expr[ComponentTag[C]] = {
  import quotes.reflect.*
  val typeReprOfC = TypeRepr.of[C]
  if typeReprOfC =:= TypeRepr.of[Component] then
    report.error("Can only derive ComponentTags for subtypes of Component, not for Component itself.")
  else if typeReprOfC =:= TypeRepr.of[Nothing] then report.error("Cannot derive ComponentTag[Nothing]")
  else if !(typeReprOfC <:< TypeRepr.of[Component]) then
    report.error(s"${typeReprOfC.show} must be a subtype of Component")

  '{
    new ComponentTag[C] {
      override def toString: String = ${ Expr(typeReprOfC.show) }
      override def hashCode: Int = this.toString.hashCode
      override def equals(obj: Any) = obj match {
        case that: ComponentTag[_] => that.toString == this.toString
        case _ => false
      }
    }
  }
}
