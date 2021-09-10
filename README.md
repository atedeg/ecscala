# ECScala
General-purpose ECS Scala framework  

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![GitHub release](https://img.shields.io/github/release/nicolasfara/ecscala.svg)](https://gitHub.com/nicolasfara/ecscala/releases/)
![example workflow](https://github.com/nicolasfara/ecscala/workflows/CI/badge.svg)
[![codecov](https://codecov.io/gh/nicolasfara/ecscala/branch/develop/graph/badge.svg?token=0XZ4XF71AY)](https://codecov.io/gh/nicolasfara/ecscala)
[![javadoc](https://javadoc.io/badge2/dev.atedeg/ecscala/javadoc.svg)](https://javadoc.io/doc/dev.atedeg/ecscala)

## Getting Started

```scala
libraryDependencies += "dev.atedeg" %% "ecscala" % "0.1.0"
```

## Usage

```scala
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscala.util.types.given

case class Position(x: Double, y: Double) extends Component
case class Velocity(vx: Double, vy: Double) extends Component
case class Gravity(g: Double) extends Component

object AnObject extends ECScalaDSL {
  val world = World()
  val entity1 = world hasAn entity withComponents {
    Position(1, 2) and Velocity(3, 4) and Gravity(9.8)
  }
  
  entity1 + Position(3, 6)
}
```

## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Authors

- **Giacomo Cavalieri** - [giacomocavalieri](https://github.com/giacomocavalieri)
- **Nicolò Di Domenico** - [ndido98](https://github.com/ndido98)
- **Nicolas Farabegoli** - [nicolasfara](https://github.com/nicolasfara)
- **Linda Vitali** - [vitlinda](https://github.com/vitlinda)

## License

Distributed under the MIT license. See [LICESE](LICENSE) for more information.

