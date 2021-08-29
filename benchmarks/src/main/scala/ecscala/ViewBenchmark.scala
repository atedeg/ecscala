package ecscala

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.{ &:, CNil, Component, World }
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
  Warmup,
}

import java.util.concurrent.TimeUnit

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 50, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 50, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(2)
class ViewBenchmark {
  private var world: World = _
  private val nEntities = 10_000

  case class Position(x: Int, y: Int) extends Component
  case class Velocity(x: Int, y: Int) extends Component

  @Setup(Level.Trial)
  def setup: Unit = {
    world = World()
    val entities = (0 until nEntities) map (_ => world.createEntity())
    entities foreach (_.addComponent(Position(1, 2)))
    entities foreach (_.addComponent(Velocity(3, 4)))
  }

  @Benchmark
  def viewIterationBenchmark: Unit = {
    val view = world.getView[Position &: Velocity &: CNil]
    view foreach (_.addComponent(Position(2, 3)))
  }
}
