package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.util.types.ComponentTag
import dev.atedeg.ecscala.{ Component, Entity, World }
import dev.atedeg.ecscala.dsl.Words.*

/**
 * This trait provides a domain specific language (DSL) for expressing the Ecscala framework operation using an
 * english-like language.
 */
trait ECScalaDSL extends ExtensionMethodsDSL {
  def entity: EntityWord = EntityWord()
}

private[dsl] case class ComponentWrapper() {

  /**
   * This method adds its argument to an entity and enables the following syntax:
   *
   * {{{
   * entity withComponents {
   *
   *   Component1() and Component2() and Component3()
   *
   * }
   * }}}
   *
   * @param rightComponent
   *   The component to be added to an entity.
   * @return
   *   A [[ComponentWrapper]] that enables the components chaining.
   */
  def and[C <: Component: ComponentTag](rightComponent: C)(using entity: Entity): ComponentWrapper = {
    entity.addComponent(rightComponent)
    this
  }
}