package dev.atedeg.ecscala

import scala.annotation.targetName
import dev.atedeg.ecscala.util.immutable.ComponentsContainer
import dev.atedeg.ecscala.util.types.TypeTag

/**
 * This trait represents a container for [[Entity]], Components and System.
 */
trait World {

  /**
   * Return the number of entities in the world.
   * @return
   *   the number of [[Entity]].
   */
  def entitiesCount: Int

  /**
   * Create a new entity and add it to the world.
   * @return
   *   the created entity.
   */
  def createEntity(): Entity

  /**
   * Remove a given entity from the world.
   * @param entity
   *   to remove.
   */
  def removeEntity(entity: Entity): Unit

  private[ecscala] def getComponents[T <: Component: TypeTag]: Option[Map[Entity, T]]

  @targetName("addComponent")
  private[ecscala] def +=[T <: Component: TypeTag](entityComponentPair: (Entity, T)): World

  @targetName("removeComponent")
  private[ecscala] def -=[T <: Component: TypeTag](entityComponentPair: (Entity, T)): World
}

/**
 * Factory for [[World]] instances.
 */
object World {

  /**
   * Creates a new [[World]].
   * @return
   *   the world.
   */
  def apply(): World = new WorldImpl()

  private class WorldImpl() extends World {
    private var entities: Set[Entity] = Set()
    private var componentsContainer = ComponentsContainer()

    override def entitiesCount: Int = entities.size

    override def createEntity(): Entity = {
      val entity = Entity(this)
      entities += entity
      entity
    }

    override def removeEntity(entity: Entity): Unit = {
      entities -= entity
      componentsContainer -= entity
    }

    private[ecscala] override def getComponents[T <: Component: TypeTag] =
      componentsContainer[T]

    @targetName("addComponent")
    private[ecscala] override def +=[T <: Component: TypeTag](entityComponentPair: (Entity, T)): World = {
      componentsContainer += entityComponentPair
      this
    }

    @targetName("removeComponent")
    private[ecscala] override def -=[T <: Component: TypeTag](entityComponentPair: (Entity, T)): World = {
      componentsContainer -= entityComponentPair
      this
    }
  }
}
