package dev.atedeg.ecscala

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
    private var _entitiesCount: Int = 0
    
    override def entitiesCount: Int = _entitiesCount

    override def createEntity(): Entity = {
      val entity = Entity()
      _entitiesCount += 1
      entity
    }

    override def removeEntity(entity: Entity): Unit = _entitiesCount -= 1
  }
}
