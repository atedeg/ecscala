@State(Scope.Thread)
@BenchmarkMode(Array(Mode.All))
@Threads(1)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 50)
@Measurement(iterations = 100)
class JmhSettings {

  @Param(Array("1024", "2048", "4096", "10000"))
  var nEntities: Int = _
  val world: World = World()

  @Setup
  def setup: Unit = {
    val entities =
      (0 until nEntities) map (_ => world.createEntity())
    entities foreach (_ setComponent Position(1, 2))
    entities foreach (_ setComponent Velocity(3, 4))
  }
}