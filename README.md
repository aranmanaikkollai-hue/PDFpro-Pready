# ProPDF Editor

[![MIT License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Android API](https://img.shields.io/badge/API-21%2B-brightgreen.svg)](https://android-arsenal.com/api?level=21)

A free, open-source PDF editor for Android. All features are completely free — monetized only through non-intrusive ads.

## Features

### PDF Viewer
- Fast rendering with PdfiumAndroid
- Zoom, scroll, and page navigation
- Full-text search within documents
- Bookmark pages
- Night mode support

### Annotations
- Highlight text
- Freehand drawing
- Text notes
- Eraser tool
- Undo/redo support

### PDF Tools
- Merge multiple PDFs
- Split PDF by page ranges
- Compress PDF files
- Rotate pages
- Delete specific pages
- Add watermarks
- Encrypt with password
- Convert images to PDF
- Extract PDF pages as images
- Extract text from PDF

### Document Scanner
- Camera-based document scanning
- Auto-enhancement
- Color/Grayscale/Black & White modes
- Multi-page scanning
- Save as PDF

### OCR (Optical Character Recognition)
- Extract text from images
- Powered by Tesseract OCR
- On-device processing (no internet required)

## Architecture

```
app/
├── data/           # Data layer (Room, repositories)
├── domain/         # Domain layer (models, use cases, repository interfaces)
├── ui/             # Presentation layer (Activities, ViewModels)
│   ├── home/       # Main screen
│   ├── viewer/     # PDF viewer
│   ├── tools/      # PDF operations
│   ├── scanner/    # Document scanner
│   └── settings/   # App settings
├── pdf/            # PDF engine (Pdfium + PDFBox)
├── ocr/            # OCR engine (Tesseract)
├── ads/            # AdMob integration
└── di/             # Hilt dependency injection
```

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Kotlin |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| Async | Kotlin Coroutines + Flow |
| PDF Rendering | PdfiumAndroid |
| PDF Operations | PdfBox-Android |
| OCR | Tesseract4Android |
| UI | XML (legacy) + Jetpack Compose (modern) |
| Database | Room |
| Preferences | DataStore |
| Ads | Google AdMob |

## Compatibility

- **Minimum SDK**: 21 (Android 5.0 Lollipop)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34

### Device Optimization
- Automatically detects low-RAM devices
- Reduces rendering quality on low-end devices
- Disables animations on devices with < 3GB RAM
- Uses RGB_565 bitmap format on low-end devices
- Adaptive bitmap pooling based on available memory

## Building

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK with API 34

### Setup
1. Clone the repository
2. Open in Android Studio
3. Add your AdMob App ID in `AndroidManifest.xml`
4. Add your `google-services.json` to the `app/` directory
5. Build and run

### Release Build
```bash
./gradlew assembleRelease
```

## Monetization

This app is **completely free** for all users. Revenue is generated through:
- Banner ads (viewer screen)
- Native ads (file list)
- Interstitial ads (limited to once per 3 minutes)

Ads are disabled on devices with < 2GB RAM to ensure smooth performance.

## Open Source Licenses

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

Third-party licenses are documented in [open_source_licenses.md](open_source_licenses.md) and accessible from within the app.

## Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## Privacy

- All PDF processing is done on-device
- OCR runs locally using Tesseract
- No user data is collected or transmitted
- AdMob may collect device identifiers per Google's policies

## Acknowledgments

- [PdfiumAndroid](https://github.com/barteksc/PdfiumAndroid) by Bartosz Schiller
- [AndroidPdfViewer](https://github.com/barteksc/AndroidPdfViewer) by Bartosz Schiller
- [Tesseract OCR](https://github.com/tesseract-ocr/tesseract)
- [PdfBox-Android](https://github.com/TomRoush/PdfBox-Android) by Tom Roush
