package ecscala

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.{ &:, CNil, System, World }
import ecscala.utils.{ Position, Velocity }
import org.graalvm.compiler.word.Word
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
class SystemBenchmark {
  private var world: World = _
  private val nEntities = 10_000

  @Setup(Level.Trial)
  def setup: Unit = {
    world = World()
    val entities = (0 until nEntities) map (_ => world.createEntity())
    entities foreach (_.addComponent(Position(1, 2)))
    entities foreach (_.addComponent(Velocity(3, 4)))

    val system: System[Position &: Velocity &: CNil] = (e, cl, v) => {
      val Position(x, y) &: Velocity(v1, v2) &: CNil = cl
      Position(x + 1, y) &: Velocity(v1, v2)
    }

    world.addSystem(system)
  }

  @Benchmark
  def systemIterationBenchmark: Unit = {
    world.update()
  }
}
