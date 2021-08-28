package ecscala

import dev.atedeg.ecscala.util.mutable.IterableMap
import org.openjdk.jmh.annotations.*

import java.util.concurrent.TimeUnit
import scala.collection.mutable

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

  private val baseMutableMap = scala.collection.mutable.HashMap.from(keys zip values)
  private val mutableIterableMap = IterableMap.from(keys zip values)

  @Benchmark
  def mutableBaseline(): Unit = {
    testMap(baseMutableMap)
  }

  @Benchmark
  def mutableIterable(): Unit = {
    testMap(mutableIterableMap)
  }

  private def testMap(map: mutable.Map[Key, Value]): Unit = {
    for (key <- map.keys) map += (key -> Value(map(key).value + 1))
  }
}
