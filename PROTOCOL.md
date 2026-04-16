# PROTOCOL 📜

The Swift protocol defines how two nodes negotiate a transfer, verify identity, and ensure data integrity.

### 🤝 The Handshake Sequence

The handshake is a synchronous, multi-step process designed to prevent "blind" file transfers.

1.  **Identity Broadcast**: The Sender connects and immediately sends its `Name`, the `FileName`, and the `FileSize`.
2.  **User Consent**: The Receiver displays this metadata and waits for the user to type `y`.
3.  **PIN Challenge**:
    - The Sender generates a random 4-digit PIN.
    - The Receiver prompts the user to enter this PIN.
    - The Receiver sends the entered PIN to the Sender.
4.  **Verification**: The Sender compares the PINs. If they match, it sends `true` (success); otherwise, it sends `false` and terminates the connection.

---

### 🛡️ Security Specifications

#### SHA-256 Integrity
Before the first byte of the file is even sent, the Sender calculates a **SHA-256** hash of the local file. This hash is sent to the Receiver after the successful PIN handshake.
- **Algorithm**: SHA-256
- **Timing**: Calculated pre-transfer, verified post-transfer.
- **Goal**: Detect bit-rot, incomplete transfers, or accidental corruption.

#### PIN Pairing
The PIN pairing mechanism acts as a simple but effective "local proximity" check. It ensures that the person receiving the file is physically (or via a trusted side-channel) in communication with the sender.

---

### ⚡ Performance Specs

Swift is optimized for local network (LAN) speeds.

- **Buffer Size**: `64 KB`. (Standard Sockets often default to 8KB or 16KB).
- **Control Flow**: We use `Math.min` in the receiver loop to ensure we never read more bytes than the file size, preventing "hangs" on the input stream.
- **Timing**: The Sender tracks total transfer duration in nanoseconds for high-resolution performance logging.

---

### 🛠️ Troubleshooting

| Issue | Likely Cause | Solution |
| :--- | :--- | :--- |
| **Connection Refused** | Firewall or IP mismatch | Ensure Port 8888 is open in your local firewall. |
| **PIN Mismatch** | Typo or impersonation | Double-check the code on the sender's screen. |
| **Hash Failed** | Data corruption | Usually caused by unstable Wi-Fi. Retry the transfer. |

---

### 🚀 Roadmap

- [ ] **UDP Multicast Discovery**: Finding peers without typing IPs.
- [ ] **Zip Support**: Drag-and-drop entire folders.
- [ ] **Transfer Resumption**: Resuming interrupted downloads using `RandomAccessFile`.
- [ ] **Encryption**: Optional AES-256 wrapping for untrusted networks.
