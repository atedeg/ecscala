package dev.atedeg.ecscala.util.mutable

import scala.collection.Map
import scala.collection.mutable.AnyRefMap
import dev.atedeg.ecscala.util.types.ComponentTag
import dev.atedeg.ecscala.{ Component, Entity }

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

  private class ComponentsContainerImpl(
      private val componentsMap: AnyRefMap[ComponentTag[? <: Component], AnyRefMap[Entity, ? <: Component]] =
        AnyRefMap(),
  ) extends ComponentsContainer {

    override def apply[C <: Component](using ct: ComponentTag[C]): Option[Map[Entity, C]] = getContainer[C](using ct)

    override def addComponent[C <: Component](entityComponentPair: (Entity, C))(using ct: ComponentTag[C]) = {
      val componentMap: AnyRefMap[Entity, C] =
        getContainer[C] map (_ += entityComponentPair) getOrElse (AnyRefMap(entityComponentPair))
      componentsMap += ct -> componentMap
      this
    }

    extension [C <: Component](map: AnyRefMap[Entity, C]) {

      /**
       * @param entityComponentPair
       *   the pair to remove.
       * @return
       *   an [[Option]] with the map with the removed pair if the map still has some elements; if the removed element
       *   was the last one and the map would be empty it returns a None.
       */
      def -?=(entityComponentPair: (Entity, C)): Option[Map[Entity, C]] = {
        val (e, c) = entityComponentPair
        val containsPair = (map get e filter (_ eq c)).isDefined
        if (containsPair) {
          map -= e
        }
        if map.size > 0 then Some(map) else None
      }

      /**
       * @param entity
       *   the [[Entity]] to remove.
       * @return
       *   an [[Option]] with the map with the removed [[Entity]] if the map still has some elements; if the removed
       *   element was the last one and the map would be empty it returns a None.
       */
      def -?=(entity: Entity): Option[Map[Entity, C]] = {
        map -= entity
        if map.size > 0 then Some(map) else None
      }
    }

    override def removeComponent[C <: Component](entityComponentPair: (Entity, C))(using ct: ComponentTag[C]) = {
      getContainer[C] flatMap (_ -?= entityComponentPair) match {
        case Some(componentMap) => ()
        case None => componentsMap -= ct
      }
      this
    }

    override def removeEntity(entity: Entity) = {
      componentsMap foreachKey { ct =>
        val componentMap = componentsMap(ct)
        componentMap -= entity
      }
      this
    }

    override def toString: String = componentsMap.toString

    private def getContainer[C <: Component](using ct: ComponentTag[C]): Option[AnyRefMap[Entity, C]] = {
      // This cast is needed to return a map with the appropriate type and not a generic "Component" type.
      // It is always safe to perform such a cast since the ComponentTag holds the type of the retrieved map's components.
      componentsMap get ct map (_.asInstanceOf[AnyRefMap[Entity, C]]) filter (!_.isEmpty)
    }
  }
}
