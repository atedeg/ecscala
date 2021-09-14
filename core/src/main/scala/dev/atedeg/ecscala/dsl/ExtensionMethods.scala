package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.util.types.{ CListTag, ComponentTag }
import dev.atedeg.ecscala.{ CList, Component, Entity, System, World }
import dev.atedeg.ecscala.dsl.Words.EntityWord

trait ExtensionMethodsDSL {

  extension (entity: Entity) {

    /**
     * This method enables the following syntax:
     *
     * {{{
     * entity withComponents { Component1() and Component2() }
     * }}}
     */
    def withComponents(map: => Map[ComponentTag[? <: Component], Component]): Entity = {
      map foreach { (ct, component) => entity.addComponent(component)(using ct.asInstanceOf[ComponentTag[Component]]) }
      entity
    }

    /**
     * This method enables the following syntax:
     *
     * {{{
     * entity withComponent Component()
     * }}}
     */
    def withComponent[C <: Component: ComponentTag](component: C): Entity = entity.addComponent(component)

    /**
     * This method enables the following syntax:
     *
     * {{{
     * entity + Component()
     * }}}
     */
    def +[C <: Component: ComponentTag](component: C): Entity = entity.addComponent(component)

    /**
     * This method enables the following syntax:
     *
     * {{{
     * entity - Component()
     * }}}
     */
    def -[C <: Component: ComponentTag](component: C): Entity = entity.removeComponent(component)
  }

  extension (world: World) {

    /**
     * This method enables the following syntax:
     *
     * {{{
     * world - entity
     * }}}
     */
    def -(entity: Entity) = world.removeEntity(entity)

    /**
     * This method enables the following syntax:
     *
     * {{{
     * world hasAn entity
     * }}}
     */
    def hasAn(entityWord: EntityWord): Entity = world.createEntity()

    /**
     * This method enables the following syntax:
     *
     * {{{
     * world hasA system[Component &: CNil]{ () => {} }
     * }}}
     */
    def hasA(init: World ?=> Unit): Unit = {
      given w: World = world
      init
    }
  }

  extension [A <: Component](component: A)(using ctA: ComponentTag[A]) {

    /**
     * This method enables the two following syntax:
     *
     * {{{
     *   * entity withComponents { Component1() and Component2() }
     * }}}
     * {{{
     *   * remove { Component1() and Component2() } from myEntity
     * }}}
     */
    def and[B <: Component](
        rightComponent: B,
    )(using ctB: ComponentTag[B]): Map[ComponentTag[? <: Component], Component] = {
      Map(ctB -> rightComponent, ctA -> component)
    }
  }

  extension (componentsMap: Map[ComponentTag[? <: Component], Component]) {

    /**
     * This method enables the two following syntax:
     *
     * {{{
     *   * entity withComponents { Component1() and Component2() }
     * }}}
     * {{{
     *   * remove { Component1() and Component2() } from myEntity
     * }}}
     */
    def and[B <: Component](
        rightComponent: B,
    )(using ct: ComponentTag[B]): Map[ComponentTag[? <: Component], Component] = {
      componentsMap + (ct -> rightComponent)
    }
  }
}
