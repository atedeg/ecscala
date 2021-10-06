package dev.atedeg.ecscala.fixtures

import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.{ &:, CNil, IteratingSystem, World }
import dev.atedeg.ecscala.dsl.ECScalaDSL

trait SystemFixture extends ECScalaDSL {

  val mySystem1 = IteratingSystem[Position &: CNil] { (_, comps, _) =>
    val Position(x, y) &: CNil = comps
    Position(x + 3, y + 3) &: CNil
  }

  val mySystem2 = IteratingSystem[Position &: CNil] { (_, comps, _) =>
    val Position(x, y) &: CNil = comps
    Position(x + 1, y + 1) &: CNil
  }
}
