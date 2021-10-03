package ecscala

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.{ &:, CNil, Component, World }
import ecscala.utils.{ JmhSettings, Position, Velocity }
import org.openjdk.jmh.annotations.Benchmark

import java.util.concurrent.TimeUnit

class ViewBenchmark extends JmhSettings {

  @Benchmark
  def viewIterationBenchmark: Unit = {
    val view = world.getView[Position &: Velocity &: CNil]
    view foreach (_.head setComponent Position(2, 3))
  }
}
