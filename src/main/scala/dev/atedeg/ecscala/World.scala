package dev.atedeg.ecscala

/**
 * This trait represents a container for [[Entity]], Components and System.
 */
trait World {

  /**
   * Return number of entity in the world.
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

    override def entitiesCount: Int = entities.size

    override def createEntity(): Entity = {
      val entity = Entity()
      entities += entity
      entity
    }

    override def removeEntity(entity: Entity): Unit = entities -= entity
  }
}
