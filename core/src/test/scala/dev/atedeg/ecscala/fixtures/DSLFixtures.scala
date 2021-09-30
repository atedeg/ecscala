package dev.atedeg.ecscala.fixtures

import dev.atedeg.ecscala.{&:, CNil, System, World}
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscala.util.types.given

trait SystemFixture extends ECScalaDSL {
  lazy val world = World()
  val entity1 = world hasAn entity withComponent Position(1, 1)

  lazy val mySystem1 = System[Position &: CNil]((_, comps, _) => {
    val Position(x, y) &: CNil = comps
    Position(x + 3, y + 3) &: CNil
  })

  lazy val mySystem2 = System[Position &: CNil]((_, comps, _) => {
    val Position(x, y) &: CNil = comps
    Position(x + 1, y + 1) &: CNil
  })
}