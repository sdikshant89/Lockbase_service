# 🔐 LockBase

A zero-knowledge password manager with end-to-end encryption. Vault contents and search queries are encrypted client-side — the server never sees your plaintext data.

---

## Table of Contents

- [Sign Up Flow](#1-sign-up-flow)
- [Login & Key Derivation](#2-login--key-derivation)
- [Encryption Keys: `encKey` vs `searchKey`](#3-encryption-keys-enckey-vs-searchkey)
- [Password Save & Retrieve](#4-password-save--retrieve)
- [Security Model](#security-model)

---

## 1. Sign Up Flow

### Frontend

1. User fills in demographic info and chooses a password.
2. User is redirected to the security questions page *(no backend call yet)*.
3. Once the user selects 2 security questions and provides answers, the `register_user` endpoint is called.

### Backend — `register_user`

User details and security questions are saved sequentially — user details first, then security questions using the returned primary key.

The following fields are stored alongside the standard demographic info:

| Field | Description |
|---|---|
| `isVerified` | Whether the user has verified their email via OTP (default: `false`) |
| `OTP` | Hash of the OTP sent to the user's email |
| `OTP_expiry` | Expiry timestamp of the OTP |
| `iv_pass` | Random bytes used to randomize encryption of the user's password |
| `salt_pass` | Random salt ensuring identical passwords encrypt differently |
| `enc_prk_pass` | Encrypted form of `prkPlainText` (see below) |
| `iv_recovery` | Random bytes for encrypting the security question combination |
| `salt_recovery` | Random salt for the recovery path |
| `enc_prk_recovery` | `prkPlainText` encrypted via security question answers |

#### How `prkPlainText` works

A random string `prkPlainText` is generated at signup. It serves as the root key for all vault encryption, but it cannot be stored in plaintext. Instead, it is encrypted **twice** via two separate paths:

**Password path** — encrypted using the user's plaintext password + `iv_pass` + `salt_pass`, producing `enc_prk_pass`. At login, the frontend receives `iv_pass`, `salt_pass`, and `enc_prk_pass` and reconstructs `prkPlainText` by decrypting with the user's entered password.

**Recovery path** — encrypted using a concatenation of the user's security question answers + `iv_recovery` + `salt_recovery`, producing `enc_prk_recovery`. If the user forgets their password, security questions are used to reconstruct `prkPlainText` instead.

### OTP Verification

After `register_user` succeeds, an OTP is generated and stored (hashed) with an expiry.

**Frontend:** User is redirected to the OTP verification page.

**Backend — `verifyOTP`:** The submitted OTP is validated against the stored hash and expiry. On success:
- `OTP` and `OTP_expiry` are set to `NULL`
- `isVerified` is set to `true`

**Frontend:** On a successful response, the user is redirected to the Login page.

---

## 2. Login & Key Derivation

### Frontend

After a successful login, the backend returns `iv_pass`, `salt_pass`, and `enc_prk_pass`. The frontend decrypts `enc_prk_pass` using the user's entered password to recover `prkPlainText`.

`prkPlainText` becomes the **root key**, from which two cryptographic keys are derived using HKDF:

```js
encKey    = HKDF(rootKey, salt = vault_salt, info = "lockbase-enc-v1",    length = 32);
searchKey = HKDF(rootKey, salt = vault_salt, info = "lockbase-search-v1", length = 32);
```

`vault_salt` is a random salt generated at signup and stored in the backend. The `info` strings are the only difference between the two keys — they ensure the keys are cryptographically distinct even though they share the same root.

> **These keys are never stored.** They exist only in memory while the vault is unlocked, preserving the zero-knowledge guarantee.

---

## 3. Encryption Keys: `encKey` vs `searchKey`

Although both keys are derived from the same root, they serve different purposes and must never be used interchangeably.

### `encKey` — Vault Encryption

Used with **AES-GCM** to encrypt and decrypt vault item contents: title, username/email, password, URL, notes, and custom fields.

- AES-GCM provides both **confidentiality** (server cannot read data) and **integrity** (tampering is detectable on decrypt).
- A fresh random **IV** is generated per vault item and stored alongside the ciphertext.
- Decryption uses the same `encKey` + the stored per-item IV to recover the original JSON payload.

### `searchKey` — Search Indexing

Used with **HMAC-SHA256** to produce deterministic, opaque search tokens for vault fields (title, username/email, URL/domain, tags).

- HMAC is **not** encryption — its output is not reversible.
- On save, relevant fields are tokenized (split into words, domain extracted, etc.) and `HMAC(searchKey, token)` is computed for each. These hashes are stored in a separate `tokens` table.
- On search, the query is tokenized the same way, HMAC values are computed, and the backend returns items whose stored token hashes match — **without ever seeing the original search terms**.

### Why Keep Them Separate?

Key separation is a deliberate security design choice:

- AES-GCM and HMAC have different security properties and failure modes — reusing a single key across both is considered poor practice.
- Changes to search-token rules (e.g. indexing only domains, adding tag indexing) don't affect the encryption path, and vice versa.
- The system is easier to reason about, audit, and upgrade when each key has exactly one job.

---

## 4. Password Save & Retrieve

### Frontend — On Save of New Password/PIN

> *(Full detail for this section coming soon)*

The client encrypts the vault item using `encKey` + a fresh random IV, then computes HMAC search tokens for the indexable fields using `searchKey`. Both the encrypted payload and the tokens are sent to the backend.

---

## Security Model

| Property | How it's achieved |
|---|---|
| **Zero-knowledge server** | All encryption/decryption happens client-side; the server only stores ciphertext and HMAC tokens |
| **Password-based recovery** | `prkPlainText` encrypted with user password via `enc_prk_pass` |
| **Security question recovery** | `prkPlainText` encrypted with question answers via `enc_prk_recovery` |
| **Authenticated encryption** | AES-GCM detects any tampering with vault data |
| **Private search** | HMAC-SHA256 tokens allow server-side lookup without exposing plaintext queries |
| **Key separation** | `encKey` and `searchKey` are derived independently via HKDF; neither is reused |
| **No key persistence** | Derived keys live in memory only — never written to DB or localStorage |
