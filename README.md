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

1. **Repo Klonlama:** `git clone https://github.com/zaferKarakurt/Piton-Maintenance-Tracker.git`
2. **Firebase:** Kendi Firebase projenizi oluşturun, `google-services.json` dosyasını `app/` dizinine ekleyin.
3. **Test Hesapları:**
   - Admin: `admin@test.com` / `123456`
   - Personel: `personel@test.com` / `123456`


<img width="384" height="859" alt="Ekran görüntüsü 2026-05-30 083955" src="https://github.com/user-attachments/assets/49e55b77-8436-426d-b805-1591e73ab391" /><img width="386" height="859" alt="Ekran görüntüsü 2026-05-30 084017" src="https://github.com/user-attachments/assets/02f8c277-773e-424f-945f-a7fa3fcfb0c8" />
<img width="73" height="312" alt="Ekran görüntüsü 2026-05-30 084133" src="https://github.com/user-attachments/assets/35ee0b3c-88d1-4e50-aadf-47e2442a7141" />
<img width="387" height="862" alt="Ekran görüntüsü 2026-05-30 084358" src="https://github.com/user-attachments/assets/7c1fcd43-c2db-4890-b140-d1d8fb5b8469" />
<img width="387" height="860" alt="Ekran görüntüsü 2026-05-30 084500" src="https://github.com/user-attachments/assets/77eb1920-584c-4256-8203-bdee0ed069b8" />
<img width="374" height="858" alt="Ekran görüntüsü 2026-05-30 084515" src="https://github.com/user-attachments/assets/25c22d6b-9942-4755-8190-dfdf0cc87323" />
