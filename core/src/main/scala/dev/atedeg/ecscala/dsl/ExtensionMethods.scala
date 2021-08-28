package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.util.types.TypeTag
import dev.atedeg.ecscala.{ Component, Entity, World }
import dev.atedeg.ecscala.dsl.EntityWord

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
   * <pre class="stHighlight"> entity + Component() </pre>
   */
  def +[T <: Component](component: T)(using tt: TypeTag[T]): Entity = {
    entity.addComponent(component)(tt)
    entity
  }

  /**
   * This method enables the following syntax:
   *
   * <pre class="stHighlight"> entity - Component() </pre>
   */
  def -[T <: Component](component: T)(using tt: TypeTag[T]): Entity = entity.removeComponent(component)(tt)
}

extension (world: World) {

  /**
   * This method enables the following syntax:
   *
   * <pre class="stHighlight"> world hasAn entity </pre>
   */
  def hasAn(entityWord: EntityWord): Entity = {
    val a = world.createEntity()
    a
  }
}

extension [T <: Component](component: T) {

  def and[C <: Component](c: C)(using e: Entity)(using tt1: TypeTag[T])(using tt2: TypeTag[C]): C = {
    e.addComponent(component)(tt1)
    e.addComponent(c)(tt2)
    c
  }
}
