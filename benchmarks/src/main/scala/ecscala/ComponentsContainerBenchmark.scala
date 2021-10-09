package ecscala

import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.{ &:, CNil }
import ecscala.utils.{ JmhSettings, Position, Velocity }
import org.openjdk.jmh.annotations.Benchmark

class ComponentsContainerBenchmark extends JmhSettings {
  @Benchmark
  def componentsContainerBenchmark: Unit = {
    val view = world.getView[Position &: Velocity &: CNil]
    view foreach { (entity, comps) =>
      val pos = entity.getComponent[Position].get
      val vel = entity.getComponent[Velocity].get
      entity.setComponent(Position(pos.x + 1, pos.y + 1))
      entity.setComponent(Velocity(vel.x + 1, vel.y + 1))
    }
  }
}
