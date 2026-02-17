# TOONToKotlinClass

[![Build](https://img.shields.io/badge/build-passing-brightgreen)]()
[![License](https://img.shields.io/badge/license-MIT-blue)]()
[![Kotlin](https://img.shields.io/badge/kotlin-1.9.22-purple)]()
[![IntelliJ Platform](https://img.shields.io/badge/IntelliJ-2023.3-orange)]()

> Convert TOON (Tree Object Oriented Notation) format into production-ready Kotlin data classes

An IntelliJ IDEA plugin that transforms hierarchical, indentation-based TOON text into Kotlin data classes with support for multiple serialization frameworks.

## Features

✨ **Live Preview** - See generated Kotlin code as you type (500ms debounce)  
🎯 **Multiple Frameworks** - Gson, Moshi, Kotlinx Serialization, Firebase Firestore  
📦 **Package Support** - Automatic package structure creation  
🔍 **Smart Type Inference** - Detects Int, Long, Float, Double, Boolean, String  
🌳 **Nested Objects** - Full support for deeply nested structures  
📋 **Lists & Arrays** - Simple lists and object lists with schema  
✅ **Validation** - Comprehensive error checking with helpful messages  
🎨 **Modern UI** - Clean, intuitive interface with auto-preview  

## Installation

### From Source

```bash
git clone https://github.com/yourusername/TOONToKotlinClass.git
cd TOONToKotlinClass
./gradlew buildPlugin
```

The plugin will be built to `build/distributions/TOONToKotlinClass-*.zip`

Install in IntelliJ IDEA:
1. Go to **Settings** → **Plugins** → **⚙️** → **Install Plugin from Disk**
2. Select the generated ZIP file

## Usage

### 1. Open the Generator

- **Menu**: Code → Generate → "Generate Kotlin Data Class from TOON"
- **Shortcut**: `Cmd+N` (Mac) / `Ctrl+N` (Windows) → Select "Generate Kotlin Data Class from TOON"

### 2. Enter TOON Format

```toon
UserProfile:
  userId: 5001
  username: saroj_dev
  email: saroj@example.com
  isVerified: false
  preferences:
    theme: dark
    notifications:
      email: true
      sms: false
      push: true
  stats:
    followers: 1200
    following: 300
    achievements[2]{title,year}:
      Top Guide,2025
      Explorer,2024
```

### 3. Configure Options

- **Package Name**: `com.example.models`
- **Annotation Framework**: Gson / Moshi / Kotlinx / Firebase
- **Use val**: Generate immutable properties
- **Nullable fields**: Add `?` to all types

### 4. Generate Files

Click **Generate Files** (`Cmd+G` / `Ctrl+G`) to create:

```kotlin
// UserProfile.kt
package com.example.models

import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("userId")
    val userId: Int?,
    
    @SerializedName("username")
    val username: String?,
    
    @SerializedName("email")
    val email: String?,
    
    @SerializedName("isVerified")
    val isVerified: Boolean?,
    
    @SerializedName("preferences")
    val preferences: Preferences?,
    
    @SerializedName("stats")
    val stats: Stats?
)
```

## TOON Format Specification

### Basic Syntax

| Pattern | Example | Description |
|---------|---------|-------------|
| Object | `key:` | Nested object |
| Property | `key: value` | Simple property |
| List | `key[n]: a,b,c` | Comma-separated list |
| Object List | `key[n]{schema}:` | List of objects |

### Rules

- **Indentation**: 2 spaces per level (tabs not allowed)
- **Top-level**: Must start with a root object at indent 0
- **Keys**: Use `snake_case` (converted to `camelCase` in Kotlin)
- **Types**: Auto-inferred from values

### Type Inference

| TOON Value | Kotlin Type |
|------------|-------------|
| `123` | `Int` |
| `123L` | `Long` |
| `12.5` | `Double` |
| `12.5f` | `Float` |
| `true` / `false` | `Boolean` |
| `"text"` or `text` | `String` |
| `null` | `String` (empty) |

## Examples

### Nested Objects

```toon
Product:
  productId: 1
  name: Laptop
  price: 999.99
  manufacturer:
    name: TechCorp
    country: USA
```

### Simple Lists

```toon
BlogPost:
  title: Getting Started
  tags[3]: kotlin,programming,tutorial
```

### Object Lists

```toon
HikingGuide:
  hikes[3]{id,name,distance_km}:
    1,Mount Everest,8848
    2,K2,8611
    3,Kilimanjaro,5895
```

## Architecture

```
TOONToKotlinClass/
├── action/              # IntelliJ action handlers
├── generator/           # Kotlin code generation
├── parser/              # TOON parsing & validation
├── ui/                  # Dialog interface
└── writer/              # PSI file writing
```

### Key Components

- **ToonParser** - Converts TOON text to AST
- **ToonValidator** - Validates syntax and structure
- **KotlinClassGenerator** - Generates Kotlin code
- **ToonGenerateDialog** - User interface
- **PsiKotlinWriter** - Writes files to project

## Development

### Prerequisites

- JDK 17+
- Gradle 8.x
- IntelliJ IDEA 2023.3+

### Build

```bash
./gradlew build
```

### Run Plugin in Development

```bash
./gradlew runIde
```

### Run Tests

```bash
./gradlew test
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
