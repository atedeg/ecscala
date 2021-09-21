package dev.atedeg.ecscalademo

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ColorTest extends AnyWordSpec with Matchers {

  "A Color" should {
    "have red capped between 0 and 255" in {
      noException should be thrownBy Color(0, 0, 0)
      noException should be thrownBy Color(128, 0, 0)
      noException should be thrownBy Color(255, 0, 0)
      an[IllegalArgumentException] should be thrownBy Color(-1, 0, 0)
      an[IllegalArgumentException] should be thrownBy Color(256, 0, 0)
    }
    "have green capped between 0 and 255" in {
      noException should be thrownBy Color(0, 0, 0)
      noException should be thrownBy Color(0, 128, 0)
      noException should be thrownBy Color(0, 255, 0)
      an[IllegalArgumentException] should be thrownBy Color(0, -1, 0)
      an[IllegalArgumentException] should be thrownBy Color(0, 256, 0)
    }
    "have blue capped between 0 and 255" in {
      noException should be thrownBy Color(0, 0, 0)
      noException should be thrownBy Color(0, 0, 128)
      noException should be thrownBy Color(0, 0, 255)
      an[IllegalArgumentException] should be thrownBy Color(0, 0, -1)
      an[IllegalArgumentException] should be thrownBy Color(0, 0, 256)
    }
  }
}
