package dev.atedeg.ecscala.dsl
import dev.atedeg.ecscala.World

/**
 * This trait provides a domain specific language (DSL) for expressing the Ecscala framework operation using an
 * english-like language.
 */
trait EcscalaDSL {
  def entity: EntityWord = EntityWord()
}

/**
 * This case class enables the following syntax:
 *
 * <pre class="stHighlight"> [...] hasAn entity withComponent [...] </pre>
 */
case class EntityWord()
