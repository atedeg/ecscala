package dev.atedeg.ecscalademo

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MathTest extends AnyWordSpec with Matchers {

  "A point" when {
    "adding a vector" should {
      "return a point with summed components" in {
        Point(1, 1) + Vector(2, 3) shouldBe Point(3, 4)
      }
    }
    "subtracting a vector" should {
      "return a point with subtracted components" in {
        Point(1, 1) - Vector(2, 3) shouldBe Point(-1, -2)
      }
    }
    "subtracting a point" should {
      "return a vector with subtracted components" in {
        Point(2, 3) - Point(1, 1) shouldBe Vector(1, 2)
      }
    }
  }

  "A vector" when {
    "adding another vector" should {
      "return the correct result" in {
        Vector(1, 1) + Vector(2, 2) shouldBe Vector(3, 3)
      }
      "subtracting another vector" should {
        "return the correct result" in {
          Vector(1, 1) - Vector(2, 2) shouldBe Vector(-1, -1)
        }
      }
      "computing the dot product" should {
        "return the correct result" in {
          Vector(1, 2) dot Vector(3, 4) shouldBe 11
        }
      }
    }
    "inverted" should {
      "return the opposite vector" in {
        -Vector(1, 1) shouldBe Vector(-1, -1)
      }
    }
    "multiplied by a scalar" should {
      "return the scaled vector" in {
        Vector(1, 1) * 2 shouldBe Vector(2, 2)
      }
      "be commutative" in {
        2 * Vector(1, 1) shouldBe Vector(1, 1) * 2
      }
    }
    "divided by a scalar" should {
      "return the scaled vector" in {
        Vector(1, 1) / 2 shouldBe Vector(0.5, 0.5)
      }
    }
    "normalized" should {
      "have the norm equal to 1" in {
        Vector(1, 1).normalized.norm shouldBe 1.0 +- 0.001
      }
    }
  }

  "A vector" must {
    "have a squared norm" in {
      Vector(2, 2).squaredNorm shouldBe 8
    }
    "have a norm" in {
      Vector(3, 4).norm shouldBe 5
    }
  }

  "A number" can {
    "be clamped between a minimum and a maximum" in {
      5.clamped(1, 10) shouldBe 5
      10.clamped(2, 5) shouldBe 5
      0.clamped(10, 20) shouldBe 10
    }
  }
}
