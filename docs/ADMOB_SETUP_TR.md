# AlgoKids AdMob bağlantısı

Gerçek uygulama kimliği: `ca-app-pub-5287725227601079~3957967359`

Geçiş reklamı: `ca-app-pub-5287725227601079/5540754175`

Ödüllü reklam: `ca-app-pub-5287725227601079/8304406472`

Ebeveyn banner'ı: `ca-app-pub-5287725227601079/7121429156`

Yeni formatlar da yerel `ads.properties` içinde bağlandı. Release için gerçek kimlik olmadan ilgili format kapalı kalır; debug her formatta Google test kimliklerini kullanır. Ödüllü reklam ayrı Görünüm seçenekleri ekranında açık onayla yalnızca Gün batımı renk temasını açar. Ödül sadece SDK'nın kazanıldı geri çağrısından sonra kalıcıdır. Banner yalnızca ebeveyn ekranının en altında, tek ve Reklam etiketiyle yer alır; ekrandan çıkınca yok edilir. İlk oturumda hiçbir format yüklenmez. Geçiş ve ödüllü reklamlar ortak oturum başına iki gösterim / üç dakika aralık sınırını kullanır; banner bu tam ekran sayacına dahil değildir.

Google test reklamlarıyla AdditionalAdsTest geçti: ödül ve banner yüklemesi, onayı iptal etme ve öğrenme puanlarının korunması. Ardından geçiş reklamı + ilerleme sıfırlama testleri de geçti (`latest-ad-and-reset.txt`). Canlı reklamlar ve AdMob uygulama doğrulaması ayrı konsol kontrolleridir.

Yerel `ads.properties` bu kimlikleri içerir ve `ADS_LIVE=true` olarak ayarlandı. Git'e dahil edilmez. Proje başka bilgisayara taşınırsa `ads.properties.example` kopyalanıp kimlikler yeniden girilir. Debug **her zaman Google test app/unit kimliklerini kullanır**; gerçek reklam tıklayarak test yapılmaz.

## Yerleşim

- İlk oturumda reklam gösterilmez.
- Sonraki oturumlarda en az iki farklı tam atölye ve üç dakika sonrası, tamamlanma ekranından atölyelere dönerken tek geçiş reklamı.
- Gösterimler arasında en az üç dakika; oturumda en fazla iki reklam.
- Tekrar aynı atölyeyi bitirmek reklam sayacını artırmaz.
- Soru, yanlış cevap, ipucu, hafıza gösterimi, hikâye ve rota animasyonu sırasında reklam yok. Banner oyunlarda yok; yalnızca ayrı ebeveyn ekranında bulunur. Çözüm veya set açmak reklam izlemeye bağlı değil.
- Yükleme/gösterim hatasında oyun beklemeden devam eder. Canlı sürümde UMP reklam isteğine izin vermiyorsa SDK başlatılmaz ve reklam yüklenmez. Debug yalnızca Google test reklamını yükleyebilir.

Google Mobile Ads 25.5.0, UMP 4.0.0. Bütün isteklerde `AgeRestrictedTreatment.CHILD`, G içerik derecesi, kişiselleştirme kapalı, `npa=1`, first-party ID kapalı. Manifest birleşiminden AD_ID ve AdServices kimlik/konu/attribution izinleri çıkarıldı. Mediation SDK'sı eklenmedi. İsteğe bağlı yaş seçimi bu çocuk korumasını kaldırmaz.

## AdMob’da yapılacaklar

1. Uygulamayı doğru Play mağaza kaydı/paket adı `com.algokids` ile bağla ve çocuklara yönelik içerik ayarını doğrula.
2. G içerik sınırı ve çocuklara uygun reklamları doğrula. **High-engagement ads** seçeneğini kapat; Families kapanma kurallarına uygun reklam formatlarını kullan. Gerçek reklamın kapatılmasını test et.
3. Gizlilik ve mesajlaşma yapılandırmasını tamamla. Kod UMP'yi under-age-of-consent olarak işaretler; bu durumda çocuklardan yetişkin reklam rızası istenmez. Ağ/konfigürasyon hataları reklamı atlatır.
4. `publishing/app-ads.txt` dosyasını Play'deki geliştirici sitesinin **köküne** yükle: `https://SITE/app-ads.txt`. APK içine koymak doğrulama sağlamaz. Başka uygulamalar için mevcut satırları silmeden bu satırı ekle:

```
google.com, pub-5287725227601079, DIRECT, f08c47fec0942fa0
```

5. URL'nin giriş istemeden HTTP 200 ile metin döndürdüğünü kontrol et. Play'deki geliştirici web sitesi bu alan adına ait olmalı. AdMob'da yeniden kontrol başlat; [Google](https://support.google.com/admob/answer/9776740) tarama/mağaza değişikliklerinin algılanmasının 24 saate kadar sürebileceğini belirtiyor.
6. Play Console Ads=Yes; mağaza görsellerinden/metninden reklamsız iddiasını kaldır. Veri Güvenliği/Gizlilik, AdMob SDK'sının gerçek cihaz ve reklam verisi işleyişine göre güncellenmeli; eski “veri toplanmıyor” beyanı bu reklamlı sürüme kopyalanmamalı.

Kullanıcı dosyayı `https://algokids-e643e.web.app/app-ads.txt` adresinde yayımladı. 4 Ekim 2026 kontrolünde HTTP 200 ve doğru yayıncı satırı doğrulandı. Play geliştirici web sitesi `https://algokids-e643e.web.app/` olmalı; bu Console alanı ve AdMob doğrulama sonucu buradan doğrulanmadı. AdMob'da yeniden kontrol başlat.

Kaynaklar: [SDK](https://developers.google.com/admob/android/quick-start), [çocuk ayarı](https://developers.google.com/admob/android/targeting), [UMP](https://developers.google.com/admob/android/privacy/gdpr), [Families](https://support.google.com/googleplay/android-developer/answer/9893335).
