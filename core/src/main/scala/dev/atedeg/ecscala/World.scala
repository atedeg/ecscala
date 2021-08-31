package dev.atedeg.ecscala

import scala.annotation.targetName
import dev.atedeg.ecscala.util.immutable.ComponentsContainer
import dev.atedeg.ecscala.util.macros.Debug
import dev.atedeg.ecscala.util.macros.ViewMacro.createViewImpl
import dev.atedeg.ecscala.util.types.{CListTag, ComponentTag}
import dev.atedeg.ecscala.util.types

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
   *   [[CList]] with the type of the components.
   * @return
   *   the [[View]].
   */
  def getView[L <: CList](using clt: CListTag[L]): View[L]

  /**
   * Add a [[System]] to the [[World]].
   * @param system the system to add.
   * @tparam L the [[CList]] of system components.
   */
  def addSystem[L <: CList](system: System[L])(using tt: CListTag[L]): Unit

  def update(): Unit

  private[ecscala] def getComponents[T <: Component: ComponentTag]: Option[Map[Entity, T]]

  @targetName("addComponent")
  private[ecscala] def +=[T <: Component: ComponentTag](entityComponentPair: (Entity, T)): World

  @targetName("removeComponent")
  private[ecscala] def -=[T <: Component: ComponentTag](entityComponentPair: (Entity, T)): World
}

/**
 * A factory for the [[World]].
 */
object World {
  def apply(): World = new WorldImpl()

  private class WorldImpl() extends World {
    private var entities: Set[Entity] = Set()
    private var componentsContainer = ComponentsContainer()
    private var systems: Set[(CListTag[?], System[? <: CList])] = Set()

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

    override def addSystem[L <: CList](system: System[L])(using tt: CListTag[L]): Unit = systems += (tt -> system)

    override def update(): Unit = systems foreach (tagSystem => {
      val (tt, system) = tagSystem
      system.update(this)(using tt)
    })

    override def toString: String = componentsContainer.toString

    override private[ecscala] def getComponents[T <: Component: ComponentTag]: Option[Map[Entity, T]] =
      componentsContainer[T]

    @targetName("addComponent")
    override private[ecscala] def +=[T <: Component: ComponentTag](entityComponentPair: (Entity, T)): World = {
      componentsContainer += entityComponentPair
      this
    }

    @targetName("removeComponent")
    override private[ecscala] def -=[T <: Component: ComponentTag](entityComponentPair: (Entity, T)): World = {
      componentsContainer -= entityComponentPair
      this
    }
  }
}
