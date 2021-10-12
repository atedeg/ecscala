extension (world: World) {
  def hasA(init: World ?=> Unit): Unit = {
    given w: World = world
    init
  }
}
