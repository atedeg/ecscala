package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.{ CList, Component, Entity, System, World }
import dev.atedeg.ecscala.dsl.Words.EntityWord
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.util.types.{ CListTag, ComponentTag, taggedWith }

trait ExtensionMethods {

  extension (entity: Entity) {

    /**
     * This method enables the following syntax:
     *
     * {{{
     * entity withComponents { Component1() &: Component2() }
     * }}}
     */
    def withComponents[L <: CList](componentList: L)(using clt: CListTag[L]): Entity = {
      componentList.taggedWith(clt) foreach { entity.setComponent(_)(using _) }
      entity
    }

    /**
     * This method enables the following syntax:
     *
     * {{{
     * entity withComponent Component()
     * }}}
     */
    def withComponent[C <: Component: ComponentTag](component: C): Entity = entity setComponent component

    /**
     * This method enables the following syntax:
     *
     * {{{
     * entity += Component()
     * }}}
     */
    def +=[C <: Component: ComponentTag](component: C): Entity = entity setComponent component

    /**
     * This method enables the following syntax:
     *
     * {{{
     * entity -= Component()
     * }}}
     */
    def -=[C <: Component: ComponentTag](component: C): Entity = entity.removeComponent(component)
  }

  extension (world: World) {

    /**
     * This method enables the following syntax:
     *
     * {{{
     * world -= myEntity
     * }}}
     */
    def -=(entity: Entity) = world.removeEntity(entity)

    /**
     * This method enables the following syntax:
     *
     * {{{
     * world += mySystem
     * }}}
     */
    def +=(system: System) = world.addSystem(system)

    /**
     * This method enables the following syntax:
     *
     * {{{
     * world -= mySystem
     * }}}
     */
    def -=(system: System) = world.removeSystem(system)

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
}
