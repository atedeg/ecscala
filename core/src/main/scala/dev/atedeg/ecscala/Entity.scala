package dev.atedeg.ecscala

import dev.atedeg.ecscala.util.Event
import dev.atedeg.ecscala.util.immutable.ComponentsContainer
import dev.atedeg.ecscala.util.types.TypeTag

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
  def addComponent[T <: Component: TypeTag](component: T): Entity

  /**
   * @param component
   *   the [[Component]] to remove from the [[Entity]].
   * @tparam T
   *   the type of the [[Component]].
   * @return
   *   itself.
   */
  def removeComponent[T <: Component: TypeTag](component: T): Entity
}

/**
 * Factory for [[dev.atedeg.ecscala.Entity]] instances.
 */
object Entity {
  opaque private type Id = Int

  protected[ecscala] def apply(world: World): Entity = EntityImpl(IdGenerator.nextId(), world)

  private case class EntityImpl(private val id: Id, private val world: World) extends Entity {

    override def addComponent[T <: Component](component: T)(using tt: TypeTag[T]): Entity = {
      component.setEntity(Some(this))
      world += (this -> component)
      this
    }

    override def removeComponent[T <: Component](component: T)(using tt: TypeTag[T]): Entity = {
      component.setEntity(None)
      world -= (this -> component)
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
