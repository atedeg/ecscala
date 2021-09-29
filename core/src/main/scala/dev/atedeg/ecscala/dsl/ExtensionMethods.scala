package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.util.types.{ CListTag, ComponentTag }
import dev.atedeg.ecscala.{ CList, Component, Entity, System, World }
import dev.atedeg.ecscala.dsl.Words.EntityWord
import dev.atedeg.ecscala.util.types.given

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
      componentList zip clt.tags.asInstanceOf[Seq[ComponentTag[Component]]] foreach { entity.addComponent(_)(using _) }
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
    def +=[C <: Component: ComponentTag](component: C): Entity = entity.addComponent(component)

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
}
