package dev.atedeg.ecscala

import dev.atedeg.ecscala.util.types.ComponentTag

/**
 * This trait represents an [[Entity]] 's feature.
 */
trait Component {
  private var _entity: Option[Entity] = Option.empty
  def entity: Option[Entity] = _entity

  private[ecscala] def setEntity(entity: Option[Entity]): Unit = {
    _entity = entity
  }
}

/**
 * A special [[Component]] type that is used to represent a component removal inside a [[System]]
 */
sealed trait Deleted extends Component
case object Deleted extends Deleted

object Component {

  extension [A <: Component: ComponentTag, B <: Component: ComponentTag](head: A)
    /**
     * Convert two [[Component]] in a [[CList]].
     */
    def &:(otherComp: B): A &: B &: CNil = dev.atedeg.ecscala.&:(head, otherComp &: CNil)
}
