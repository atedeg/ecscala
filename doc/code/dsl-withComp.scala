val world = World()
val entity1 = world hasAn entity withComponents {
  Position(1, 2) &: Velocity(3, 4) &: Gravity(9.8)
}