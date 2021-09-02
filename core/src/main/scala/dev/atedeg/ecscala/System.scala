package dev.atedeg.ecscala

import scala.annotation.tailrec
import dev.atedeg.ecscala.{ CList, Entity, View }
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.util.types.{ CListTag, ComponentTag }

trait System[L <: CList] extends Function3[Entity, L, View[L], Deletable[L]] {
  def before(view: View[L]): Unit = {}

  def after(view: View[L]): Unit = {}

  // This method should only be called with a CListTag[L], it can not be enforced in the interface as
  // it would make it impossible to call it correctly from the World requiring to cast the tags to a
  // type that has been erased.
  private[ecscala] def update(world: World)(using clt: CListTag[? <: CList]): Unit = {
    // If the method is called correctly (i.e. only with a CListTag[L]) this cast is always safe
    val castedClt = clt.asInstanceOf[CListTag[L]]
    val view = world.getView(using castedClt)
    before(view)
    view foreach { (entity, components) =>
      val updatedComponents = this.apply(entity, components, view)
      updateComponents(updatedComponents)(entity)(using castedClt)
    }
    after(view)
  }

  private def updateComponents[C <: CList](components: Deletable[C])(entity: Entity)(using clt: CListTag[C]): Unit = {
    val taggedComponents = clt.tags.asInstanceOf[Seq[ComponentTag[Component]]] zip components
    taggedComponents foreach { taggedComponent =>
      val (tt, component) = taggedComponent
      component match {
        case Deleted => entity.removeComponent(using tt)
        case _ => entity.addComponent(component)(using tt)
      }
    }
  }
}
