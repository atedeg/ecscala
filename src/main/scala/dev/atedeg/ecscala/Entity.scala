package dev.atedeg.ecscala

sealed trait Entity

object Entity {
  private opaque type Id = Int

  def apply(): Entity = EntityImpl(IdGenerator.nextId())

  private case class EntityImpl(private val id: Id) extends Entity

  private object IdGenerator {
    private var currentId: Id = 0
    def nextId(): Id = synchronized {
      val id = currentId
      currentId += 1
      id
    }
  }
}
