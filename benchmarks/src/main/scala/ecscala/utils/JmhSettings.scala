package ecscala.utils

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.{ &:, CNil, System, World }
import org.openjdk.jmh.annotations.{
  Benchmark,
  BenchmarkMode,
  Fork,
  Level,
  Measurement,
  Mode,
  OutputTimeUnit,
  Param,
  Scope,
  Setup,
  State,
  Threads,
  Timeout,
  Warmup,
}

import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@Threads(1)
@Fork(1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
class JmhSettings {

  @Param(Array("1024", "2048", "4096"))
  var nEntities: Int = _
  val world: World = World()

  @Setup
  def setup: Unit = {
    val entities = (0 until nEntities) map (_ => world.createEntity())
    entities foreach (_ setComponent Position(1, 2))
    entities foreach (_ setComponent Velocity(3, 4))
  }
}
