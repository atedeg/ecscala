package dev.atedeg.ecscala

/**
 * This trait represents an entity of ECS whose state is defined by its components.
 */
sealed trait Entity

/**
 * Factory for [[dev.atedeg.ecscala.Entity]] instances.
 */
object Entity {
  private opaque type Id = Int

  protected[ecscala] def apply(): Entity = EntityImpl(IdGenerator.nextId())

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
