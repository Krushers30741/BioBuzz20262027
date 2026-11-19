# Shooter speed from distance

The robot asks the camera how far away our alliance's tags are, then looks up the wheel speed in a
small table we fill in by test-shooting. Between two rows it draws a straight line (linear
interpolation). Closer than the first row or farther than the last, it uses the end row's speed
(clamping), so it never guesses past what we tested.

## What's where

- `ShooterSpeedTable.java`: the table and the math. No hardware in it.
- `ShotController.java`: picks the speed when a volley starts.
  - `USE_DISTANCE_SPEED`: off until the table is tuned. Off = shoots at `SHOOTER_SPEED` like before.
  - `ALLIANCE`: `"blue"` or `"red"`, which tags to measure to.
  - `SHOOTER_TABLE`: the rows. **The numbers in it now are placeholders.**
  - If the camera sees no tag, it falls back to `SHOOTER_SPEED`.
- `UtilityShooterDistanceTuner.java`: the OpMode for filling in the table.

Distance comes from `VisionSystem.getCurrentDistance()`, so the OpMode has to call
`initVision()`. Spot1 and Spot2 already do. `TeleopDrive` does not yet, so add `initVision();`
after `initOpMode(...)` there before turning distance mode on.

## Filling in the table

1. Charge the battery. A tired battery shoots short.
2. Run **Utility - Shooter Distance Tuner**. Press X for blue tags or B for red.
3. Park close to the Hive, facing it, so **DISTANCE** shows a number.
   Use that number, not a tape measure. It's what the robot will use in a match.
4. Hold the right bumper to load balls. Press the left bumper to shoot.
5. Short? D-pad up (+25). Long? D-pad down (-25). Left/right fine-tune by 5.
   The new speed is used the next time you press the left bumper.
6. When most shots score, write down `{DISTANCE, SPEED}`.
7. Back up about a foot and repeat, out to your longest shot.
8. Put your rows in `SHOOTER_TABLE` in `ShotController.java`, closest first, and set
   `USE_DISTANCE_SPEED = true`.

5 to 7 rows is plenty. If shots miss between two rows, add a row in the middle.
Re-tune after changing the shooter wheels, compression or the motors' PIDF values.

## Data sheet

| Distance (in) | Speed (ticks/sec) | Made / tried | Notes |
|---|---|---|---|
| | | | |
| | | | |
| | | | |
| | | | |
| | | | |
| | | | |
