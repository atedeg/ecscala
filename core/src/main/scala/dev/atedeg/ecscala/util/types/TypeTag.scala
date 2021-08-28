package dev.atedeg.ecscala.util.types

import dev.atedeg.ecscala.Component

import scala.quoted.*

/**
 * A TypeTag is a trait used to describe types keeping information about the type that would otherwise be erased at
 * runtime.
 *
 * @tparam T
 *   the type whose compiletime information are stored in the TypeTag.
 */
sealed trait TypeTag[T]

inline given [T]: TypeTag[T] = ${ deriveTypeTagImpl[T] }

private def deriveTypeTagImpl[T: Type](using quotes: Quotes): Expr[TypeTag[T]] = {
  import quotes.reflect.*
  val typeReprOfT = TypeRepr.of[T]
  if typeReprOfT =:= TypeRepr.of[Component] then
    report.error("Can only derive TypeTags for subtypes of Component, not for Component itself.")
  else if typeReprOfT =:= TypeRepr.of[Nothing] then report.error("Cannot derive TypeTag[Nothing]")
  else if !(typeReprOfT <:< TypeRepr.of[Component]) then
    report.error(s"${typeReprOfT.show} must be a subtype of Component")

  '{
    new TypeTag[T] {
      override def toString: String = ${ Expr(typeReprOfT.show) }
      override def hashCode: Int = this.toString.hashCode
      override def equals(obj: Any) = obj match {
        case that: TypeTag[_] => that.toString == this.toString
        case _ => false
      }
    }
  }
}
