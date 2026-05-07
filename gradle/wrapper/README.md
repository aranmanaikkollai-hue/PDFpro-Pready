# Gradle Wrapper Setup

The `gradle-wrapper.jar` file is required to build this project but is NOT included in this repository 
due to binary file size constraints.

## How to get gradle-wrapper.jar

### Option 1: Copy from existing Android Studio project
```bash
cp /path/to/any/android/project/gradle/wrapper/gradle-wrapper.jar gradle/wrapper/
```

### Option 2: Download from Gradle official
```bash
curl -L -o gradle/wrapper/gradle-wrapper.jar   https://services.gradle.org/distributions/gradle-8.4-bin.zip
```

### Option 3: Use system Gradle (if installed)
```bash
gradle wrapper --gradle-version 8.4
```

### Option 4: Android Studio will auto-generate
Open the project in Android Studio → File → Sync Project with Gradle Files
Android Studio will automatically download the correct wrapper.

## Verify
After adding the file:
```bash
ls -la gradle/wrapper/gradle-wrapper.jar
# Should show ~60KB+ file size
```
