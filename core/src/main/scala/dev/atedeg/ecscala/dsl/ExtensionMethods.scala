package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.util.types.{CListTag, ComponentTag}
import dev.atedeg.ecscala.{CList, Component, Entity, System, World}
import dev.atedeg.ecscala.dsl.Words.*

import scala.annotation.showAsInfix

trait ExtensionMethodsDSL {

  extension (entity: Entity) {

    /**
     * This method enables the following syntax:
     *
     * {{{
     * entity withComponents {
     *
     *   Component1() and Component2()
     *
     * }
     * }}}
     */
    def withComponents(init: Entity ?=> Unit): Entity = {
      given e: Entity = entity
      init
      e
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
     * world hasAn entity
     * }}}
     */
    def hasAn(entityWord: EntityWord): Entity = world.createEntity()

//    /**
//     * This method enables the following syntax:
//     *
//     * {{{
//     * world hasNoMore myEntity
//     * }}}
//     */
//    def hasNoMore(entity: Entity): Unit = world.removeEntity(entity)

    /**
     * This method enables the following syntax:
     *
     * {{{
     * world hasA system[Component &: CNil]()
     * }}}
     */
    def hasA(init: World ?=> Unit): Unit = {
      given w: World = world
      init
    }
  }

  extension [A <: Component: ComponentTag](component: A) {

    /**
     * This method adds the current component and its agrument to an entity and enables the following syntax:
     *
     * {{{
     * entity withComponents {
     *
     *   Component1() and Component2()
     *
     * }
     * }}}
     *
     * @param rightComponent
     *   The component to be added to an entity.
     * @return
     *   A [[ComponentWrapper]] that enables the components chaining.
     */
    def and[B <: Component](rightComponent: B)(using entity: Entity)(using ct: ComponentTag[B]): ComponentWrapper = {
      entity.addComponent(component)
      entity.addComponent(rightComponent)(ct)
      ComponentWrapper()
    }
  }
}
