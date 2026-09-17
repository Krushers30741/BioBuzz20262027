# BIOBUZZ 2026-2027 — Setup Notes

## One thing you still need to add

The `FtcRobotController` module (the FIRST-provided library — sample OpModes, the Robot Controller
app framework, resources/sounds/icons) isn't in this folder yet. That module is 100% unmodified
stock FIRST SDK code — identical to what's already in `FtcRobotController2025-2026\FtcRobotController`
— so the fastest way to get it here is a plain copy/paste rather than recreating ~65 files one at a time:

1. Open `C:\FTC Code\FtcRobotController2025-2026\FtcRobotController` in File Explorer.
2. Copy that whole `FtcRobotController` folder.
3. Paste it into `C:\FTC Code\BioBuzz2026_2027\` (as a sibling of `TeamCode`).

Once that's in place, `settings.gradle` and `TeamCode/build.gradle` already reference it correctly —
the project should open and sync in Android Studio without further changes.

## What's already here

- **Root gradle/build files** — copied from last year, unchanged.
- **`TeamCode` module scaffolding** — `build.gradle`, `AndroidManifest.xml`, resources, annotation
  processor jar — copied from last year, unchanged.
- **Fully reusable framework** (copied verbatim, no game-specific content):
  - `Clock`, `Step`, `SequentialSteps`, `SleepStep`, `TimeOutAction` — the Action/behavior framework
  - `MecanumDrive` — the RoadRunner drivetrain
  - `auto/roadrunner/` — RoadRunner localizer support files
  - `messages/` — RoadRunner Dashboard logging message classes (`IntakeMessage.java` was left out —
    it was tied to last year's specific intake servo wiring)
- **Adapted for this year** (same shape as last year, but content updated/stubbed):
  - `FTC26502OpMode` — same shared-setup pattern, but the `shooter`/`intake` fields and flags are
    removed for now. Add them back (with new names) once you've picked a scoring mechanism —
    same pattern: one boolean flag, one line in `initOpMode` to build it.
  - `VisionSystem` — same AprilTag-detection scaffolding, but last year's DECODE-specific bits
    (Obelisk pattern helper, hardcoded goal tag IDs) were removed. `checkTag()` is now a placeholder
    (`TAG_ID_PLACEHOLDER = -1`) — update it once you know which tag(s) matter for your strategy.
    Also note: this year's AprilTags are 3.25 in., not last year's 6.5 in.
  - `SensorSystem` — same light/color/distance sensor wrapper, but the color checks were renamed
    from DECODE's purple/green artifacts to `isPollen()` / `isRedNectar()` / `isBlueNectar()`.
    **These thresholds are unverified placeholders** — tune them against real POLLEN/NECTAR and your
    actual color sensor.
- **New placeholder OpModes** so there's something runnable immediately:
  - `TeleopDrive` — drive-only TeleOp, no scoring controls yet.
  - `AutoDrive` — drives forward a configurable distance and stops, to confirm odometry works.
- **`MeepMeepTesting` module** — a separate, plain-desktop-Java module (not part of the robot app)
  that visualizes autonomous paths on your computer without needing the real robot. Open
  `MeepMeepTesting/src/main/java/com/example/meepmeeptesting/MeepMeepTesting.java` in Android
  Studio and click Run next to `main()`. It currently mirrors the path in `AutoDrive.java`. The
  bot size/speed numbers in it were carried over from last year's tuned `MecanumDrive.Params` —
  update them once the BIOBUZZ chassis is built and re-tuned.

## Recent fix: "unable to find hardware device with name 'pinpoint'"

`MecanumDrive`'s localizer was switched from the GoBilda Pinpoint sensor (which this chassis
doesn't have installed) to the built-in `DriveLocalizer` — motor encoders + the Control Hub's own
IMU, no extra sensor required. If a Pinpoint gets added later, see the comment above the
`localizer = new DriveLocalizer(pose);` line in `MecanumDrive.java` for how to switch back.

## What's intentionally not here yet

Anything tied to a specific scoring mechanism (an Intake-equivalent, a Shooter-equivalent, real
Auto/TeleOp scoring routines) — because the team hasn't decided on a robot design yet. Once you
have, the pattern from last year (one class per subsystem, wired into `FTC26502OpMode`) is ready
to extend — see the team's code guide doc for a walkthrough of that pattern.
