package dev.atedeg.ecscala

import scala.annotation.targetName
import dev.atedeg.ecscala.util.types.{ CListTag, ComponentTag }
import dev.atedeg.ecscala.util.types.given

/**
 * A [[View]] on a [[World]] that allows to iterate over its entities with components of the type specified in L.
 * @tparam L
 *   [[CList]] with the types of the components.
 */
trait View[L <: CList] extends Iterable[(Entity, L)]

/**
 * A [[View]] on this [[World]] that allows to iterate over its entities with components of the type specified in
 * LIncluded, that do not have any of the components listed in LEXcluded.
 * @tparam LIncluded
 *   [[CList]] with the types of the components that must be present in all entities.
 * @tparam LExcluded
 *   [[CList]] with the types of the components that must not be present in any entity.
 */
trait ExcludingView[LIncluded <: CList, LExcluded <: CList] extends View[LIncluded]

private[ecscala] object View {

  def apply[L <: CList](world: World)(using clt: CListTag[L]): View[L] = new View[L] {
    override def iterator: Iterator[(Entity, L)] = new ViewIterator(world)(using clt)
  }

  def apply[LIncluded <: CList, LExcluded <: CList](
      world: World,
  )(using cltIncl: CListTag[LIncluded], cltExcl: CListTag[LExcluded]): ExcludingView[LIncluded, LExcluded] =
    new ExcludingView[LIncluded, LExcluded] {

      override def iterator: Iterator[(Entity, LIncluded)] = new ExcludingViewIterator(world)(using cltIncl, cltExcl)
    }

  private abstract class BaseViewIterator[L <: CList](world: World)(using clt: CListTag[L])
      extends Iterator[(Entity, L)] {

    protected val taggedMaps = getMaps(world)(using clt) sortBy (_.size)
    protected val maps = taggedMaps map (_._2)
    protected val otherMaps = if !maps.isEmpty then maps.tail else maps
    protected val smallerMap = if !maps.isEmpty then maps.head else Map()

    val innerIterator = for {
      entity <- smallerMap.keysIterator if isValid(entity)
    } yield (entity, getEntityComponents(taggedMaps)(entity)(using clt))

    override def hasNext = innerIterator.hasNext

    override def next() = innerIterator.next

    protected def isValid(entity: Entity): Boolean
  }

  private class ViewIterator[L <: CList](world: World)(using clt: CListTag[L])
      extends BaseViewIterator[L](world)(using clt) {
    override protected def isValid(entity: Entity): Boolean = otherMaps.forall(_ contains entity)
  }

  private class ExcludingViewIterator[LIncluded <: CList, LExcluded <: CList](world: World)(using
      cltIncl: CListTag[LIncluded],
      cltExcl: CListTag[LExcluded],
  ) extends ViewIterator[LIncluded](world)(using cltIncl) {

    private val taggedExcludingMaps = getMaps(world)(using cltExcl)
    private val excludingMaps = taggedExcludingMaps map (_._2)

    override protected def isValid(entity: Entity): Boolean =
      otherMaps.forall(_ contains entity) && !excludingMaps.exists(_ contains entity)
  }

  private def getEntityComponents[L <: CList](
      taggedMaps: Seq[(ComponentTag[? <: Component], Map[Entity, ? <: Component])],
  )(entity: Entity)(using clt: CListTag[L]): L = {
    // The val taggedComponents would otherwise be inferred as a Seq[(ComponentTag[? <: Component], Component)].
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
