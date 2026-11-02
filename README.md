# iSwipeMacro - Android Gaming Gesture Studio

A standalone, non-root Android application for recording, visualizing, analyzing, and replaying swipe and touch gestures inside an interactive high-performance practice arena.

Designed with a modern gaming HUD aesthetic in dark purple and electric neon cyan, optimized for modern Android smartphones including the **Redmi Note 11T 5G (FHD+ 1080x2400 @ 90Hz)**.

---

## 🚀 How to Build & Run in Android Studio

### Prerequisites
1. **Android Studio**: Android Studio Koala, Ladybug, or Iguana (2024.1+ recommended).
2. **JDK**: OpenJDK 17 or JDK 21 (bundled with modern Android Studio).
3. **Android Device or Emulator**: Android 7.0 (API 24) to Android 15 (API 35). Tested on Redmi Note 11T 5G.

### Step 1: Open the Project
1. Open Android Studio.
2. Click **Open** (or **File > Open**) and select this `android` folder.
3. Allow Gradle Sync to finish downloading required dependencies.

### Step 2: Connect Your Redmi Note 11T 5G
1. On your phone, go to **Settings > About Phone**.
2. Tap **MIUI Version / OS Version** 7 times until you see *"You are now a developer!"*.
3. Go back to **Settings > Additional Settings > Developer Options**.
4. Enable **USB Debugging** and **Install via USB**.
5. Connect your phone via USB cable and tap **Allow USB Debugging** on the phone screen.

### Step 3: Build & Install Debug APK
- **From Android Studio UI**:
  Select your connected device from the device dropdown at the top toolbar, then click the green **Run (▶)** button or press `Shift + F10`.
- **From Terminal / Command Line**:
  ```bash
  # Linux / macOS
  ./gradlew assembleDebug

  # Windows PowerShell / CMD
  .\gradlew.bat assembleDebug
  ```

The compiled debug APK will be created at:
`app/build/outputs/apk/debug/app-debug.apk`

To install directly to your connected phone via adb:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## ⚡ Main App Features

1. **Practice Arena Gesture Recording**:
   - High-rate touch sampling (up to 90Hz/120Hz matching Redmi Note 11T 5G touch sampling).
   - Records normalized coordinates (`nx`, `ny`) so gestures scale across any screen aspect ratio.
   - Preserves exact relative delta timing and pressure.
2. **Visual Replay Engine**:
   - Replays gestures smoothly inside the practice arena with original order and timing.
   - Dynamic ghost touch pointer with glowing neon trail and ripple effects.
   - Variable playback speeds: 0.25x, 0.5x, 1.0x, 1.5x, 2.0x, 3.0x, 5.0x.
   - Configurable repeat loops and interval delays.
3. **Subtle Tactile Haptic Feedback**:
   - Subtle double pulse on gesture record start.
   - Single crisp tick on record stop.
   - Rising pulse on replay start.
   - Double tick on replay stop.
   - Enhances tactile feedback without overwhelming the user.
4. **Local Profile Management**:
   - Saves gestures locally with full telemetry (total travel distance, peak speed, point count).
   - Inline rename, profile deletion, profile duplication.
   - Preloaded gaming macro presets (FPS Snap, MOBA Skillshot, Zig-Zag Evasion).
5. **Security & Privacy Policy**:
   - Non-root compliant: operates strictly within the application's practice surface.
   - Does NOT inject touches into foreign applications or bypass Android security sandbox.
