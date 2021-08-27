package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.util.types.TypeTag
import dev.atedeg.ecscala.{ Component, Entity, World }

/**
 * This case class enables the following syntax:
 *
 * <pre class="stHighlight"> world hasAn entity </pre>
 */
case class EntityWord()

/**
 * This trait provides a domain specific language (DSL) for expressing the Ecscala framework operation using an
 * english-like language.
 */
trait EcscalaDSL {
  def entity: EntityWord = EntityWord()
}

/**
 * This method enables the following syntax:
 *
 * <pre class="stHighlight"> entity withComponents { 
 *                         *(Component1()) 
 *                         *(Component2())
 *                         [...]
 *                       } </pre>
 */
def *[T <: Component](component: T)(using e: Entity)(using tt: TypeTag[T]) = {
  e.addComponent(component)(tt)
}
