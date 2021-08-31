package dev.atedeg.ecscala.util.types

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.{ &:, CNil }
import dev.atedeg.ecscala.fixtures.{ Position, Velocity }

class CListTagTest extends AnyWordSpec with Matchers {

  "A CListTag" when {
    "has type CNil" should {
      "have no tags" in {
        summon[CListTag[CNil]].tags shouldBe empty
      }
    }
    "has a CList" should {
      "have the appropriate tags" in {
        summon[CListTag[Position &: CNil]].tags shouldBe Seq(summon[ComponentTag[Position]])
        summon[CListTag[Position &: Velocity &: CNil]].tags shouldBe
          Seq(summon[ComponentTag[Position]], summon[ComponentTag[Velocity]])
      }
    }
  }
}
