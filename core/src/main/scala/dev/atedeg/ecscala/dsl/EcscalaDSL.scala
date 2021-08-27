package dev.atedeg.ecscala.dsl
import dev.atedeg.ecscala.World

trait EcscalaDSL {
  def entity: EntityWord = EntityWord()
}

case class EntityWord()
