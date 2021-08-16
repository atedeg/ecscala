package dev.atedeg.ecscala

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.*

class EntityIdGeneratorTest extends AnyWordSpec with Matchers {
  "An IdGenerator" should {
    "generate unique IDs" in {
      val ids = (0 until 1000).map(_ => EntityIdGenerator.nextId())
      ids shouldBe ids.distinct
    }
  }
}
