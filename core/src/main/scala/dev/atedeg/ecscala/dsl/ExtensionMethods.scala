package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.util.types.TypeTag
import dev.atedeg.ecscala.{ Component, Entity, World }
import dev.atedeg.ecscala.dsl.Words.*

trait ExtensionMethodsDSL {

  extension (entity: Entity) {

    /**
     * This method enables the following syntax:
     *
     * <pre class="stHighlight"> entity withComponents {
     *
     * Component1() and Component2()
     *
     * } </pre>
     */
    def withComponents(init: Entity ?=> Unit): Entity = {
      given e: Entity = entity

      init
      e
    }

    /**
     * This method enables the following syntax:
     *
     * <pre class="stHighlight"> entity withComponent Component()</pre>
     */
    def withComponent[T <: Component: TypeTag](component: T): Entity = entity.addComponent(component)

    /**
     * This method enables the following syntax:
     *
     * <pre class="stHighlight"> entity + Component() </pre>
     */
    def +[T <: Component: TypeTag](component: T): Entity = entity.addComponent(component)

    /**
     * This method enables the following syntax:
     *
     * <pre class="stHighlight"> entity - Component() </pre>
     */
    def -[T <: Component: TypeTag](component: T): Entity = entity.removeComponent(component)
  }

  extension (world: World) {

    /**
     * This method enables the following syntax:
     *
     * <pre class="stHighlight"> world hasAn entity </pre>
     */
    def hasAn(entityWord: EntityWord): Entity = world.createEntity()
  }

  extension [T <: Component: TypeTag](component: T) {

    /**
     * This method adds the current component and its agrument to an entity and enables the following syntax:
     *
     * <pre class="stHighlight"> entity withComponents {
     *
     * Component1() and Component2()
     *
     * } </pre>
     *
     * @param rightComponent
     *   The component to be added to an entity.
     * @return
     *   A [[ComponentWrapper]] that enables the components chaining.
     */
    def and[C <: Component](rightComponent: C)(using entity: Entity)(using tt: TypeTag[C]): ComponentWrapper = {
      entity.addComponent(component)
      entity.addComponent(rightComponent)(tt)
      ComponentWrapper()
    }
  }
}
