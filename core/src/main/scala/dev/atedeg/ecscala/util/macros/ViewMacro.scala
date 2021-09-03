package dev.atedeg.ecscala.util.macros

import dev.atedeg.ecscala.util.types.{ CListTag, ComponentTag }
import dev.atedeg.ecscala.util.types
import dev.atedeg.ecscala.{ &:, CList, CNil, Component, Entity, View, World }

object ViewMacro {

  private[ecscala] def createViewImpl[L <: CList](world: World)(using clt: CListTag[L]): View[L] = {
    new View[L] {
      override def iterator = new Iterator[(Entity, L)] {
        val taggedMaps: Seq[(ComponentTag[? <: Component], Map[Entity, Component])] =
          getMaps(world)(using clt) sortBy (_.size)
        val maps = taggedMaps map (_._2)
        val otherMaps: Seq[Map[Entity, Component]] = if !maps.isEmpty then maps.tail else maps
        var smallerMap: Option[Map[Entity, Component]] = maps.headOption map (keepCommonKeys(_)(otherMaps))
        var current: Option[Entity] = smallerMap flatMap (_.headOption) map (_.head)

        private def keepCommonKeys[K, V](map: Map[K, V])(maps: Seq[Map[K, V]]) =
          map.filter((k, _) => isInAllMaps(k)(maps))
        private def isInAllMaps[K, V](key: K)(maps: Seq[Map[K, V]]) = maps.forall(_.contains(key))

        override def hasNext = current.isDefined

        override def next() = {
          val result = current
          smallerMap = smallerMap map (_ - current.get)
          current = smallerMap flatMap (_.headOption) map (_.head)
          (result.get, getEntityComponents(taggedMaps)(result.get)(using clt))
        }
      }
    }
  }

  private def getEntityComponents[L <: CList](
      taggedMaps: Seq[(ComponentTag[? <: Component], Map[Entity, ? <: Component])],
  )(entity: Entity)(using clt: CListTag[L]): L = {
    // taggedComponents would otherwise be inferred as a Seq[(ComponentTag[? <: Component], Component)].
    // This cast is necessary since ComponentTag cannot be made covariant (it would hinder the correct
    // compile time inference of ComponentTags' types).
    val taggedComponents = taggedMaps
      .map(taggedMap => taggedMap._1 -> taggedMap._2(entity))
      .asInstanceOf[Seq[(ComponentTag[Component], Component)]]
    // The following cast is always safe since the CList of components is built as a CList
    // of type L (getting the ordered list of components specified with the CListTag[L]).
    taggedComponents
      .foldRight(CNil: CList)((compTag, acc) => &:(compTag._2, acc)(using compTag._1))
      .asInstanceOf[L]
  }

  private def getMaps[L <: CList](
      world: World,
  )(using clt: CListTag[L]): Seq[(ComponentTag[? <: Component], Map[Entity, Component])] = {
    val optionalMaps = clt.tags map (ct => ct -> world.getComponents(using ct))
    if (optionalMaps.exists(_._2.isEmpty)) then Seq()
    else optionalMaps map ((taggedMap) => taggedMap._1 -> taggedMap._2.get.asInstanceOf[Map[Entity, Component]])
  }
}
