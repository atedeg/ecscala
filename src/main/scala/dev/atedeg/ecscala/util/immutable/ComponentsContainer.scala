package dev.atedeg.ecscala.util.immutable

import dev.atedeg.ecscala.{Component, Entity}
import dev.atedeg.ecscala.util.types.TypeTag

/**
 * This trait represents a container of multiple [[scala.collection.immutable.Map]] [Entity, T], with T subtype of
 * Component.
 */
private[ecscala] trait ComponentsContainer {

  /**
   * @tparam T
   *   the return map's values' type
   * @return
   *   A collection of type [[scala.collection.immutable.Map]] [Entity, T]
   */
  def apply[T <: Component: TypeTag]: Option[Map[Entity, T]]

  /**
   * @param entityComponentPair
   *   the (entity, component) pair to add to the container
   * @tparam T
   *   the type of the [[Component]] to add
   * @return
   *   A new [[ComponentsContainer]] with the added (entity, component) pair
   */
  def addComponent[T <: Component: TypeTag](entityComponentPair: (Entity, T)): ComponentsContainer

  /**
   * @see
   *   [[addComponent]]
   */
  def +[T <: Component: TypeTag](entityComponentPair: (Entity, T)): ComponentsContainer =
    addComponent(entityComponentPair)

  /**
   * @param entityComponentPair
   *   the (entity, component) pair to remove from the container
   * @tparam T
   *   the type of the [[Component]] to remove
   * @return
   *   A new [[ComponentsContainer]] with the removed (entity, component) pair
   */
  def removeComponent[T <: Component: TypeTag](entityComponentPair: (Entity, T)): ComponentsContainer

  /**
   * @see
   *   [[removeComponent]]
   */
  def -[T <: Component: TypeTag](entityComponentPair: (Entity, T)): ComponentsContainer =
    removeComponent(entityComponentPair)

  /**
   * @param entity
   *   the [[Entity]] to remove from the container
   * @return
   *   A [[ComponentsContainer]] with the removed entity and all its components
   */
  def removeEntity(entity: Entity): ComponentsContainer

  /**
   * @see
   *   [[removeEntity]]
   */
  def -(entity: Entity): ComponentsContainer = removeEntity(entity)
}

private[ecscala] object ComponentsContainer {
  def apply(): ComponentsContainer = new ComponentsContainerImpl
  private def apply(map: Map[TypeTag[? <: Component], Map[Entity, ? <: Component]]) = new ComponentsContainerImpl(map)

  private class ComponentsContainerImpl(
      private val componentsMap: Map[TypeTag[? <: Component], Map[Entity, ? <: Component]] = Map()
  ) extends ComponentsContainer {
    override def apply[T <: Component](using tt: TypeTag[T]) =
      componentsMap.get(tt) map (_.asInstanceOf[Map[Entity, T]])

    override def addComponent[T <: Component](entityComponentPair: (Entity, T))(using tt: TypeTag[T]) = {
      val newComponentMap = this.apply[T] map (_ + entityComponentPair) getOrElse (Map(entityComponentPair))
      val newComponentsMap = componentsMap + (tt -> newComponentMap)
      ComponentsContainer(newComponentsMap)
    }

    extension [T <: Component](map: Map[Entity, T]) {
      def -(entityComponentPair: (Entity, T)): Map[Entity, T] = {
        val (entity, component) = entityComponentPair
        map filterNot ((e, c) => e == entity && c.eq(component))
      }

      def -?(entityComponentPair: (Entity, T)): Option[Map[Entity, T]] =
        Some(map - entityComponentPair) filter (!_.isEmpty)

      def -?(entity: Entity): Option[Map[Entity, T]] = Some(map - entity) filter (!_.isEmpty)
    }

    override def removeComponent[T <: Component](entityComponentPair: (Entity, T))(using tt: TypeTag[T]) = {
      val newComponentsMap =
        this.apply[T] flatMap (_ -? entityComponentPair) match {
          case Some(componentMap) => componentsMap + (tt -> componentMap)
          case None               => componentsMap - tt
        }
      ComponentsContainer(newComponentsMap)
    }

    override def removeEntity(entity: Entity) = {
      val newComponentsMap = componentsMap flatMap { (tt, componentMap) => (componentMap -? entity) map (tt -> _) }
      ComponentsContainer(newComponentsMap)
    }
  }
}