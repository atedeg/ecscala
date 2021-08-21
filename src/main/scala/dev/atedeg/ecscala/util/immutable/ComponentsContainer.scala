package dev.atedeg.ecscala.util.immutable

import dev.atedeg.ecscala.{Component, Entity}
import dev.atedeg.ecscala.util.types.TypeTag

private[ecscala] trait ComponentsContainer {
  def apply[T <: Component: TypeTag]: Option[Map[Entity, T]]
  def addComponent[T <: Component: TypeTag](entityComponentPair: (Entity, T)): ComponentsContainer
  def removeComponent[T <: Component: TypeTag](entityComponentPair: (Entity, T)): ComponentsContainer
  def +[T <: Component: TypeTag](entityComponentPair: (Entity, T)): ComponentsContainer =
    addComponent(entityComponentPair)
  def -[T <: Component: TypeTag](entityComponentPair: (Entity, T)): ComponentsContainer =
    removeComponent(entityComponentPair)
  def removeEntity(entity: Entity): ComponentsContainer
  def -(entity: Entity): ComponentsContainer = removeEntity(entity)
}

private[ecscala] object ComponentsContainer {
  def apply(): ComponentsContainer = new ComponentsContainerImpl
  private def apply(map: Map[TypeTag[? <: Component], Map[Entity, ? <: Component]]) = new ComponentsContainerImpl(map)

  private class ComponentsContainerImpl(
      private val componentsMap: Map[TypeTag[? <: Component], Map[Entity, ? <: Component]] = Map()
  ) extends ComponentsContainer {
    override def apply[T <: Component](using tt: TypeTag[T]) = componentsMap.get(tt).map(_.asInstanceOf[Map[Entity, T]])

    override def addComponent[T <: Component](entityComponentPair: (Entity, T))(using tt: TypeTag[T]) = {
      val newComponentsMap = this.apply[T] match {
        case Some(map) => componentsMap + (tt -> (map + entityComponentPair))
        case None      => componentsMap + (tt -> Map(entityComponentPair))
      }
      ComponentsContainer(newComponentsMap)
    }

    extension [T <: Component](map: Map[Entity, T]) {
      def -(entityComponentPair: (Entity, T)): Map[Entity, T] = {
        val (entity, component) = entityComponentPair
        map filterNot ((e, c) => e == entity && c.eq(component))
      }
    }

    override def removeComponent[T <: Component](entityComponentPair: (Entity, T))(using tt: TypeTag[T]) = {
      val (entity, component) = entityComponentPair
      val newComponentsMap = this.apply[T] match {
        case Some(map) => {
          (map - entityComponentPair) match {
            case m if !m.isEmpty => componentsMap + (tt -> m)
            case _               => componentsMap - tt
          }
        }
        case None => componentsMap
      }
      ComponentsContainer(newComponentsMap)
    }

    override def removeEntity(entity: Entity) = {
//      val newComponentsMap = componentsMap flatMap { (tt, map) =>
//        val newMap = map - entity
//        if newMap.isEmpty then None else Some(tt -> newMap)
//      }
      val newComponentsMap = componentsMap collect {
        case (tt, map) if !(map - entity).isEmpty => tt -> (map - entity)
      }
      ComponentsContainer(newComponentsMap)
    }
  }
}
