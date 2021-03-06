package ecscala

import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.{ &:, CNil, IteratingSystem, World }
import ecscala.utils.{ JmhSettings, Position, Velocity }
import org.openjdk.jmh.annotations.{ Benchmark, Setup }

import java.util.concurrent.TimeUnit

class SystemBenchmark extends JmhSettings {

  @Setup
  def init: Unit = {
    world.addSystem(IteratingSystem[Position &: Velocity &: CNil]((_, comps, _) => {
      val Position(x, y) &: Velocity(v1, v2) &: CNil = comps
      Position(x + 1, y) &: Velocity(v1, v2)
    }))
  }

  @Benchmark
  def systemIterationBenchmark: Unit = {
    world.update(10)
  }
}
