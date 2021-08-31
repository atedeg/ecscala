package dev.atedeg.ecscala.util.immutable

import dev.atedeg.ecscala.{ Component, Entity }
import dev.atedeg.ecscala.util.types.ComponentTag

/**
 * This trait represents a container of multiple [[scala.collection.immutable.Map]] [Entity, T], with T subtype of
 * Component.
 */
private[ecscala] trait ComponentsContainer {

  /**
   * @tparam T
   *   the return map's values' type.
   * @return
   *   a collection of type [[scala.collection.immutable.Map]] [Entity, T].
   */
  def apply[T <: Component: ComponentTag]: Option[Map[Entity, T]]

  /**
   * @param entityComponentPair
   *   the (entity, component) pair to add to the container.
   * @tparam T
   *   the type of the [[Component]] to add.
   * @return
   *   a new [[ComponentsContainer]] with the added (entity, component) pair.
   */
  def addComponent[T <: Component: ComponentTag](entityComponentPair: (Entity, T)): ComponentsContainer

  /**
   * An alias for [[addComponent]].
   */
  def +[T <: Component: ComponentTag](entityComponentPair: (Entity, T)): ComponentsContainer =
    addComponent(entityComponentPair)

  /**
   * @param entityComponentPair
   *   the (entity, component) pair to remove from the container.
   * @tparam T
   *   the type of the [[Component]] to remove.
   * @return
   *   a new [[ComponentsContainer]] with the removed (entity, component) pair.
   */
  def removeComponent[T <: Component: ComponentTag](entityComponentPair: (Entity, T)): ComponentsContainer

  /**
   * An alias for [[removeComponent]].
   */
  def -[T <: Component: ComponentTag](entityComponentPair: (Entity, T)): ComponentsContainer =
    removeComponent(entityComponentPair)

  /**
   * @param entity
   *   the [[Entity]] to remove from the container.
   * @return
   *   a [[ComponentsContainer]] with the removed entity and all its components.
   */
  def removeEntity(entity: Entity): ComponentsContainer

  /**
   * An alias for [[removeEntity]].
   */
  def -(entity: Entity): ComponentsContainer = removeEntity(entity)
}

private[ecscala] object ComponentsContainer {
  def apply(): ComponentsContainer = new ComponentsContainerImpl
  private def apply(map: Map[ComponentTag[? <: Component], Map[Entity, ? <: Component]]) = new ComponentsContainerImpl(map)

  private class ComponentsContainerImpl(
                                         private val componentsMap: Map[ComponentTag[? <: Component], Map[Entity, ? <: Component]] = Map(),
  ) extends ComponentsContainer {

    override def apply[T <: Component](using tt: ComponentTag[T]) =
      // This cast is needed to return a map with the appropriate type and not a generic "Component" type.
      // It is always safe to perform such a cast since the ComponentTag holds the type of the retrieved map's components.
      componentsMap.get(tt) map (_.asInstanceOf[Map[Entity, T]])

    override def addComponent[T <: Component](entityComponentPair: (Entity, T))(using tt: ComponentTag[T]) = {
      val newComponentMap = this.apply[T] map (_ + entityComponentPair) getOrElse (Map(entityComponentPair))
      val newComponentsMap = componentsMap + (tt -> newComponentMap)
      ComponentsContainer(newComponentsMap)
    }

    extension [T <: Component](map: Map[Entity, T]) {

      def -(entityComponentPair: (Entity, T)): Map[Entity, T] = {
        val (entity, component) = entityComponentPair
        // The components are compared using the eq method because, when removing elements, two components are
        // considered to be equal only if they are the same object.
        map filterNot ((e, c) => e == entity && c.eq(component))
      }

      /**
       * @param entityComponentPair
       *   the pair to remove.
       * @return
       *   an [[Option]] with the map with the removed pair if the map still has some elements; if the removed element
       *   was the last one and the map would be empty it returns a None.
       */
      def -?(entityComponentPair: (Entity, T)): Option[Map[Entity, T]] =
        Some(map - entityComponentPair) filter (!_.isEmpty)

      /**
       * @param entity
       *   the [[Entity]] to remove.
       * @return
       *   an [[Option]] with the map with the removed [[Entity]] if the map still has some elements; if the removed
       *   element was the last one and the map would be empty it returns a None.
       */
      def -?(entity: Entity): Option[Map[Entity, T]] = Some(map - entity) filter (_.nonEmpty)
    }

    override def removeComponent[T <: Component](entityComponentPair: (Entity, T))(using tt: ComponentTag[T]) = {
      val newComponentsMap =
        this.apply[T] flatMap (_ -? entityComponentPair) match {
          case Some(componentMap) => componentsMap + (tt -> componentMap)
          case None => componentsMap - tt
        }
      ComponentsContainer(newComponentsMap)
    }

    override def removeEntity(entity: Entity) = {
      val newComponentsMap: Map[ComponentTag[? <: Component], Map[Entity, Component]] =
        componentsMap flatMap { (tt, componentMap) => (componentMap -? entity) map (tt -> _) }
      ComponentsContainer(newComponentsMap)
    }

    override def toString: String = componentsMap.toString
  }
}
