"A CList" must {
  "fail to compile when trying to do an invalid unpacking" in new ComponentsFixture with {
    "val a &: CNil = Position(1, 1) &: Velocity(1, 1) &: CNil"
      shouldNot typeCheck
    "val a &: b &: CNil = Position(1, 1) &: CNil"
      shouldNot typeCheck
  }
}