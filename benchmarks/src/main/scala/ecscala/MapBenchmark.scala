package ecscala

import dev.atedeg.ecscala.util.mutable.IterableMap
import org.openjdk.jmh.annotations.*

import java.util.concurrent.TimeUnit

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 50, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 50, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(2)
class MapBenchmark {
  case class Key(key: Int)
  case class Value(value: Int)

  private val keys = 0 until 1_000_000 map { Key.apply }
  private val values = 0 until 1_000_000 map { Value.apply }

  private var baseMutableMap = scala.collection.mutable.HashMap.from(keys zip values)
  private var mutableIterableMap = IterableMap.from((keys map { _.key }) zip values)

  @Benchmark
  def mutableBaseline(): Unit = {
    for (key <- baseMutableMap.keys) baseMutableMap += (key -> Value(baseMutableMap(key).value + 1))
  }

  @Benchmark
  def mutableIterable(): Unit = {
    for (key <- mutableIterableMap.keys) mutableIterableMap += (key -> Value(mutableIterableMap(key).value + 1))
  }
}
