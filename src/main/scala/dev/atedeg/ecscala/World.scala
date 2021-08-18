package dev.atedeg.ecscala

/**
 * This trait represents a container for [[Entity]], Components and System.
 *
 * It's responsible for calling each System registered in it.
 */
trait World {

  /**
   * Return the world size.
   * @return
   *   the number of [[Entity]].
   */
  def size: Int

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
 * Factory for [[dev.atedeg.ecscala.World]] instances.
 */
object World {

  /**
   * Creates a new [[dev.atedeg.ecscala.World]].
   * @return
   *   the world.
   */
  def apply(): World = new WorldImpl()

  private class WorldImpl() extends World {
    private var entities: Set[Entity] = Set()

    override def size: Int = entities.size

    override def createEntity(): Entity = {
      val entity = Entity()
      entities = entities + entity
      entity
    }

    override def removeEntity(entity: Entity): Unit = {
      entities = entities - entity
    }
  }
}
