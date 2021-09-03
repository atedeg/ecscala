package dev.atedeg.ecscala

import scala.annotation.tailrec
import dev.atedeg.ecscala.{ CList, Entity, View }
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.util.types.{ CListTag, ComponentTag }

/**
 * Represent a way to iterate over specific components (given by the type parameter L) and manupulate them.
 * @tparam L
 *   a clist representing the Components available to the [[System]].
 */
trait System[L <: CList] extends Function3[Entity, L, View[L], Deletable[L]] {

  /**
   * This method is executed before each iteration of the [[System]].
   * @param world
   *   the world.
   * @param view
   *   a [[View]] with the same Components of the system.
   */
  def before(world: World, view: View[L]): Unit = {}

  /**
   * This method is executed after each iteration of the [[System]]
   * @param world
   *   the world.
   * @param view
   *   a [[View]] with the same Components of the system.
   */
  def after(world: World, view: View[L]): Unit = {}

  /**
   * This method should only be called with a CListTag[L], it can not be enforced in the interface as it would make it
   * impossible to call it correctly from the World requiring to cast the tags to a type that has been erased.
   */
  private[ecscala] def update(world: World)(using clt: CListTag[? <: CList]): Unit = {
    // If the method is called correctly (i.e. only with a CListTag[L]) this cast is always safe
    val castedClt = clt.asInstanceOf[CListTag[L]]
    val view = world.getView(using castedClt)
    before(world, view)
    view foreach { (entity, components) =>
      val updatedComponents = this.apply(entity, components, view)
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
