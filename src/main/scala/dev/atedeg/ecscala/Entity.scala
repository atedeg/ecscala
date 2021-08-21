package dev.atedeg.ecscala

import dev.atedeg.ecscala.util.Event
import dev.atedeg.ecscala.util.immutable.ComponentsContainer
import dev.atedeg.ecscala.util.types.TypeTag

/**
 * This trait represents an entity of ECS whose state is defined by its components.
 */
sealed trait Entity {
  
  /**
   * @param component the [[Component]] to add to the [[Entity]].
   * @tparam T the type of the [[Component]].
   * @return itself.
   */
  def addComponent[T <: Component](component: T): Entity

  /**
   * @param handler the handler to execute when a [[Component]] is added to this entity.
   * @return itself.
   */
  def onAddedComponent(handler: ((Entity, Component)) => Unit): Entity
}

/**
 * Factory for [[dev.atedeg.ecscala.Entity]] instances.
 */
object Entity {
  private opaque type Id = Int

  protected[ecscala] def apply(): Entity = EntityImpl(IdGenerator.nextId())

  private case class EntityImpl(private val id: Id) extends Entity {
    private var onAddedComponentEvent: Event[(Entity, Component)] = Event()

    override def addComponent[T <: Component](component: T): Entity = {
      onAddedComponentEvent(this, component)
      this
    }

    override def onAddedComponent(handler: ((Entity, Component)) => Unit): Entity = {
      onAddedComponentEvent += handler
      this
    }
  }

  private object IdGenerator {
    private var currentId: Id = 0
    def nextId(): Id = synchronized {
      val id = currentId
      currentId += 1
      id
    }
  }
}
