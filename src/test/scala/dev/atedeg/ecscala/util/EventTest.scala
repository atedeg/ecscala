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
    "removed an handler" should {
      "return a new Event without it" in {
        var s = ""
        var event = Event[String]()
        val handler: String => Unit = (s += _)
        event += handler
        event -= handler
        event("Test")
        s shouldBe ""
      }
    }
    "removed an handler" should {
      "still execute the others" in {
        var n = 0
        var event = Event[Int]()
        val handler: Int => Unit = (n += _)
        val handler2: Int => Unit = (n += _)
        event += handler
        event += handler2
        event -= handler2
        event(1)
        event(1)
        n shouldBe 2
      }
    }

  }
}
