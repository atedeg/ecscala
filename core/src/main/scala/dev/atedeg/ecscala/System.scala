package dev.atedeg.ecscala

import dev.atedeg.ecscala.util.macros.Debug

import scala.annotation.tailrec
import dev.atedeg.ecscala.{ CList, Entity, View }
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.util.types.{ CListTag, TypeTag }

trait System[L <: CList] extends Function3[Entity, L, View[L], L] {

  private[ecscala] def update(world: World)(using clt: CListTag[? <: CList]): Unit = {
    val view = world.getView(using clt).asInstanceOf[View[L]]
    view foreach { (entity, components) =>
      val updatedComponents = this.apply(entity, components, view)
      updateComponents(updatedComponents)(entity)(using clt.asInstanceOf[CListTag[L]])
    }
  }

  private def updateComponents[C <: CList](components: C)(entity: Entity)(using clt: CListTag[C]): Unit = {
    val taggedComponents = clt.tags.asInstanceOf[Seq[TypeTag[Component]]] zip components
    taggedComponents foreach { taggedComponent =>
      val (tt, component) = taggedComponent
      entity.addComponent(component)(using tt)
    }
//    components match {
//      case head &: tail =>
//        entity.addComponent(head)
//        updateComponents(tail)(entity)
//      case _ => ()
//    }
  }
}