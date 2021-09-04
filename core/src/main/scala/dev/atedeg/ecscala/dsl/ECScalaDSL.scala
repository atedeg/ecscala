package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.util.types.{ CListTag, ComponentTag }
import dev.atedeg.ecscala.{ CList, Component, Entity, System, View, World }
import dev.atedeg.ecscala.dsl.Words.*

/**
 * This trait provides a domain specific language (DSL) for expressing the Ecscala framework operation using an
 * english-like language.
 */
trait ECScalaDSL extends ExtensionMethodsDSL {

  /**
   * Keyword that enables the use of the word "entity" in the dsl.
   */
  def entity: EntityWord = EntityWord()

  /**
   * Keyword that enables the use of the word "system" in the dsl.
   */
  def system[L <: CList](system: System[L])(using ct: CListTag[L])(using world: World): Unit =
    world.addSystem(system)(using ct)
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

object ViewWrapper {

  class From[L <: CList](using clt: CListTag[L]) {
    def from(world: World): View[L] = world.getView(using clt)
  }

  def getView[L <: CList](using clt: CListTag[L]): From[? <: CList] = From(using clt)
}
