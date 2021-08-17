package dev.atedeg.ecscala

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec



class ComponentTest extends AnyWordSpec with Matchers{
  "A Component" when {
    "created" should {
      "contains one Entity" in {
        case class Gravity(val g: Double) extends Component(Some(Entity()))
        val b: Gravity = Gravity(9.807)
        b.entity should not be empty
      }
    }
  }

}
