package dev.atedeg.ecscala

trait World {
  def size: Int
  def createEntity(): Entity
  def removeEntity(entity: Entity): Unit
}

object World {
  def apply(): World = new WorldImpl()
  private class WorldImpl() extends World {
    private var entities: Seq[Entity] = Seq()

    override def size: Int = entities.size

    override def createEntity(): Entity = {
      val entity = Entity()
      entities = entities :+ entity
      entity
    }

    override def removeEntity(entity: Entity): Unit = {
      entities = entities filter { _ != entity }
    }
  }
}
