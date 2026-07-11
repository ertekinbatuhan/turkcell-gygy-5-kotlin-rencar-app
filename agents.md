# Rencar — Agent Guide

Bu repository Rencar adında bir araç kiralama/paylaşım uygulamasının kaynak kodlarını içermektedir. Bu agents.md dosyası bu projede çalışan insan/ai herkesin uyması ZORUNDA olduğu genel kuralları içerir.

Android app for a car-sharing/rental service. Kotlin + Jetpack Compose, MVVM, type-safe Navigation Compose. Backend is a separate NestJS-style REST API — **the mockups (Figma/HTML canvas) describe a richer product vision (per-minute billing, wallet, unlock button, fuel %, live GPS) than the real API currently supports.** Always build against the real API contract below, not the mockup copy.

## Tech stack (current)

- Kotlin, Jetpack Compose (Material 3), `compileSdk 36` / `minSdk 24`
- Navigation Compose with type-safe `@Serializable` routes (`kotlinx.serialization`)
- MVVM: `XyzScreen` (Compose) + `XyzViewModel` + `XyzUiState` per feature (see `ui/screens/profile` for the current pattern)
- No DI framework wired up yet (no Hilt/Koin) — don't assume one exists
- No networking library wired up yet (no Retrofit/Ktor) — don't assume a client exists; if adding one, prefer Retrofit + OkHttp + kotlinx.serialization converter to match the serialization dependency already present
- Package root: `com.flowbytestudio.rencar`
- Bottom nav has 4 destinations today: `MapRoute`, `HistoryRoute`, `WalletRoute`, `ProfileRoute` (`navigation/AppRoute.kt`, `AppNavGraph.kt`, `RencarNavBar.kt`, `BottomNavItem.kt`)

## Source layout

```
app/src/main/java/com/flowbytestudio/rencar/
  MainActivity.kt
  navigation/          AppNavGraph, AppRoute, BottomNavItem, RencarNavBar
  ui/screens/<feature>/ <Feature>Screen.kt, <Feature>ViewModel.kt, <Feature>UiState.kt
  ui/theme/             Color, Theme, Type
```

When adding a new screen, follow the existing `profile` package as the template (Screen + ViewModel + UiState split), and register the route in `AppRoute.kt` + wire it in `AppNavGraph.kt` + `RencarNavBar.kt`/`BottomNavItem.kt` if it belongs in bottom nav.

## Backend API — ground truth

Base docs (Swagger UI, client-rendered): `https://rencar.halitkalayci.com/api/docs`
Machine-readable spec (use this, not the UI page, when you need exact schemas): `https://rencar.halitkalayci.com/api/docs-json`

Title: **Araç Kiralama API** v1.0. Auth is JWT bearer (`accessToken`, ~15 min) with a separate rotating `refreshToken` (~7 days). All endpoints except `/health`, `/auth/register`, `/auth/login`, `/auth/verify-otp`, `/auth/refresh` require `Authorization: Bearer <accessToken>`.

### Auth flow (important — do not build a different one)

Login is **two-step, phone + OTP**, not email/password:

1. `POST /auth/register` — `{ email, password, fullName, phone }` → `AuthResponseDto` (tokens + user). Phone is required and unique; no phone means no login.
2. `POST /auth/login` — `{ phone }` → `OtpRequiredResponseDto` (`message`, `phone`, `expiresAt`). This "sends" an SMS (simulated).
3. `POST /auth/verify-otp` — `{ phone, code }` (6-digit, default simulated code `123456`) → `AuthResponseDto`.
4. `POST /auth/refresh` — `{ refreshToken }` → new `AuthResponseDto` (rotation: old refresh token is invalidated).
5. `POST /auth/logout` (auth) → revokes refresh sessions.
6. `GET /auth/me` (auth) → `UserResponseDto`.

`UserResponseDto.role` is an enum: `PENDING | CUSTOMER | ADMIN`. New users start `PENDING` until their license is approved, then become `CUSTOMER`.

### License verification (maps to mockup screen "11")

- `POST /license/upload` (auth, multipart) — fields `front`, `back` (image files, jpg/png, ≤5MB each) → `LicenseResponseDto`.
- `GET /license/status` (auth) → `LicenseStatusResponseDto` with `status: NOT_SUBMITTED | UNDER_REVIEW | APPROVED | REJECTED` and `rejectReason` when rejected.
- Admin side: `GET /admin/licenses`, `GET /admin/licenses/{id}`, `PATCH /admin/licenses/{id}/approve`, `PATCH /admin/licenses/{id}/reject` (`{ reason }`).

There is **no separate selfie/liveness step and no 3-step wizard** in the real API — it's just front+back upload, then poll/await status. If implementing the mockup's 3-step UI, steps 2–3 must be client-side only or dropped.

### Vehicles

- `GET /vehicles` (auth) → list of **available** vehicles only (`VehicleResponseDto[]`).
- `GET /vehicles/{id}` (auth) → single available vehicle.
- Fields: `id, plate, brand, model, type (SEDAN|SUV|HATCHBACK|STATION|MINIVAN), pricePerDay, status (AVAILABLE|RENTED|MAINTENANCE), latitude, longitude, createdAt, updatedAt`.
- **No `fuelLevel`, `range`, `transmission`, `seats`, or per-minute price fields exist.** The vehicle detail mockup's fuel gauge / manual-transmission / 5-seat / ₺/dk chips are not backed by the API — either drop them from the UI or treat them as future/mock-only data, don't wire them to real fields.
- Admin CRUD: `POST /admin/vehicles`, `GET /admin/vehicles`, `GET /admin/vehicles/{id}`, `PATCH /admin/vehicles/{id}`, `DELETE /admin/vehicles/{id}` — same shape via `CreateVehicleDto`/`UpdateVehicleDto`.

### Rentals (maps to mockup screens "05"–"07", simplified)

- `POST /rentals` (auth) — `{ vehicleId, endDate (ISO future date) }` → `RentalResponseDto`. **Billing is by whole days (`totalPrice = days * pricePerDay`), locked in at creation — not per-minute/per-hour as the mockup shows.** There's no "reservation" step, no plan picker (dakikalık/saatlik/günlük), no free-reservation window, no unlock/lock action, no live distance/duration ticker, and no discount codes — none of that exists server-side.
- `GET /rentals` (auth) → my rentals (`RentalResponseDto[]`).
- `GET /rentals/{id}` (auth) → my rental detail.
- `POST /rentals/{id}/return` (auth) → ends the rental (no body).
- `status`: `ACTIVE | COMPLETED | CANCELLED`.
- Admin: `GET /admin/rentals`, `GET /admin/rentals/{id}` → `AdminRentalResponseDto` (includes nested `user`/`vehicle` summaries).

### What the mockups show that has no API counterpart

Do not implement these against a real backend call — there is nothing to call:
- **Wallet / balance / saved payment cards / top-up / transaction history** — no `/wallet` or `/payments` endpoints exist at all.
- **Payment/checkout screen with card selection, discount line items, service fee** — no payment endpoint; rentals have no payment step, just `totalPrice`.
- **Per-minute meter, "kilidi aç/kilitle" (unlock/lock), live route polyline during an active rental** — no telemetry endpoint for a single rental beyond its DB row.
- **Live map pins with per-vehicle fuel/price bubbles** — closest real equivalent is `GET /admin/locations` (admin-only, `VehiclePositionDto[]`: `vehicleId, plate, status, latitude, longitude, updatedAt`), which is a snapshot, not a live per-customer feed, and customers can't call it.

If a task asks to "implement screen N," check this section first and flag to the user which parts are mockup-only before building fake local state that looks wired but isn't.

### Misc

- `GET /health` — unauthenticated liveness probe.
- `GET /admin/ping`, `GET /customer/ping` — role-gated smoke-test endpoints, no real payload.

## Working conventions

- Screen text in the mockups is Turkish; keep UI copy in Turkish unless told otherwise.
- When wiring a screen to the API, name request/response DTOs after the OpenAPI schema names above (e.g. `RentalResponseDto`, `VehicleResponseDto`) so API and app vocabulary stay aligned.
- Re-fetch `https://rencar.halitkalayci.com/api/docs-json` if the API may have changed — don't rely on this file's schema snapshot indefinitely for anything security- or billing-sensitive.

## 2) GENEL ÇALIŞMA PRENSİPLERİ

### 2.1) TEK SEFERDE DOSYA LİMİTİ

Hangi işlem olursa olsun tek seferde maksimum (birbiriyle alakalı) 5 dosyalar halinde çalışmak zorundasın. İşlemi birbiriyle bağlantılı batchlere bölmek zorundasın. Eğer bunun aksi talep edilirse DUR ve EK ONAY iste.

### 2.2) UYDURMAK YASAK (NO INVENTING)

Eğer herhangi bir operasyonda bilgi ya da referans eksikliği/hatası yaşıyorsan (API şeması, dosya yapısı, bağımlılık, mockup-API uyuşmazlığı dahil) buradaki eksik/hatalı bilgiyi uydurman yasak. Böyle bir durumda operasyonu durdur ve kullanıcıya sorarak ilerle. Bu özellikle "Backend API — ground truth" bölümünde işaretlenen mockup-only alanlar için geçerlidir: API'de karşılığı olmayan bir veriyi varmış gibi mock'lamak yasak, kullanıcıya açıkça söylenmeli.

### 2.3) ÖNCE PLANLA, SONRA KODLA

Kod üretmeden önce şunları yapmak zorundasın:

- Bir dosya dökümü hazırla (hangi dosyalar değişecek/eklenecek/silinecek + neden)
- Eğer varsa yeni bağımlılıklar matrisi (Hangi kütüphane, versiyon + neden) — özellikle bu projede henüz DI/networking kütüphanesi yok, biri eklenecekse bu adım zorunlu
- Planı sun, onay almadan asla implementasyona başlama.

Bunun istisnası: tek dosyada, tek satırlık, geri alınması trivial düzeltmeler (typo, import düzeltme) — bunlarda plan sunmadan direkt yapıp ne değiştiğini raporla.

### 2.4) DİĞER GENEL KURALLAR

- **Ask before acting** riskli/geri dönüşü zor işlemlerde: yeni bağımlılık/build-system değişikliği, dosya silme/yeniden adlandırma, git işlemleri (commit/push/branch/reset), şema/DB değişikliği, CI config değişikliği.
- **No comments explaining what code does** — isimler bunu zaten anlatmalı. Sadece gerçekten non-obvious bir *neden* (workaround, gizli constraint) için tek satır yorum.
- **No speculative abstractions.** DI, repository, use-case katmanı, "ileride kullanılır" config flag'leri ekleme — sadece o anki task'ın gerektirdiğini yap.
- **No unrelated cleanup riding along with a feature/fix.** Başka bir sorun fark edersen söyle, aynı değişikliğe sessizce ekleme.
- **Turkish for user-facing strings, English for code** (identifier, yorum, commit mesajı) — ikisini birbirine karıştırma.
- **Never commit or push without being asked**, değişiklik tamamlanmış görünse de.
- **Match existing patterns before introducing new ones** — örn. `profile` paketindeki Screen/ViewModel/UiState ayrımını takip et, her feature için yeni bir yapı icat etme.
- **No hardcoded/mock data**, gerçek bir veri kaynağının (API, kullanıcı girdisi, kalıcı storage) olmadığı ve bunun mock'lanmasının o anki task için zorunlu/açıkça istenmiş olduğu senaryolar dışında. Mock kullanılan her yerde bunun mock olduğu ve neden mock'landığı açıkça belirtilmeli (yorum veya plan/rapor içinde), sessizce sahte veri sızdırılmamalı.
- **Clean code ve profesyonellik zorunludur.** Anlamlı isimlendirme, tek sorumluluk, tekrar etmeyen kod (DRY), okunabilir kontrol akışı, tutarlı formatlama — bunlar opsiyonel bir tercih değil, her implementasyonda uyulması gereken asgari standarttır.
- **Geçmiş ihlaller düzeltilmeden bırakılmaz.** Üzerinde çalışılan alanda (dokunulan dosya/özellik) daha önce bu prensiplere (clean code, no-hardcode/mock, mimari desen tutarlılığı vb.) uyulmadığı fark edilirse, bu iş kapsamının parçası sayılır ve gerekirse ilgili tasarım/kod komple yeniden yapılır — "değişmesin, dokunulmasın" varsayılmaz. Bu, 2.4'teki "no unrelated cleanup" kuralını geçersiz kılmaz: kapsam yalnızca üzerinde çalışılan özellik/ekranla sınırlı kalır, alakasız dosyalara sirayet etmez.

## 3) ÇIKTI FORMATI

Her implementasyon ya da plan sonrası aşağıdaki çıktı formatına uymak zorundasın:

- **Dosya Dökümü** vermek zorundasın (değişen/eklenen/silinen dosyalar).
- **Resmi bir dil** kullanmak zorundasın, **emoji kullanman yasak** (kod, commit mesajı, UI metni, chat yanıtı dahil — kullanıcı açıkça istemedikçe).
- Her implementasyon sonrası eğer mümkünse **"Happy-Path Test"** ver (örn. hangi ekranı açıp hangi aksiyonu tetikleyince ne görülmeli).
- Bu implementasyonla ilgili varsa **sık yapılan hatalar** ve implementasyon sırasında aklına gelen **önerileri** listelemek zorundasın.
- Yanıtları kısa tut; keşif/düşünme sürecini adım adım anlatma, sonucu ve sıradaki adımı bildir.
