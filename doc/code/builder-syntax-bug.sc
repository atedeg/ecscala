// For the code to compile it is necessary to use redundant type
// annotations in the lambda parameters.
val system2 = BuildSystem
  .withView
  .withDeltaTime{ (view: View, deltaTime: DeltaTime) => ??? }
