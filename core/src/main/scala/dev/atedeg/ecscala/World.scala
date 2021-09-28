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
   * Remove all the entites and their respective components from the [[World]]
   */
  def clear(): Unit

  /**
   * A [[View]] on this [[World]] that allows to iterate over its entities with components of the type specified in L.
   * @tparam L
   *   [[CList]] with the types of the components.
   * @return
   *   the [[View]].
   */
  def getView[L <: CList: CListTag]: View[L]

  /**
   * A [[View]] on this [[World]] that allows to iterate over its entities with components of the type specified in
   * LIncluded, that do not have any of the components listed in LExcluded.
   * @tparam LIncluded
   *   [[CList]] with the types of the components that must be present in all entities.
   * @tparam LExcluded
   *   [[CList]] with the types of the components that must not be present in any entity.
   * @return
   *   the [[View]].
   */
  def getView[LIncluded <: CList: CListTag, LExcluded <: CList: CListTag]: ExcludingView[LIncluded, LExcluded]

  /**
   * Add a [[System]] to the [[World]].
   * @param system
   *   the system to add.
   * @tparam L
   *   the [[CList]] of system components.
   */
  def addSystem[L <: CList: CListTag](system: System[L]): Unit

  /**
   * Update the world.
   * @param deltaTime
   *   the time between two updates.
   */
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

    override def clear(): Unit = {
      entities foreach { componentsContainer -= _ }
      entities = Set()
    }

    override def getView[L <: CList](using clt: CListTag[L]): View[L] = View(this)(using clt)

    override def getView[LIncluded <: CList, LExcluded <: CList](using
        cltIncl: CListTag[LIncluded],
        cltExcl: CListTag[LExcluded],
    ): ExcludingView[LIncluded, LExcluded] = View(this)(using cltIncl, cltExcl)

    override def addSystem[L <: CList](system: System[L])(using clt: CListTag[L]): Unit =
      systems = systems :+ (clt -> system)

    override def update(deltaTime: DeltaTime): Unit = systems foreach (taggedSystem => {
      val (ct, system) = taggedSystem
      system(this, deltaTime)
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

    private def convertFunctionToSystem[L <: CList](
        system: (Entity, L, DeltaTime) => Deletable[L],
    )(using clt: CListTag[L]): System[L] =
      new System[L](using clt) {

        override def update(
            entity: Entity,
            components: L,
        )(deltaTime: DeltaTime, world: World, view: View[L]): Deletable[L] = system(entity, components, deltaTime)
      }

    private def convertFunctionToSystem[L <: CList](
        system: (Entity, L, DeltaTime, World, View[L]) => Deletable[L],
    )(using clt: CListTag[L]): System[L] = new System[L](using clt) {

      override def update(
          entity: Entity,
          components: L,
      )(deltaTime: DeltaTime, world: World, view: View[L]): Deletable[L] =
        system(entity, components, deltaTime, world, view)
    }

    private def convertFunctionToSystem[LIncluded <: CList, LExcluded <: CList](
        system: (Entity, LIncluded, DeltaTime) => Deletable[LIncluded],
    )(using cltIncl: CListTag[LIncluded], cltExcl: CListTag[LExcluded]): ExcludingSystem[LIncluded, LExcluded] =
      new ExcludingSystem[LIncluded, LExcluded](using cltIncl, cltExcl) {

        override def update(
            entity: Entity,
            components: LIncluded,
        )(deltaTime: DeltaTime, world: World, view: View[LIncluded]): Deletable[LIncluded] =
          system(entity, components, deltaTime)
      }

    private def convertFunctionToSystem[LIncluded <: CList, LExcluded <: CList](
        system: (Entity, LIncluded, DeltaTime, World, View[LIncluded]) => Deletable[LIncluded],
    )(using cltIncl: CListTag[LIncluded], cltExcl: CListTag[LExcluded]): ExcludingSystem[LIncluded, LExcluded] =
      new ExcludingSystem[LIncluded, LExcluded] {

        override def update(
            entity: Entity,
            components: LIncluded,
        )(deltaTime: DeltaTime, world: World, view: View[LIncluded]): Deletable[LIncluded] =
          system(entity, components, deltaTime, world, view)
      }
  }
}
