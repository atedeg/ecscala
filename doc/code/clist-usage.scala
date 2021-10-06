import dev.atedeg.ecscala.{ &:, CNil, Component }

case class Position(x: Int, y: Int) extends Component
case class Velocity(x: Int, y: Int) extends Component

val view = View[Position &: Velocity &: CNil](world)
view foreach (println)
