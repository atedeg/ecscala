// The type of the lambda required to build a System changes
// depending on the specified elements.
val system1 = BuildSystem
  .withWorld
  .withDeltaTime
  .withComponents{ (world, deltaTime, comps) => ??? }
val system2 = BuildSystem
  .withView
  .withComponents{ (view, comps) => ??? }
