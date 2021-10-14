sealed trait ComponentTag[C]
inline given [C]: ComponentTag[C] = ${ deriveComponentTagImpl[C] }

private def deriveComponentTagImpl[C: Type](using quotes: Quotes): Expr[ComponentTag[C]] = {
  import quotes.reflect.*
  val typeReprOfC = TypeRepr.of[C]
  // Perform checks on the type...

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
