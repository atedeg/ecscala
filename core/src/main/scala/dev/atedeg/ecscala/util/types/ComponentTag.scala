package dev.atedeg.ecscala.util.types

import dev.atedeg.ecscala.{ CList, Component }

import scala.quoted.*

/**
 * A ComponentTag is a trait used to describe types keeping information about the type that would otherwise be erased at
 * runtime.
 *
 * @tparam T
 *   the type whose compiletime information are stored in the ComponentTag.
 */
sealed trait ComponentTag[T]

inline given [T]: ComponentTag[T] = ${ deriveComponentTagImpl[T] }

private def deriveComponentTagImpl[T: Type](using quotes: Quotes): Expr[ComponentTag[T]] = {
  import quotes.reflect.*
  val typeReprOfT = TypeRepr.of[T]
  if typeReprOfT =:= TypeRepr.of[Component] then
    report.error("Can only derive ComponentTags for subtypes of Component, not for Component itself.")
  else if typeReprOfT =:= TypeRepr.of[Nothing] then report.error("Cannot derive ComponentTag[Nothing]")
  else if !(typeReprOfT <:< TypeRepr.of[Component]) then
    report.error(s"${typeReprOfT.show} must be a subtype of Component")

  '{
    new ComponentTag[T] {
      override def toString: String = ${ Expr(typeReprOfT.show) }
      override def hashCode: Int = this.toString.hashCode
      override def equals(obj: Any) = obj match {
        case that: ComponentTag[_] => that.toString == this.toString
        case _ => false
      }
    }
  }
}
