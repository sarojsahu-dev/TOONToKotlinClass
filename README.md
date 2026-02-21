# TOONToKotlinClass

[![Version](https://img.shields.io/jetbrains/plugin/v/30297.svg)](https://plugins.jetbrains.com/plugin/30297-toontokotlinclass)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/30297.svg)](https://plugins.jetbrains.com/plugin/30297-toontokotlinclass)
[![Install](https://img.shields.io/badge/Install-JetBrains%20Marketplace-blue?logo=jetbrains)](https://plugins.jetbrains.com/plugin/30297-toontokotlinclass)

An IntelliJ IDEA / Android Studio plugin that generates Kotlin data classes from **TOON** [Token-Oriented Object Notation](https://toonformat.dev/) — a lightweight, human-readable format for defining data structures.

> **TOON** is designed to be simpler than JSON for quickly sketching out data models. Write your schema in plain text and instantly get production-ready Kotlin code.

---

## ✨ Features

- 🔄 **Instant Conversion** — Write TOON, see Kotlin data classes in real-time preview
- 📦 **Nested Objects** — Supports deeply nested structures with inner class generation
- 📋 **Lists & Object Lists** — Arrays and table-style data with headers and rows
- 🏷️ **11 Annotation Frameworks** — Gson, Jackson, Moshi, Fastjson, kotlinx.serialization, LoganSquare, Firebase, and more
- ⚙️ **Advanced Settings** — Full control via 4-tab settings dialog (Property, Annotation, Other, Extensions)
- 🧠 **Smart Type Inference** — Automatically detects `Int`, `Long`, `Double`, `Boolean`, `String` from values
- ✍️ **Format & Validate** — Built-in TOON formatter and validator

---

## 📸 Screenshots

<!-- Add your screenshots here -->
<!-- ![Main Dialog](screenshots/main_dialog.png) -->
<!-- ![Advanced Settings](screenshots/advanced_settings.png) -->

---

## 🚀 Quick Start

### Installation

1. Open **IntelliJ IDEA** or **Android Studio**
2. Go to **Settings → Plugins → Marketplace**
3. Search for **TOONToKotlinClass**
4. Click **Install** and restart

### Usage

1. Right-click in any Kotlin file → **Generate** → **Generate Kotlin Data Class from TOON**
2. Write or paste your TOON input on the left
3. See the generated Kotlin code on the right in real-time
4. Click **OK** to insert the code

---

## 📝 TOON Format

TOON uses indentation-based nesting with simple `key: value` pairs.

### Example

**TOON Input:**
```
user:
  id: 101
  name: Saroj
  email: saroj@example.com
  is_active: true
  balance: 999.99
  address:
    city: Bangalore
    zip_code: 560001
  tags[3]: android,kotlin,dev
  orders[2]{order_id,item,price}:
    1,Laptop,75999.50
    2,Mouse,499.00
```

**Generated Kotlin:**
```kotlin
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val isActive: Boolean,
    val balance: Double,
    val address: Address,
    val tags: List<String>,
    val orders: List<Order>
)

data class Address(
    val city: String,
    val zipCode: Int
)

data class Order(
    val orderId: Int,
    val item: String,
    val price: Double
)
```

---

## 📖 TOON Syntax Reference

| Syntax | Description | Example |
|--------|-------------|---------|
| `key: value` | Simple property | `name: Saroj` |
| `key:` (with indented children) | Nested object | `address:` → `city: NY` |
| `key[N]: a,b,c` | List of N items | `tags[3]: a,b,c` |
| `key[N]{h1,h2}: rows` | Object list with headers | `items[2]{id,name}: ...` |

### Type Inference

| Value | Inferred Type |
|-------|---------------|
| `42` | `Int` |
| `100L` | `Long` |
| `3.14` | `Double` |
| `true` / `false` | `Boolean` |
| Everything else | `String` |

---

## ⚙️ Advanced Settings

Click **Advanced** in the main dialog to access the full settings:

### Property Tab
- **Keyword** — `val` or `var`
- **Type** — Non-Nullable, Nullable, or Auto Determine
- **Default Value Strategy** — No default, Non-null default, or Null when nullable

### Annotation Tab
Choose from 11 annotation frameworks:

| Framework | Annotation |
|-----------|------------|
| None | _(no annotation)_ |
| Gson | `@SerializedName("key")` |
| Jackson | `@JsonProperty("key")` |
| Moshi (Reflect) | `@Json(name = "key")` |
| Moshi (Codegen) | `@Json(name = "key")` |
| Fastjson | `@JSONField(name = "key")` |
| kotlinx.serialization | `@SerialName("key")` + `@Serializable` |
| LoganSquare | `@JsonField(name = "key")` + `@JsonObject` |
| Firebase | `@PropertyName("key")` |
| Custom | Your own template |

### Other Tab
- Append original TOON as comment
- Enable alphabetical ordering
- Enable inner class model
- Configurable indent & parent class template

### Extensions Tab
- `@Keep` annotation (standard / AndroidX)
- Parcelable support (`@Parcelize`)
- Property & class prefix/suffix
- Disable data class / use member variables
- Internal visibility modifier
- Gson `@Expose` annotation

---

## 🛠️ Building from Source

```bash
git clone https://github.com/sarojsahu/TOONToKotlinClass.git
cd TOONToKotlinClass
./gradlew buildPlugin
```

The plugin ZIP will be in `build/distributions/`.

### Run in Development

```bash
./gradlew runIde
```

---

## 🤝 Contributing

Contributions are welcome! Feel free to:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📬 Contact

**Saroj Sahu**
- 📧 Email: [sarojsahu14369@gmail.com](mailto:sarojsahu14369@gmail.com)
- 🐙 GitHub: [@sarojsahu](https://github.com/sarojsahu-dev)

---

## ⭐ Star this repo

If you find this plugin useful, give it a ⭐ on GitHub — it helps others discover it!
