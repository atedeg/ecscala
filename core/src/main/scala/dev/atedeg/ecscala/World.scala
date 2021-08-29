package dev.atedeg.ecscala

import scala.annotation.targetName
import dev.atedeg.ecscala.util.immutable.ComponentsContainer
import dev.atedeg.ecscala.util.macros.ViewMacro.createViewImpl
import dev.atedeg.ecscala.util.types.TypeTag

/**
 * A container for [[Entity]], Components and System.
 */
// world could not be a trait since the inline method getView would be retained and not correctly
// inlined when calling world.getView.
// An alterative solution could be declare the getView as an abstract inline method in the trait;
// however, the library user could not call such method on any val typed as the trait.
// For more details on this problem refer to: https://docs.scala-lang.org/scala3/guides/macros/inline.html
class World() {
  private var entities: Set[Entity] = Set()
  private var componentsContainer = ComponentsContainer()

  /**
   * @return
   *   the number of [[Entity]] in the [[World]].
   */
  def entitiesCount: Int = entities.size

  /**
   * Create a new [[Entity]] and add it to the [[World]].
   * @return
   *   the created [[Entity]].
   */
  def createEntity(): Entity = {
    val entity = Entity(this)
    entities += entity
    entity
  }

  /**
   * Remove a given [[Entity]] from the [[World]].
   * @param entity
   *   the [[Entity]] to remove.
   */
  def removeEntity(entity: Entity): Unit = {
    entities -= entity
    componentsContainer -= entity
  }

  /**
   * A [[View]] on this [[World]] that allows to iterate over its entities with components of the type specified in L.
   * @tparam L
   *   [[CList]] with the type of the components.
   * @return
   *   the [[View]].
   */
  inline def getView[L <: CList]: View[L] = View[L](this)

  /**
   * A [[View]] on this [[World]] that allows to iterate over its entities with a [[Component]] of type C.
   * @tparam C
   *   the type of the [[Component]] that entities in this [[World]] must have.
   * @return
   *   the [[View]].
   */
  @targetName("getViewFromSingleComponentType")
  inline def getView[C <: Component]: View[C &: CNil] = View[C](this)

  private[ecscala] def getComponents[T <: Component: TypeTag] =
    componentsContainer[T]

  @targetName("addComponent")
  private[ecscala] def +=[T <: Component: TypeTag](entityComponentPair: (Entity, T)): World = {
    componentsContainer += entityComponentPair
    this
  }

  @targetName("removeComponent")
  private[ecscala] def -=[T <: Component: TypeTag](entityComponentPair: (Entity, T)): World = {
    componentsContainer -= entityComponentPair
    this
  }
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
  def apply(): World = new World()
}
