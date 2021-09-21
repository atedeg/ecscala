package dev.atedeg.ecscalademo

case class Point(x: Double, y: Double) {
  def +(vector: Vector) = Point(x + vector.x, y + vector.y)
  def -(vector: Vector) = Point(x - vector.x, y - vector.y)
  def -(point: Point) = Vector(x - point.x, y - point.y)
}

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

extension (scalar: Double) {
  def *(vector: Vector) = vector * scalar
}

extension [T](number: T)(using ord: Ordering[T]) {
  def clamped(min: T, max: T) = if ord.gt(number, max) then max else if ord.lt(number, min) then min else number
}
