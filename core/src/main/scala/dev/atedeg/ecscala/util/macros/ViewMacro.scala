package dev.atedeg.ecscala.util.macros

import dev.atedeg.ecscala.util.types.{ CListTag, ComponentTag }
import dev.atedeg.ecscala.util.types
import dev.atedeg.ecscala.{ &:, CList, CNil, Component, Entity, View, World }

object ViewMacro {

  private[ecscala] def createViewImpl[L <: CList](world: World)(using clt: CListTag[L]): View[L] = {
    new View[L] {
      override def iterator = new Iterator[(Entity, L)] {
        val maps: Seq[Map[Entity, Component]] = getMaps(world)(using clt) sortBy (_.size)
        val otherMaps: Seq[Map[Entity, Component]] = if !maps.isEmpty then maps.tail else maps
        var smallerMap: Option[Map[Entity, Component]] = maps.headOption.map(keepCommonKeys(_)(otherMaps))
        var current: Option[Entity] = smallerMap flatMap (_.headOption) map (_.head)

        private def keepCommonKeys[K, V](map: Map[K, V])(maps: Seq[Map[K, V]]) =
          map.filter((k, _) => isInAllMaps(k)(maps))
        private def isInAllMaps[K, V](key: K)(maps: Seq[Map[K, V]]) = maps.forall(_.contains(key))

        override def hasNext = current.isDefined

        override def next() = {
          val result = current
          smallerMap = smallerMap map (_ - current.get)
          current = smallerMap flatMap (_.headOption) map (_.head)
          (result.get, getEntityComponents(world)(result.get)(using clt))
        }
      }
    }
  }

  private def getEntityComponents[L <: CList](world: World)(entity: Entity)(using clt: CListTag[L]): L = {
    // This cast is always safe:
    // the compiler infers the type as Seq[Any], while it is a Seq[? <: Component] so casting it to
    // Seq[Component] is safe
    val components =
      (clt.tags map (world.getComponents(using _)) map (_.get) map (_(entity))).asInstanceOf[Seq[Component]]
    val taggedComponents = components zip clt.tags.asInstanceOf[Seq[ComponentTag[Component]]]
    (taggedComponents
      .foldRight(CNil: CList)((compTag, acc) => &:(compTag.head, acc)(using compTag._2)))
      .asInstanceOf[L]
  }

  private def getMaps[L <: CList](world: World)(using clt: CListTag[L]): Seq[Map[Entity, Component]] = {
    val optionalMaps = clt.tags map (world.getComponents(using _))
    if (optionalMaps.exists(_.isEmpty)) then Seq()
    else optionalMaps map (_.get.asInstanceOf[Map[Entity, Component]])
  }
}
