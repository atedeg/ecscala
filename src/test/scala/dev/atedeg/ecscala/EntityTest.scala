package dev.atedeg.ecscala

import org.scalatest.matchers.should.*
import org.scalatest.wordspec.AnyWordSpec

class EntityTest extends AnyWordSpec with Matchers {
  "An Entity" should {
    "generate unique IDs" in {
      val ids = (0 until 1000) map { _ => Entity() }
      ids shouldBe ids.distinct
    }
  }
}
