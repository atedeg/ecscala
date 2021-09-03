package dev.atedeg.ecscala

import scala.annotation.tailrec
import dev.atedeg.ecscala.{ CList, Entity, View }
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.util.types.{ CListTag, ComponentTag }

type DeltaTime = Float

/**
 * Represent a way to iterate over specific components (given by the type parameter L) and manupulate them.
 * @tparam L
 *   a clist representing the Components available to the [[System]].
 */
trait System[L <: CList] extends ((Entity, L, DeltaTime, World, View[L]) => Deletable[L]) {

  /**
   * Describes how this [[System]] updates the components (described by the type of the System) of an [[Entity]].
   * @param entity
   *   the [[Entity]] whose components are being updated
   * @param components
   *   the [[CList]] of Components that are being updated
   * @param deltaTime
   *   the delta time used to update
   * @param world
   *   the [[World]] in which this [[System]] is being used
   * @param view
   *   the [[View]] of all entities with the components described by L
   * @return
   *   a new [[CList]] with the updated components; it could also contain a special component - Deleted - that is used
   *   to delete the corresponding component: e.g. If the expected return type is a {{{Position &: Velocity &: CNil}}}
   *   one could also return {{{Position(1, 2) &: Deleted &: CNil}}} Resulting in the removal of the Velocity Component
   *   from the given Entity.
   */
  override def apply(entity: Entity, components: L, deltaTime: DeltaTime, world: World, view: View[L]): Deletable[L]

  /**
   * This method is executed before each iteration of the [[System]].
   * @param world
   *   the [[World]] in which the [[System]] is being executed.
   * @param view
   *   a [[View]] with the Components specified by the [[System]] type.
   */
  def before(world: World, view: View[L]): Unit = {}

  /**
   * This method is executed after each iteration of the [[System]]
   * @param world
   *   the [[World]] in which the [[System]] is being executed.
   * @param view
   *   a [[View]] with the Components specified by the [[System]] type.
   */
  def after(world: World, view: View[L]): Unit = {}

  /**
   * This method should only be called with a CListTag[L], it can not be enforced in the interface as it would make it
   * impossible to call it correctly from the World requiring to cast the tags to a type that has been erased.
   */
  private[ecscala] def update(world: World, deltaTime: Float)(using clt: CListTag[? <: CList]): Unit = {
    // If the method is called correctly (i.e. only with a CListTag[L]) this cast is always safe
    val castedClt = clt.asInstanceOf[CListTag[L]]
    val view = world.getView(using castedClt)
    before(world, view)
    view foreach { (entity, components) =>
      val updatedComponents = this.apply(entity, components, deltaTime, world, view)
      updateComponents(updatedComponents)(entity)(using castedClt)
    }
    after(world, view)
  }

  private def updateComponents[L <: CList](components: Deletable[L])(entity: Entity)(using clt: CListTag[L]): Unit = {
    val taggedComponents = clt.tags.asInstanceOf[Seq[ComponentTag[Component]]] zip components
    taggedComponents foreach { taggedComponent =>
      val (ct, component) = taggedComponent
      component match {
        case Deleted => entity.removeComponent(using ct)
        case _ => entity.addComponent(component)(using ct)
      }
    }
  }
}
