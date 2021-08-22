# ECScala
General-purpose ECS Scala framework  

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![example workflow](https://github.com/nicolasfara/ecscala/workflows/CI/badge.svg)
[![codecov](https://codecov.io/gh/nicolasfara/ecscala/branch/develop/graph/badge.svg?token=0XZ4XF71AY)](https://codecov.io/gh/nicolasfara/ecscala)
[![javadoc](https://javadoc.io/badge2/dev.atedeg/ecscala/javadoc.svg)](https://javadoc.io/doc/dev.atedeg/ecscala)

## Getting Started

```scala
libraryDependencies += "dev.atedeg" %% "ecscala" % "0.1.0"
```

## Usage

```scala
import dev.atedeg.ecscala.{ World, Entity }

@main def main(): Unit = {
  val world: World = World()
  
  val entity1: Entity = world.createEntity()
  val entity2: Entity = world.createEntity()
  
  // TODO
}
```

## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request


## License

Distributed under the MIT license. See [LICESE](LICENSE) for more information.

