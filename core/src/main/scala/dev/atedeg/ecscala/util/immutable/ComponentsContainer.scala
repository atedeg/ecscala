package dev.atedeg.ecscala.util.immutable

import dev.atedeg.ecscala.{ Component, Entity }
import dev.atedeg.ecscala.util.types.ComponentTag

/**
 * This trait represents a container of multiple [[scala.collection.immutable.Map]] [Entity, T], with T subtype of
 * Component.
 */
private[ecscala] trait ComponentsContainer {

  /**
   * @tparam C
   *   the return map's values' type.
   * @return
   *   a collection of type [[scala.collection.immutable.Map]].
   */
  def apply[C <: Component: ComponentTag]: Option[Map[Entity, C]]

  /**
   * @param entityComponentPair
   *   the (entity, component) pair to add to the container.
   * @tparam C
   *   the type of the [[Component]] to add.
   * @return
   *   a new [[ComponentsContainer]] with the added (entity, component) pair.
   */
  def addComponent[C <: Component: ComponentTag](entityComponentPair: (Entity, C)): ComponentsContainer

  /**
   * An alias for [[addComponent]].
   */
  def +[C <: Component: ComponentTag](entityComponentPair: (Entity, C)): ComponentsContainer =
    addComponent(entityComponentPair)

  /**
   * @param entityComponentPair
   *   the (entity, component) pair to remove from the container.
   * @tparam C
   *   the type of the [[Component]] to remove.
   * @return
   *   a new [[ComponentsContainer]] with the removed (entity, component) pair.
   */
  def removeComponent[C <: Component: ComponentTag](entityComponentPair: (Entity, C)): ComponentsContainer

  /**
   * An alias for [[removeComponent]].
   */
  def -[C <: Component: ComponentTag](entityComponentPair: (Entity, C)): ComponentsContainer =
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

  private def apply(map: Map[ComponentTag[? <: Component], Map[Entity, ? <: Component]]) = new ComponentsContainerImpl(
    map,
  )

  private class ComponentsContainerImpl(
      private val componentsMap: Map[ComponentTag[? <: Component], Map[Entity, ? <: Component]] = Map(),
  ) extends ComponentsContainer {

    override def apply[C <: Component](using ct: ComponentTag[C]) =
      // This cast is needed to return a map with the appropriate type and not a generic "Component" type.
      // It is always safe to perform such a cast since the ComponentTag holds the type of the retrieved map's components.
      componentsMap.get(ct) map (_.asInstanceOf[Map[Entity, C]])

    override def addComponent[C <: Component](entityComponentPair: (Entity, C))(using ct: ComponentTag[C]) = {
      val newComponentMap = this.apply[C] map (_ + entityComponentPair) getOrElse (Map(entityComponentPair))
      val newComponentsMap = componentsMap + (ct -> newComponentMap)
      ComponentsContainer(newComponentsMap)
    }

    extension [C <: Component](map: Map[Entity, C]) {

      def -(entityComponentPair: (Entity, C)): Map[Entity, C] = {
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
      def -?(entityComponentPair: (Entity, C)): Option[Map[Entity, C]] =
        Some(map - entityComponentPair) filter (!_.isEmpty)

      /**
       * @param entity
       *   the [[Entity]] to remove.
       * @return
       *   an [[Option]] with the map with the removed [[Entity]] if the map still has some elements; if the removed
       *   element was the last one and the map would be empty it returns a None.
       */
      def -?(entity: Entity): Option[Map[Entity, C]] = Some(map - entity) filter (_.nonEmpty)
    }

    override def removeComponent[C <: Component](entityComponentPair: (Entity, C))(using ct: ComponentTag[C]) = {
      val newComponentsMap =
        this.apply[C] flatMap (_ -? entityComponentPair) match {
          case Some(componentMap) => componentsMap + (ct -> componentMap)
          case None => componentsMap - ct
        }
      ComponentsContainer(newComponentsMap)
    }

    override def removeEntity(entity: Entity) = {
      val newComponentsMap: Map[ComponentTag[? <: Component], Map[Entity, Component]] =
        componentsMap flatMap { (ct, componentMap) => (componentMap -? entity) map (ct -> _) }
      ComponentsContainer(newComponentsMap)
    }

    override def toString: String = componentsMap.toString
  }
}
