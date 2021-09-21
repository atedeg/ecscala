package dev.atedeg.ecscalademo

case class Color(r: Int, g: Int, b: Int) {
  require(r >= 0 && r <= 255)
  require(g >= 0 && g <= 255)
  require(b >= 0 && b <= 255)
}
