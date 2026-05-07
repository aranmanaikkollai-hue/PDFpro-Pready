# ProPDF Editor - Build Setup Guide

## Quick Start (3 Steps)

### Step 1: Add gradle-wrapper.jar

Since the Gradle wrapper JAR is a binary file, you need to add it manually:

```bash
# From any existing Android project on your machine:
cp /path/to/existing/android/project/gradle/wrapper/gradle-wrapper.jar gradle/wrapper/

# OR download from Gradle official:
curl -L -o gradle/wrapper/gradle-wrapper.jar   https://raw.githubusercontent.com/gradle/gradle/v8.4.0/gradle/wrapper/gradle-wrapper.jar

# OR let Android Studio handle it (recommended):
# Just open the project in Android Studio and click "Sync Now"
```

### Step 2: Verify File Structure

Your project should look like this:

```
PDFpro/
├── gradlew                          ✅ Must exist, executable
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar       ✅ YOU MUST ADD THIS
│       └── gradle-wrapper.properties ✅ Already included
├── build.gradle                     ✅ Already included
├── settings.gradle                  ✅ Already included
├── app/
│   ├── build.gradle                 ✅ Already included
│   ├── src/main/AndroidManifest.xml ✅ Already included
│   └── ... (all source files)       ✅ Already included
```

### Step 3: Build

```bash
# Make gradlew executable (Linux/Mac)
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# Or open in Android Studio and click "Run"
```

---

## Codemagic CI/CD Setup

For automated builds on Codemagic, add this pre-build script:

```yaml
scripts:
  - name: Setup Gradle Wrapper
    script: |
      # Download gradle-wrapper.jar
      curl -L -o gradle/wrapper/gradle-wrapper.jar         https://raw.githubusercontent.com/gradle/gradle/v8.4.0/gradle/wrapper/gradle-wrapper.jar

      # Verify
      ls -la gradle/wrapper/gradle-wrapper.jar

      # Make executable
      chmod +x gradlew
```

---

## Common Build Errors & Fixes

### Error: `gradlew: No such file or directory`
**Fix**: The `gradlew` script is included. Make it executable:
```bash
chmod +x gradlew
```

### Error: `gradle-wrapper.jar: No such file or directory`
**Fix**: Add the JAR file as described in Step 1 above.

### Error: `Could not find com.github.barteksc:pdfium-android`
**Fix**: Ensure `jitpack.io` is in repositories. Already configured in `settings.gradle`.

### Error: `Cannot resolve symbol 'dagger'`
**Fix**: Enable annotation processing in Android Studio:
```
File → Settings → Build → Annotation Processors → Enable
```
Then: `Build` → `Rebuild Project`

### Error: `Manifest merger failed`
**Fix**: Clean and rebuild:
```bash
./gradlew clean
./gradlew assembleDebug
```

---

## What's Already Configured

| Feature | Status |
|---------|--------|
| MIT License | ✅ Included |
| Clean Architecture (MVVM) | ✅ Implemented |
| Hilt DI | ✅ Configured |
| Room Database | ✅ Configured |
| PdfiumAndroid | ✅ Dependency added |
| PDFBox-Android | ✅ Dependency added |
| Tesseract OCR | ✅ Dependency added |
| CameraX | ✅ Dependency added |
| Jetpack Compose | ✅ Prepared (API 24+) |
| Material 3 | ✅ Configured |
| AdMob/Firebase | ❌ Removed (ad-free build) |

---

## Next Steps After Build

1. **Test on device**: Install the debug APK on an Android device
2. **Add app icon**: Replace placeholder icons in `res/mipmap-*`
3. **Configure signing**: Add release keystore for Play Store
4. **Add privacy policy**: Required for Play Store submission
5. **Take screenshots**: For Play Store listing

---

## Need Help?

- Check `PROJECT_SUMMARY.md` for architecture details
- Check `open_source_licenses.md` for dependency info
- All source code is in `app/src/main/java/com/propdf/editor/`
