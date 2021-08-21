package dev.atedeg.ecscala.util

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class EventTest extends AnyWordSpec with Matchers {
  "An Event" when {
    "added an handler" should {
      "execute it" in {
        var executed = false
        var event = Event[Boolean]()
        event += (executed = _)
        event(true)
        executed shouldBe true
      }
    }
    "added multiple handlers" should {
      "executed all of them" in {
        var n = 0
        var event = Event[Int]()
        (0 until 100) foreach (_ => event += (n += _))
        event(1)
        n shouldBe 100
      }
    }
  }
}
