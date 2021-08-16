package dev.atedeg.ecscala

type EntityId = Int

object EntityIdGenerator {
  private var currentId: EntityId = 0
  def nextId(): EntityId = synchronized {
    val id = currentId
    currentId += 1
    id
  }
}

trait Entity

object Entity {
  def apply(): Entity = EntityImpl(EntityIdGenerator.nextId())
  private case class EntityImpl(private val id: EntityId) extends Entity
}
