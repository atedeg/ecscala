package dev.atedeg.ecscala

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
