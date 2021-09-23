package dev.atedeg.ecscalademo

case class Point(x: Double, y: Double) {
  def +(vector: Vector) = Point(x + vector.x, y + vector.y)
  def -(vector: Vector) = Point(x - vector.x, y - vector.y)
  def -(point: Point) = Vector(x - point.x, y - point.y)
}

given Conversion[(Double, Double), Point] = tuple => Point(tuple._1, tuple._2)

case class Vector(x: Double, y: Double) {
  def +(vector: Vector) = Vector(x + vector.x, y + vector.y)
  def -(vector: Vector) = Vector(x - vector.x, y - vector.y)
  def unary_- = Vector(-x, -y)
  def *(scalar: Double) = Vector(x * scalar, y * scalar)
  def /(scalar: Double) = Vector(x / scalar, y / scalar)
  def dot(vector: Vector) = x * vector.x + y * vector.y
  def squaredNorm = this dot this
  def norm = math.sqrt(squaredNorm)
  def normalized = this / norm
}

given Conversion[(Double, Double), Vector] = tuple => Vector(tuple._1, tuple._2)

extension (scalar: Double) {
  def *(vector: Vector) = vector * scalar
}

extension [T](element: T)(using ord: Ordering[T]) {

  def clamped(lowerBound: T, upperBound: T): T =
    if ord.gt(element, upperBound) then upperBound else if ord.lt(element, lowerBound) then lowerBound else element
  def clamped(bounds: (T, T)): T = clamped(bounds._1, bounds._2)
}
