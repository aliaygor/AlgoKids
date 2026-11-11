# AlgoKids reklam denetimi — 2 Kasım 2026

## Doğrudan konsolda görülenler
- Üretim: 2.1, versionCode 8, %100 dağıtım.
- AdMob: uygulama doğrulandı, Hazır. Politika merkezinde reklam sunumunu durduran/sınırlayan sorun yok.
- 1 Kasım incelemesindeki son 28 gün: 9 istek, 3 gösterim, %100 eşleşme, yaklaşık 0,08 TL. Kullanıcı bu gelirin kendi denemesi olduğunu belirtti; gerçek kullanıcı geliri olarak değerlendirilemez.
- Son 7 gün: sıfır istek ve gösterim. Düşük talep/erişim temel bulgu; reklam bulunamaması kanıtlanmadı.
- Play son 28 gün: 31 cihaz edinimi, 11 ilk açılış, 24 aylık etkin cihaz. Edinim tek başına reklam izleme anlamına gelmez.
- API 36 politika uyarısının paket listesi eski alpha 6 (1.3) ve dahili 4 (1.2) sürümlerini gösteriyor. Bu, AdMob reklam sunum kısıtlaması değil.
- AdMob yüksek etkileşimli reklamlar açıktı; kapatıldı ve kaydedildi. Bu gelir artırma değişikliği değil; uzun kapatma akışlarını azaltma tercihidir.

## Kod değişiklikleri — 2.2 (9)
- Ana menü altında içerikten ayrılmış, etiketli tek banner. Oyun, hikâye ve görev ekranlarına banner eklenmedi.
- İlk oturumda SDK başlatılır ve uygunluk varsa banner yüklenir. İlk oturumda tam ekran reklam gösterilmez.
- Sonraki oturumlarda geçiş reklamı: tamamlanan bir farklı atölyenin bitişinden kategoriye dönüşte; oturum yaşı ve reklam arası en az 120 saniye. Tam ekran toplam sınırı üç/oturum.
- UMP güncelleme hatasından sonra tekrar uygulamaya dönüşte yeniden deneme, 30 saniye sınırı. canRequestAds kontrolü korunur.
- Yükleme hatalarının tür ve kodları cihaz günlüğünde AlgoKidsAds etiketiyle izlenebilir; kişisel veri eklenmedi.
- Debug yalnızca Google test reklam kimlikleri kullanır. Gerçek reklam tıklayarak test edilmez.

## Yorum ve sınırlar
İlk oturumda hiç başlatmama, banner'ın yalnızca ebeveyn ekranında bulunması ve geçiş reklamının iki ayrı atölye/üç dakika şartı reklam fırsatlarını azaltıyordu. Mevcut konsol verileri belirli bir cihazdaki UMP veya ağ hatasını teşhis etmeye yetmez. Gelir artışı kullanıcıların uygulamayı açmasına ve kullanmaya devam etmesine bağlıdır; garanti değildir.

Yeni kod yerelde hazırlanmıştır; Play'e yayınlanmış sayılmaz. Test çıktıları ads-audit-final-build.txt dosyasındadır.

Kaynaklar:
- https://support.google.com/googleplay/android-developer/answer/9893335?hl=en
- https://developers.google.com/admob/android/privacy

## Son doğrulama
- Son sıklık değişikliğinin birim testleri geçti.
- Menü banner cihaz testi emülatörde siyah ekranda ilerlemedi ve sonlandırıldı; başarılı sayılmadı. Fiziksel cihazda Google test reklamıyla ilk oturum banner ve oyuna geçiş kontrolü yayın öncesinde gereklidir.
- Son paket/lint/birim test derlemesi: ads-audit-package-check.txt.
