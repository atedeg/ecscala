package dev.atedeg.ecscala

type EntityId

object EntityIdGenerator {
  opaque type EntityId = Int
  private var currentId: EntityId = 0
  def nextId(): EntityId = synchronized {
    val id = currentId
    currentId += 1
    id
  }
}
