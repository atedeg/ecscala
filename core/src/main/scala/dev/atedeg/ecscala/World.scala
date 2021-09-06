package dev.atedeg.ecscala

import scala.annotation.targetName
import dev.atedeg.ecscala.util.immutable.ComponentsContainer
import dev.atedeg.ecscala.util.types.{ CListTag, ComponentTag }

/**
 * A container for [[Entity]], Components and System.
 */
sealed trait World {

  /**
   * @return
   *   the number of [[Entity]] in the [[World]].
   */
  def entitiesCount: Int

  /**
   * Create a new [[Entity]] and add it to the [[World]].
   * @return
   *   the created [[Entity]].
   */
  def createEntity(): Entity

  /**
   * Remove a given [[Entity]] from the [[World]].
   * @param entity
   *   the [[Entity]] to remove.
   */
  def removeEntity(entity: Entity): Unit

  /**
   * A [[View]] on this [[World]] that allows to iterate over its entities with components of the type specified in L.
   * @tparam L
   *   [[CList]] with the types of the components.
   * @return
   *   the [[View]].
   */
  def getView[L <: CList](using clt: CListTag[L]): View[L]

  /**
   * A [[View]] on this [[World]] that allows to iterate over its entities with components of the type specified in
   * LIncluded, that do not have any of the components listed in LEXcluded.
   * @tparam LIncluded
   *   [[CList]] with the types of the components that must be present in all entities.
   * @tparam LExcluded
   *   [[CList]] with the types of the components that must not be present in any entity.
   * @return
   *   the [[View]].
   */
  def getView[LIncluded <: CList, LExcluded <: CList](using
      cltIncl: CListTag[LIncluded],
      cltExcl: CListTag[LExcluded],
  ): ExcludingView[LIncluded, LExcluded]

  /**
   * Add a [[System]] to the [[World]].
   * @param system
   *   the system to add.
   * @tparam L
   *   the [[CList]] of system components.
   */
  def addSystem[L <: CList](system: System[L])(using ct: CListTag[L]): Unit

  def update(deltaTime: DeltaTime): Unit

  private[ecscala] def getComponents[C <: Component: ComponentTag]: Option[Map[Entity, C]]

  @targetName("addComponent")
  private[ecscala] def +=[C <: Component: ComponentTag](entityComponentPair: (Entity, C)): World

  @targetName("removeComponent")
  private[ecscala] def -=[C <: Component: ComponentTag](entityComponentPair: (Entity, C)): World
}

/**
 * A factory for the [[World]].
 */
object World {
  def apply(): World = new WorldImpl()

  private class WorldImpl() extends World {
    private var entities: Set[Entity] = Set()
    private var componentsContainer = ComponentsContainer()
    private var systems: List[(CListTag[?], System[? <: CList])] = List()

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

    override def getView[L <: CList](using clt: CListTag[L]): View[L] = View(this)(using clt)

    override def getView[LIncluded <: CList, LExcluded <: CList](using
        cltIncl: CListTag[LIncluded],
        cltExcl: CListTag[LExcluded],
    ): ExcludingView[LIncluded, LExcluded] = View(this)(using cltIncl, cltExcl)

    override def addSystem[L <: CList](system: System[L])(using ct: CListTag[L]): Unit =
      systems = systems :+ (ct -> system)

    override def update(deltaTime: DeltaTime): Unit = systems foreach (taggedSystem => {
      val (ct, system) = taggedSystem
      system.update(this, deltaTime)(using ct)
    })

    override def toString: String = componentsContainer.toString

    private[ecscala] override def getComponents[C <: Component: ComponentTag]: Option[Map[Entity, C]] =
      componentsContainer[C]

    @targetName("addComponent")
    private[ecscala] override def +=[C <: Component: ComponentTag](entityComponentPair: (Entity, C)): World = {
      componentsContainer += entityComponentPair
      this
    }

    @targetName("removeComponent")
    private[ecscala] override def -=[C <: Component: ComponentTag](entityComponentPair: (Entity, C)): World = {
      componentsContainer -= entityComponentPair
      this
    }
  }
}
