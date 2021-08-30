package dev.atedeg.ecscala.util.macros

import scala.quoted.*

object Debug {
  inline def showType[T] = ${ showTypeImpl[T] }

  private def showTypeImpl[T: Type](using quotes: Quotes) = {
    import quotes.reflect.*
    Expr(TypeRepr.of[T].show)
  }
}
