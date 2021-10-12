"A BallSelectionSystem" should {
  "run" when {
    "in an enabled state" in
      checkAllStates(BallSelectionSystem(_, _))(
        (State.Pause, AnyValue, true, AnyValue),
        (State.SelectBall, AnyValue, true, AnyValue),
      )
  }
}
