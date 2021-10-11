inline given [L <: CList]: CListTag[L] = ${ deriveCListTagImpl[L] }
private def deriveCListTagImpl[L <: CList: Type](using quotes: Quotes): Expr[CListTag[L]] = {
  import quotes.reflect.*
  // Perform checks on the type...
  if containsDuplicates[L] then
    report.error("A CListTag cannot be derived from CLists with duplicate element types.")
  // Tag code generation...
}

private def containsDuplicates[L <: CList: Type](using quotes: Quotes): Boolean =
  Type.of[L] match { // Pattern matching on the compile-time type T
    case '[head &: tail] => countRepetitions[L, head] > 1 || containsDuplicates[tail]
    case _ => false
  }

private def countRepetitions[L <: CList: Type, C <: Component: Type](using quotes: Quotes): Int =
  Type.of[L] match {
    case '[C &: tail] => 1 + countRepetitions[tail, C]
    case '[_ &: tail] => countRepetitions[tail, C]
    case _ => 0
  }
