# Digital Maintenance & Issue Tracker

Bu proje, sahadaki donanım ürünlerinin (HMI, LED Panel, LCD) bakım süreçlerini ve arıza bildirimlerini dijitalleştirmek için geliştirdiğim bir takip uygulamasıdır.

## Kullanılan Teknolojiler
- **Dil & Framework:** Kotlin, Jetpack Compose (Native Android)
- **Veritabanı & Auth:** Firebase Firestore, Firebase Authentication
- **Görsel Yönetimi:** Cloudinary API & OkHttp
- **CI/CD:** GitHub Actions

## Özellikler

### 1. Saha Personeli (Bakım & Arıza Bildirimi)
- Firebase Auth ile giriş.
- Envanter üzerinden cihaz seçimi.
- "Çalışıyor", "Arızalı", "Eksik" durum bildirimi.
- Arıza durumunda kamera izni alarak fotoğraf çekme ve Cloudinary ile buluta yükleme.
- Firestore'a not ve fotoğraf URL'si ile rapor kaydetme.

### 2. Yönetici (Admin) Paneli
- Sahadan gelen arıza raporlarının anlık listelenmesi.
- Raporların notlar ve görsellerle detaylı görüntülenmesi.
- Yeni kullanıcı tanımlama (Admin/Personel rolü seçimi).

## Kurulum ve Test

1. **Repo Klonlama:**
   ```bash
   git clone https://github.com/zaferKarakurt/Piton-Maintenance-Tracker.git
   ```

2. **Android Studio'da Açma:**
   - Android Studio'yu açın → **File → Open** → klonlanan klasörü seçin.
   - Gradle sync otomatik başlayacaktır, tamamlanmasını bekleyin.

3. **Firebase Yapılandırması:**
   - [Firebase Console](https://console.firebase.google.com)'dan bir Android projesi oluşturun.
   - `google-services.json` dosyasını indirip `app/` dizinine ekleyin.
   - Firestore, Authentication ve Storage servislerini aktif edin.

4. **Uygulamayı Çalıştırma:**
   - Bir Android cihaz veya emülatör bağlayın (API 26+).
   - Android Studio'da **Run** butonuna basın.

5. **Test Hesapları:**
   - Admin: `admin@test.com` / `123456`
   - Personel: `personel@test.com` / `123456`

## Ekran Görüntüleri

<img width="384" height="859" alt="Ekran görüntüsü 2026-05-30 083955" src="https://github.com/user-attachments/assets/49e55b77-8436-426d-b805-1591e73ab391" />
<img width="386" height="859" alt="Ekran görüntüsü 2026-05-30 084017" src="https://github.com/user-attachments/assets/02f8c277-773e-424f-945f-a7fa3fcfb0c8" />
<img width="387" height="862" alt="Ekran görüntüsü 2026-05-30 084358" src="https://github.com/user-attachments/assets/7c1fcd43-c2db-4890-b140-d1d8fb5b8469" />
<img width="387" height="860" alt="Ekran görüntüsü 2026-05-30 084500" src="https://github.com/user-attachments/assets/77eb1920-584c-4256-8203-bdee0ed069b8" />
<img width="374" height="858" alt="Ekran görüntüsü 2026-05-30 084515" src="https://github.com/user-attachments/assets/25c22d6b-9942-4755-8190-dfdf0cc87323" />
<img width="1491" height="359" alt="Ekran görüntüsü 2026-05-30 093117" src="https://github.com/user-attachments/assets/c33177c9-45cc-498f-b029-271607e2c598" />
<img width="1366" height="203" alt="Ekran görüntüsü 2026-05-30 093126" src="https://github.com/user-attachments/assets/8b51231c-9334-4aa1-b55f-9e1ed196ac48" />
<img width="1493" height="233" alt="Ekran görüntüsü 2026-05-30 093106" src="https://github.com/user-attachments/assets/0e51af3b-416f-4197-82c2-839ba749a160" />

