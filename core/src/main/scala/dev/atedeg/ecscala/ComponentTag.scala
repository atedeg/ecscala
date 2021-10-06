package dev.atedeg.ecscala

import scala.quoted.{ Expr, Quotes, Type }
import dev.atedeg.ecscala.{ CList, Component }

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

  val computedString = typeReprOfC.show
  val computedHashCode = computedString.hashCode
  '{
    new ComponentTag[C] {
      override def toString: String = ${ Expr(computedString) }
      override def hashCode: Int = ${ Expr(computedHashCode) }
      override def equals(obj: Any) = obj match {
        case that: ComponentTag[_] =>
          (this eq that) || (this.hashCode == that.hashCode && this.toString == that.toString)
        case _ => false
      }
    }
  }
}
