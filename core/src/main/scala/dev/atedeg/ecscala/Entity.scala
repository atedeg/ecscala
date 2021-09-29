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
   * @tparam C
   *   the type of the [[Component]].
   * @return
   *   itself.
   */
  def addComponent[C <: Component: ComponentTag](component: C): Entity

  /**
   * @tparam C
   *   the type of the [[Component]] to be retrieved.
   * @return
   *   the requested component (if present).
   */
  def getComponent[C <: Component: ComponentTag]: Option[C]

  /**
   * @tparam C
   *   the type of the [[Component]] to be removed.
   * @return
   *   itself.
   */
  def removeComponent[C <: Component: ComponentTag]: Entity

  /**
   * @param component
   *   the [[Component]] to remove from the [[Entity]].
   * @tparam C
   *   the type of the [[Component]] to be removed.
   * @return
   *   itself.
   */
  def removeComponent[C <: Component: ComponentTag](component: C): Entity
}

/**
 * Factory for [[dev.atedeg.ecscala.Entity]] instances.
 */
object Entity {
  opaque private type Id = Int

  protected[ecscala] def apply(world: World): Entity = EntityImpl(IdGenerator.nextId(), world)

  private case class EntityImpl(private val id: Id, private val world: World) extends Entity {

    override def addComponent[C <: Component](component: C)(using ct: ComponentTag[C]): Entity = {
      component.setEntity(Some(this))
      world addComponent (this -> component)
      this
    }

    override def getComponent[C <: Component](using ct: ComponentTag[C]): Option[C] =
      world.getComponents(using ct) flatMap (_ get this)

    override def removeComponent[C <: Component](using ct: ComponentTag[C]): Entity = {
      val componentToRemove = world.getComponents(using ct) flatMap (_.get(this))
      componentToRemove match {
        case Some(component) =>
          component.setEntity(None)
          world removeComponent (this -> component)
        case None => ()
      }
      this
    }

    override def removeComponent[C <: Component](component: C)(using ct: ComponentTag[C]): Entity = {
      component.entity match {
        case Some(entity) if entity == this =>
          component.setEntity(None)
          world removeComponent (this -> component)
        case _ => ()
      }
      this
    }

    override def toString: String = s"Entity($id)"

    override def hashCode(): Id = id
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
