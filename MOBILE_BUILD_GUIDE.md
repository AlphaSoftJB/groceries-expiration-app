# Mobile App Build Guide

## Overview

This guide provides step-by-step instructions for building and deploying the Groceries Expiration Tracking App for iOS and Android platforms.

---

## Prerequisites

### General
- Node.js 18+ installed
- React Native CLI installed
- Git installed
- Firebase project configured (see FIREBASE_SETUP_GUIDE.md)

### For Android
- Android Studio installed
- Android SDK (API 31+)
- JDK 17
- Android device or emulator

### For iOS
- macOS computer
- Xcode 14+ installed
- iOS device or simulator
- Apple Developer account ($99/year)
- CocoaPods installed

---

## Part 1: Project Setup

### Step 1: Install Dependencies

```bash
cd /home/ubuntu/GroceriesExpirationApp/frontend

# Install Node modules
npm install

# Install iOS dependencies (macOS only)
cd ios
pod install
cd ..
```

### Step 2: Configure Environment

**File: `frontend/.env`**
```env
# API Configuration
API_URL=https://your-backend-url.com/graphql
API_WS_URL=wss://your-backend-url.com/graphql

# Firebase Configuration (will be in google-services.json and GoogleService-Info.plist)
# These are just for reference

# App Configuration
APP_NAME=Groceries Tracker
APP_VERSION=1.0.0
APP_BUILD_NUMBER=1

# Feature Flags
ENABLE_ANALYTICS=true
ENABLE_CRASHLYTICS=true
ENABLE_AR_FEATURES=false  # Set to true when AR is fully implemented
```

### Step 3: Update App Configuration

**File: `frontend/app.json`**
```json
{
  "name": "GroceriesApp",
  "displayName": "Groceries Tracker",
  "version": "1.0.0",
  "description": "Never waste food again - Track expiration dates, reduce waste, save money",
  "author": "Your Company Name",
  "license": "MIT",
  "privacy": "public",
  "orientation": "portrait",
  "icon": "./assets/icon.png",
  "splash": {
    "image": "./assets/splash.png",
    "resizeMode": "contain",
    "backgroundColor": "#4CAF50"
  },
  "ios": {
    "bundleIdentifier": "com.groceriesapp",
    "buildNumber": "1",
    "supportsTablet": true,
    "infoPlist": {
      "NSCameraUsageDescription": "We need camera access to scan barcodes and receipts",
      "NSPhotoLibraryUsageDescription": "We need photo library access to save scanned receipts",
      "NSMicrophoneUsageDescription": "We need microphone access for voice commands"
    }
  },
  "android": {
    "package": "com.groceriesapp",
    "versionCode": 1,
    "adaptiveIcon": {
      "foregroundImage": "./assets/adaptive-icon.png",
      "backgroundColor": "#4CAF50"
    },
    "permissions": [
      "CAMERA",
      "READ_EXTERNAL_STORAGE",
      "WRITE_EXTERNAL_STORAGE",
      "RECORD_AUDIO",
      "POST_NOTIFICATIONS"
    ]
  }
}
```

---

## Part 2: Android Build

### Step 1: Configure Android Build

**File: `android/app/build.gradle`**
```gradle
android {
    namespace "com.groceriesapp"
    compileSdkVersion 34
    
    defaultConfig {
        applicationId "com.groceriesapp"
        minSdkVersion 24
        targetSdkVersion 34
        versionCode 1
        versionName "1.0.0"
        
        // Enable multidex
        multiDexEnabled true
    }
    
    signingConfigs {
        debug {
            storeFile file('debug.keystore')
            storePassword 'android'
            keyAlias 'androiddebugkey'
            keyPassword 'android'
        }
        release {
            // Production keystore configuration
            if (project.hasProperty('GROCERIES_UPLOAD_STORE_FILE')) {
                storeFile file(GROCERIES_UPLOAD_STORE_FILE)
                storePassword GROCERIES_UPLOAD_STORE_PASSWORD
                keyAlias GROCERIES_UPLOAD_KEY_ALIAS
                keyPassword GROCERIES_UPLOAD_KEY_PASSWORD
            }
        }
    }
    
    buildTypes {
        debug {
            signingConfig signingConfigs.debug
        }
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
            signingConfig signingConfigs.release
        }
    }
}
```

### Step 2: Generate Release Keystore

```bash
cd android/app

# Generate keystore
keytool -genkeypair -v -storetype PKCS12 \
  -keystore groceries-release.keystore \
  -alias groceries-key \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000

# You will be prompted for:
# - Keystore password (remember this!)
# - Key password (remember this!)
# - Your name, organization, etc.
```

**IMPORTANT:** Save the keystore file and passwords securely! You'll need them for all future updates.

### Step 3: Configure Keystore

**File: `android/gradle.properties`**
```properties
# Keystore configuration (DO NOT commit these values!)
GROCERIES_UPLOAD_STORE_FILE=groceries-release.keystore
GROCERIES_UPLOAD_KEY_ALIAS=groceries-key
GROCERIES_UPLOAD_STORE_PASSWORD=your_keystore_password
GROCERIES_UPLOAD_KEY_PASSWORD=your_key_password

# Android configuration
android.useAndroidX=true
android.enableJetifier=true
org.gradle.jvmargs=-Xmx4096m
```

**Add to `.gitignore`:**
```
# Keystore files
*.keystore
*.jks

# Gradle properties with secrets
gradle.properties
```

### Step 4: Build Release APK

```bash
cd android

# Clean build
./gradlew clean

# Build release APK
./gradlew assembleRelease

# APK will be at:
# android/app/build/outputs/apk/release/app-release.apk
```

### Step 5: Build Release AAB (for Google Play)

```bash
cd android

# Build release AAB (Android App Bundle)
./gradlew bundleRelease

# AAB will be at:
# android/app/build/outputs/bundle/release/app-release.aab
```

### Step 6: Test Release Build

```bash
# Install on connected device
adb install android/app/build/outputs/apk/release/app-release.apk

# Or use React Native CLI
npx react-native run-android --variant=release
```

---

## Part 3: iOS Build

### Step 1: Configure Xcode Project

1. Open Xcode:
   ```bash
   cd ios
   open GroceriesApp.xcworkspace
   ```

2. Select project in navigator
3. Select target → **General** tab
4. Update:
   - Display Name: `Groceries Tracker`
   - Bundle Identifier: `com.groceriesapp`
   - Version: `1.0.0`
   - Build: `1`

### Step 2: Configure Signing

1. Select target → **Signing & Capabilities**
2. Check **Automatically manage signing**
3. Select your Team (Apple Developer account)
4. Xcode will automatically create provisioning profiles

**For Manual Signing:**
1. Uncheck **Automatically manage signing**
2. Select Provisioning Profile for Debug and Release
3. Ensure certificates are installed in Keychain

### Step 3: Configure Capabilities

Add required capabilities:
1. **Push Notifications**
2. **Background Modes**
   - Remote notifications
   - Background fetch
3. **Camera** (already in Info.plist)

### Step 4: Configure Info.plist

**File: `ios/GroceriesApp/Info.plist`**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <!-- App Information -->
    <key>CFBundleDisplayName</key>
    <string>Groceries Tracker</string>
    <key>CFBundleIdentifier</key>
    <string>com.groceriesapp</string>
    <key>CFBundleVersion</key>
    <string>1</string>
    <key>CFBundleShortVersionString</key>
    <string>1.0.0</string>
    
    <!-- Privacy Permissions -->
    <key>NSCameraUsageDescription</key>
    <string>We need camera access to scan barcodes and receipts for tracking expiration dates</string>
    <key>NSPhotoLibraryUsageDescription</key>
    <string>We need photo library access to save scanned receipts</string>
    <key>NSMicrophoneUsageDescription</key>
    <string>We need microphone access for voice commands and accessibility features</string>
    <key>NSSpeechRecognitionUsageDescription</key>
    <string>We use speech recognition for voice commands to help you manage your groceries hands-free</string>
    
    <!-- App Transport Security -->
    <key>NSAppTransportSecurity</key>
    <dict>
        <key>NSAllowsArbitraryLoads</key>
        <false/>
        <key>NSExceptionDomains</key>
        <dict>
            <key>your-backend-domain.com</key>
            <dict>
                <key>NSExceptionAllowsInsecureHTTPLoads</key>
                <false/>
                <key>NSIncludesSubdomains</key>
                <true/>
            </dict>
        </dict>
    </dict>
    
    <!-- Supported Orientations -->
    <key>UISupportedInterfaceOrientations</key>
    <array>
        <string>UIInterfaceOrientationPortrait</string>
    </array>
</dict>
</plist>
```

### Step 5: Build for Testing

```bash
# Build for simulator
npx react-native run-ios

# Build for specific device
npx react-native run-ios --device "Your iPhone Name"

# Build release version
npx react-native run-ios --configuration Release
```

### Step 6: Archive for App Store

1. In Xcode, select **Product** → **Archive**
2. Wait for archive to complete
3. Organizer window will open
4. Select your archive
5. Click **Distribute App**
6. Select distribution method:
   - **App Store Connect** (for submission)
   - **Ad Hoc** (for testing)
   - **Enterprise** (for internal distribution)
   - **Development** (for development testing)

### Step 7: Upload to App Store Connect

1. Choose **App Store Connect**
2. Click **Next**
3. Select **Upload** or **Export**
4. Review app information
5. Click **Upload**
6. Wait for processing (can take 30-60 minutes)

---

## Part 4: App Icons and Assets

### Required Assets

#### Android
- **App Icon:** 512x512 px (PNG)
- **Adaptive Icon:** 
  - Foreground: 512x512 px (PNG with transparency)
  - Background: 512x512 px (PNG)
- **Notification Icon:** 24x24 dp (white on transparent)
- **Splash Screen:** 1242x2688 px (PNG)

#### iOS
- **App Icon:** Multiple sizes required
  - 1024x1024 px (App Store)
  - 180x180 px (iPhone)
  - 167x167 px (iPad Pro)
  - 152x152 px (iPad)
  - 120x120 px (iPhone)
  - 87x87 px (iPhone)
  - 80x80 px (iPad)
  - 76x76 px (iPad)
  - 60x60 px (iPhone)
  - 58x58 px (iPhone)
  - 40x40 px (iPhone/iPad)
  - 29x29 px (iPhone/iPad)
  - 20x20 px (iPhone/iPad)
- **Launch Screen:** 1242x2688 px (PNG)

### Generate Icons

Use a tool like [App Icon Generator](https://appicon.co/) or create manually:

```bash
# Install icon generator
npm install -g app-icon

# Generate all required sizes
app-icon generate -i icon.png -o ./assets/icons
```

### Place Assets

**Android:**
```
android/app/src/main/res/
├── mipmap-hdpi/
│   └── ic_launcher.png (72x72)
├── mipmap-mdpi/
│   └── ic_launcher.png (48x48)
├── mipmap-xhdpi/
│   └── ic_launcher.png (96x96)
├── mipmap-xxhdpi/
│   └── ic_launcher.png (144x144)
├── mipmap-xxxhdpi/
│   └── ic_launcher.png (192x192)
└── drawable/
    └── ic_notification.png (24x24)
```

**iOS:**
```
ios/GroceriesApp/Images.xcassets/
├── AppIcon.appiconset/
│   ├── Contents.json
│   └── [all icon sizes]
└── LaunchImage.launchimage/
    ├── Contents.json
    └── [launch images]
```

---

## Part 5: Testing

### Android Testing

**Debug Build:**
```bash
npx react-native run-android
```

**Release Build:**
```bash
npx react-native run-android --variant=release
```

**Test on Multiple Devices:**
- Physical devices (various Android versions)
- Emulators (different screen sizes)
- Test on Android 8.0+ minimum

**Test Checklist:**
- [ ] App launches successfully
- [ ] All screens navigate correctly
- [ ] Barcode scanning works
- [ ] Camera OCR works
- [ ] Push notifications received
- [ ] GraphQL API calls work
- [ ] Offline functionality
- [ ] Performance is smooth
- [ ] No crashes or errors

### iOS Testing

**Debug Build:**
```bash
npx react-native run-ios
```

**Release Build:**
```bash
npx react-native run-ios --configuration Release
```

**Test on Multiple Devices:**
- Physical devices (various iOS versions)
- Simulators (different screen sizes)
- Test on iOS 13.0+ minimum

**Test Checklist:**
- [ ] App launches successfully
- [ ] All screens navigate correctly
- [ ] Barcode scanning works
- [ ] Camera OCR works
- [ ] Push notifications received
- [ ] GraphQL API calls work
- [ ] Offline functionality
- [ ] Performance is smooth (60 FPS)
- [ ] No crashes or errors
- [ ] VoiceOver accessibility works

### TestFlight (iOS)

1. Upload build to App Store Connect
2. Go to TestFlight tab
3. Add internal testers
4. Add external testers (optional)
5. Distribute build
6. Testers receive invitation
7. Collect feedback

### Google Play Internal Testing (Android)

1. Upload AAB to Google Play Console
2. Create internal testing track
3. Add testers (email addresses)
4. Publish to internal testing
5. Testers receive link
6. Collect feedback

---

## Part 6: Performance Optimization

### Bundle Size Optimization

**Android:**
```gradle
// In android/app/build.gradle
android {
    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    
    // Enable app bundle splits
    bundle {
        language {
            enableSplit = true
        }
        density {
            enableSplit = true
        }
        abi {
            enableSplit = true
        }
    }
}
```

**iOS:**
- Enable bitcode (if supported by all dependencies)
- Use app thinning (automatic in App Store)

### Performance Monitoring

**Add Firebase Performance Monitoring:**

```bash
npm install @react-native-firebase/perf
```

**Track custom traces:**
```typescript
import perf from '@react-native-firebase/perf';

const trace = await perf().startTrace('custom_trace');
// ... your code
await trace.stop();
```

---

## Part 7: Troubleshooting

### Common Android Issues

**Issue: Build fails with "Duplicate class" error**
```gradle
// Add to android/app/build.gradle
android {
    packagingOptions {
        pickFirst 'lib/x86/libc++_shared.so'
        pickFirst 'lib/x86_64/libc++_shared.so'
        pickFirst 'lib/armeabi-v7a/libc++_shared.so'
        pickFirst 'lib/arm64-v8a/libc++_shared.so'
    }
}
```

**Issue: App crashes on startup**
- Check logcat: `adb logcat`
- Verify all native dependencies are linked
- Check ProGuard rules

**Issue: Camera not working**
- Check permissions in AndroidManifest.xml
- Request runtime permissions
- Test on physical device (not emulator)

### Common iOS Issues

**Issue: Build fails with "No such module"**
```bash
cd ios
pod deintegrate
pod install
```

**Issue: App crashes on launch**
- Check Xcode console for errors
- Verify all dependencies are installed
- Check Info.plist configuration

**Issue: Push notifications not working**
- Test on physical device (not simulator)
- Verify APNs certificate is uploaded
- Check notification permissions

---

## Part 8: Deployment Checklist

### Pre-Deployment

- [ ] All features tested and working
- [ ] No console errors or warnings
- [ ] Performance optimized
- [ ] Bundle size optimized
- [ ] Icons and assets finalized
- [ ] Privacy policy and terms of service ready
- [ ] App store screenshots prepared
- [ ] App store description written
- [ ] Keywords researched (for ASO)

### Android Deployment

- [ ] Release keystore generated and secured
- [ ] Release AAB built and tested
- [ ] Google Play Console account created
- [ ] App listing created
- [ ] Screenshots uploaded (phone and tablet)
- [ ] Feature graphic created (1024x500)
- [ ] Privacy policy URL added
- [ ] Content rating completed
- [ ] Pricing and distribution set
- [ ] Release notes written
- [ ] AAB uploaded to production track
- [ ] Release reviewed and published

### iOS Deployment

- [ ] Apple Developer account active
- [ ] App Store Connect app created
- [ ] App archive created and validated
- [ ] Build uploaded to App Store Connect
- [ ] TestFlight testing completed
- [ ] App Store listing completed
- [ ] Screenshots uploaded (all required sizes)
- [ ] App preview video created (optional)
- [ ] Privacy policy URL added
- [ ] App Store review information provided
- [ ] Release notes written
- [ ] Submitted for review
- [ ] Approved and released

---

## Part 9: Continuous Integration/Deployment

### GitHub Actions (Example)

**File: `.github/workflows/android-build.yml`**
```yaml
name: Android Build

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Setup Node.js
      uses: actions/setup-node@v3
      with:
        node-version: '18'
    
    - name: Install dependencies
      run: |
        cd frontend
        npm install
    
    - name: Build Android Release
      run: |
        cd frontend/android
        ./gradlew assembleRelease
    
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-release
        path: frontend/android/app/build/outputs/apk/release/app-release.apk
```

### Fastlane (Advanced)

Install Fastlane:
```bash
gem install fastlane
```

Initialize:
```bash
cd android
fastlane init

cd ../ios
fastlane init
```

---

## Resources

- [React Native Documentation](https://reactnative.dev/)
- [Android Developer Guide](https://developer.android.com/)
- [iOS Developer Guide](https://developer.apple.com/)
- [Google Play Console Help](https://support.google.com/googleplay/android-developer/)
- [App Store Connect Help](https://developer.apple.com/app-store-connect/)

---

**Last Updated:** November 6, 2025  
**Version:** 1.0.0
