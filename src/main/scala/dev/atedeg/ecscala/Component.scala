package dev.atedeg.ecscala

trait Component {
  private var _entity: Option[Entity] = Option.empty
  def entity: Option[Entity] = _entity
  private[ecscala] def setEntity(entity: Option[Entity]): Unit = {
    myEntity = entity
  }
}
