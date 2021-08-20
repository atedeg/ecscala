package dev.atedeg.ecscala.util.types

import scala.quoted.*

/**
 * A TypeTag is a trait used to describe types keeping informations about the type that would otherwise be erased at
 * runtime.
 *
 * @tparam T
 *   the type whose compiletime informations are stored in the TypeTag.
 */
private[ecscala] sealed trait TypeTag[T] {
  override def equals(obj: Any): Boolean
  override def hashCode(): Int
  override def toString: String
}

inline given [T]: TypeTag[T] = ${ deriveTypeTagImpl[T] }
private def deriveTypeTagImpl[T: Type](using quotes: Quotes): Expr[TypeTag[T]] = {
  import quotes.reflect.*
  '{
    new TypeTag[T] {
      override def toString: String = ${ Expr(TypeRepr.of[T].show) }
      override def hashCode: Int = this.toString.hashCode
      override def equals(obj: Any) = obj match {
        case that: TypeTag[_] => that.toString == this.toString
        case _                => false
      }
    }
  }
}
