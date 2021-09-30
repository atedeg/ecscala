package dev.atedeg.ecscala.fixtures

import dev.atedeg.ecscala.{ &:, CNil, SystemBuilder }
import dev.atedeg.ecscala.util.types.given

//import scala.language.implicitConversions

trait SystemFixture {

  lazy val mySystem1 = SystemBuilder[Position &: CNil].withBefore { (_, _, _) => () }.withAfter { (_, _, _) =>
    ()
  }.withUpdate { (_, c, _) =>
    val Position(x, y) &: CNil = c
    Position(x + 3, y + 3) &: CNil
  }

  lazy val mySystem2 = SystemBuilder[Position &: CNil].withBefore { (_, _, _) => () }.withAfter { (_, _, _) =>
    ()
  }.withUpdate { (_, c, _) =>
    val Position(x, y) &: CNil = c
    Position(x + 1, y + 1) &: CNil
  }
}
