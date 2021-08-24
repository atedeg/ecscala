package ecscala

import dev.atedeg.ecscala.util.IterableMap
import dev.atedeg.ecscala.util.mutable.LongIterableMap
import org.openjdk.jmh.annotations.*

import java.util.concurrent.TimeUnit

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 15, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(2)
class MapBenchmark {
  case class Key(val key: Int)
  case class Value(val value: Int)

  given Conversion[Key, Long] = _.key.toLong

  private val keys = 0 until 10000000 map { Key(_) }
  private val values = 0 until 10000000 map { Value(_) }

  private var baseLineMap = Map.from(keys zip values)
  private var iterableMap = IterableMap.from(keys zip values)

  private var baseMutableMap = scala.collection.mutable.Map.from(keys zip values)
  private var mutableIterableMap = LongIterableMap(12L -> Value(3), 15L -> Value(4))

  //@Benchmark
  def immutableBaseline: Unit = {
    for (key <- baseLineMap.keys) baseLineMap += (key -> Value(baseLineMap(key).value + 1))
  }

  //@Benchmark
  def immutableIterable: Unit = {
    for (key <- iterableMap.keys) iterableMap += (key -> Value(iterableMap(key).value + 1))
  }

  @Benchmark
  def mutableBaseline: Unit = {
    for (key <- baseMutableMap.keys) baseMutableMap += (key -> Value(baseMutableMap(key).value + 1))
  }

  @Benchmark
  def mutableIterable: Unit = {
    for (key <- mutableIterableMap.keys) mutableIterableMap += (key -> Value(mutableIterableMap(key).value + 1))
  }
}
