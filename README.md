# Swift 🚀

Swift is a minimalist, high-speed, and secure peer-to-peer file transfer engine built in pure Java. No bloat, no complex configuration—just pure performance and trust.

### 💡 Why Swift?
In a world of bloated file-sharing apps, Swift goes back to basics. It was born from the need for a simple, developer-friendly way to move data across a local network without sacrificing security or speed. It's built for those who value clean code and raw throughput.

---

### 🔄 The Lifecycle
Swift uses a rigid multi-stage verification process to ensure your data arrives exactly how it was sent.

```mermaid
graph LR
    A[File Selection] --> B[SHA-256 Hashing]
    B --> C[Network Discovery]
    C --> D[Handshake & PIN Pairing]
    D --> E[Buffered Transfer]
    E --> F[Integrity Verification]
    F --> G[Done]
```

---

### 🏎️ Key Features
- **High-Speed Buffering**: Utilizes a 64KB optimized buffer to maximize network throughput.
- **PIN Pairing**: A custom handshake prevents unauthorized transfers via 4-digit code verification.
- **Integrity First**: Automatic SHA-256 checksum validation ensures zero data corruption.
- **Interface Driven**: Modular architecture for easy extensibility and clean inheritance.

---

### 🚦 Quick Start

**1. Compile the project:**
```bash
javac -d . interfaces/*.java utils/*.java core/*.java Main.java
```

**2. Start a Receiver:**
```bash
java Main 
# Choose option 1
```

**3. Start a Sender:**
```bash
java Main
# Choose option 2, enter Receiver IP and File Path
```

---

### 📘 Documentation
- [**BLUEPRINT.md**](./BLUEPRINT.md): Technical architecture and class diagrams.
- [**PROTOCOL.md**](./PROTOCOL.md): Deep dive into the security and handshake specs.

---
*Created with ❤️ for performance-focused developers.*
