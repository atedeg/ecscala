package ecscala

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.{ &:, CNil, System, World }
import ecscala.utils.{ JmhSettings, Position, Velocity }
import org.openjdk.jmh.annotations.{
  Benchmark,
  BenchmarkMode,
  Fork,
  Level,
  Measurement,
  Mode,
  OutputTimeUnit,
  Scope,
  Setup,
  State,
  Threads,
  Timeout,
  Warmup,
}

import java.util.concurrent.TimeUnit

class SystemBenchmark extends JmhSettings {

  @Setup
  def init: Unit = {
    val system: System[Position &: Velocity &: CNil] = (_, comps, _, _, _) => {
      val Position(x, y) &: Velocity(v1, v2) &: CNil = comps
      Position(x + 1, y) &: Velocity(v1, v2)
    }

    world.addSystem(system)
  }

  @Benchmark
  def systemIterationBenchmark: Unit = {
    world.update(10)
  }
}
