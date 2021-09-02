package dev.atedeg.ecscala

import dev.atedeg.ecscala.util.immutable.ComponentsContainer
import dev.atedeg.ecscala.util.types.ComponentTag

/**
 * This trait represents an entity of ECS whose state is defined by its components.
 */
sealed trait Entity {

  /**
   * @param component
   *   the [[Component]] to add to the [[Entity]].
   * @tparam T
   *   the type of the [[Component]].
   * @return
   *   itself.
   */
  def addComponent[T <: Component: ComponentTag](component: T): Entity

  /**
   * @param component
   *   the [[Component]] to remove from the [[Entity]].
   * @tparam T
   *   the type of the [[Component]] to be removed.
   * @return
   *   itself.
   */
  def removeComponent[T <: Component: ComponentTag]: Entity

  /**
   * @param component
   *   the [[Component]] to remove from the [[Entity]].
   * @tparam T
   *   the type of the [[Component]] to be removed.
   * @return
   *   itself.
   */
  def removeComponent[T <: Component: ComponentTag](component: T): Entity
}

/**
 * Factory for [[dev.atedeg.ecscala.Entity]] instances.
 */
object Entity {
  opaque private type Id = Int

  protected[ecscala] def apply(world: World): Entity = EntityImpl(IdGenerator.nextId(), world)

  private case class EntityImpl(private val id: Id, private val world: World) extends Entity {

    override def addComponent[T <: Component](component: T)(using tt: ComponentTag[T]): Entity = {
      component.setEntity(Some(this))
      world += (this -> component)
      this
    }

    override def removeComponent[T <: Component](using tt: ComponentTag[T]): Entity = {
      val componentToRemove = world.getComponents(using tt) flatMap (_.get(this))
      componentToRemove match {
        case Some(component) =>
          component.setEntity(None)
          world -= (this -> component)
        case None => ()
      }
      this
    }

    override def removeComponent[T <: Component](component: T)(using tt: ComponentTag[T]): Entity = {
      component.entity match {
        case Some(entity) if entity == this =>
          component.setEntity(None)
          world -= (this -> component)
        case _ => ()
      }
      this
    }

    override def toString: String = s"Entity($id)"
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
