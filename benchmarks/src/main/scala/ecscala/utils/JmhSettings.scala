package ecscala.utils

import dev.atedeg.ecscala.given
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
@BenchmarkMode(Array(Mode.SampleTime))
@Threads(1)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 50, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 100, time = 100, timeUnit = TimeUnit.MILLISECONDS)
class JmhSettings {

  @Param(Array("1024", "2048", "4096", "10000"))
  var nEntities: Int = _
  val world: World = World()

  @Setup
  def setup: Unit = {
    val entities = (0 until nEntities) map (_ => world.createEntity())
    entities foreach (_ setComponent Position(1, 2))
    entities foreach (_ setComponent Velocity(3, 4))
  }
}
