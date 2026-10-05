# AlgoKids

Son durum: [Son istekler ve bekleyen doğrulamalar](docs/FOLLOWUP_AUDIT_TR.md). Eğitim yolu, puan, günlük bonus, otomatik hatırlatma ve AdMob kaynakta uygulanıp debug derlemesinde kontrol edildi. Güncel test sonuçları denetim belgesinde; AAB oluşturma kullanıcıya bırakıldı.

Android / Jetpack Compose ile düşünme ve öğrenme atölyeleri.

2.0 sürümü Android 16 / API 36 hedefler. Mevcut kategorilerde temel alıştırmalar, 21 yeni düşünme atölyesi (63 görev) ve 10 bölümlü rota oyunu içerir. İlerleme ve tercihler cihazda tutulur.

- [Sürüm değişiklikleri, doğrulama ve yayın adımları](docs/RELEASE_2.0_TR.md)
- [Mağaza metni ve trafik/etkileşim planı](docs/GROWTH_PLAN_TR.md)
- [225 eski sorunun ve kategorilerin incelemesi](docs/CONTENT_AUDIT_TR.md)
- [Rakipler ve Google Play keşfedilme planı](docs/COMPETITOR_AND_DISCOVERY_TR.md)
- [Uygulanan reklam yerleşimi](docs/ADS_DECISION_TR.md)
- [Yeni ana sayfa](docs/screenshots/home.png)

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug lintDebug
.\gradlew.bat connectedDebugAndroidTest
.\gradlew.bat bundleRelease
```

Üretim yayını için mevcut upload key ile imzalanmış AAB ve Play Console'da uygun sürüm kodu gerekir. Depodaki release yapılandırması imzalama anahtarı içermez.
