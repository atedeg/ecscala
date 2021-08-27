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
   * *(Component1())
   *
   * *(Component2())
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
