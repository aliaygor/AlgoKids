# Son uçtan uca kontrol — 4 Ekim 2026

Kullanıcının son isteğiyle aynı kaynak baştan sona tekrar kontrol edildi. Bu koşuda ses iyileştirmesi yapıldı iddia edilmez: cihaz motorundaki alternatifler kullanıcı tarafından yeterince doğal bulunmadı. Profesyonel doğal anlatım beklentisi açık kalıyor; lisanslı gerçek/doğal kayıt paketi gerekir. Ses için proje yeniden taranmadı.

## Build / yayın yapılandırması

- assembleDebug, assembleDebugAndroidTest, testDebugUnitTest, lintDebug, compileReleaseKotlin ve processReleaseManifest başarılı.
- assembleRelease ve lintRelease başarılı. Kontrol amaçlı unsigned release APK paketlendi; yeni AAB oluşturulmadı.
- 32 birim testi, 0 başarısızlık. Debug ve release lint: 0 hata / 59 uyarı.
- Paket com.algokids; minimum API 24, target/compile API 36, sürüm 2.0 / kod 7.
- Gerçek AdMob uygulama, geçiş, ödüllü ve banner kimlikleri release yapılandırmasında etkin; debug Google test kimliklerini kullanır.
- Release APK zipalign -c -P 16 4 kontrolü başarılı. APK'daki dört ABI'nin libandroidx.graphics.path.so PT_LOAD segmentleri 16384 bayt hizalı; 64 bit yerel kütüphane kontrolü geçti.
- Yayın paketleme kontrolü, imzalı AAB / Play ön lansman / üretim yayını yerine geçmez. Play'deki en yüksek sürüm kodu ayrıca bilinmiyor; 7 daha önce kullanıldıysa artırılmalı.

## Genel mantık kontrolü

- Kategoriler Görsel → Dikkat → Hafıza → Dinleme → Sayılar → Alfabe → Geometri → Sayısal Mantık → Mantık → Algoritma sırasıyla açılır; bütün önceki puanlı atölyeler gerekli. Temel alıştırmalar isteğe bağlı pratiktir.
- Son doğru yanıt tamamlanmayı kaydeder; ilk değerlendirme puanı korunur. Her yanlış ilk görev puanını 25 düşürür, taban 25. Tekrar çözmek geçmiş puanı yükseltmez.
- Günlük bonus dil/yaş değiştirilerek çoğaltılmaz; kategori kilitlerini açmaz. Çözüm açıklaması ücretsizdir.
- Sıfırlama iki dilde eğitim ilerlemesini temizler ve yalnız ilk kategoriyi açık bırakır; ses/yaş/bildirim ayarlarını korur.
- Rotalar çözülebilir; başlangıç kare hareket değildir, her komut bir kare ve ilk çarpışmada durur.
- Bildirimden kilitli kategori açılamaz; dil, izin, sıklık ve kapatma akışları kontrol edilir.
- Reklam ödülü yalnız görünüm içindir; öğrenme puanı/kilitleri değiştirmez. Geçiş ve ödüllü reklam ortak sıklık sınırını kullanır, banner ebeveyn ekranıyla sınırlıdır.

## Yeni cihaz koşuları

API 37 emülatörde, aynı debug APK ile sırayla çalıştırıldı; API 36 gerçek cihaz testi yapıldı denemez.

- Türkçe bütün atölye/temel oyun/rota/öğrenme kartları: pre-aab-full-tr.txt.
- İngilizce aynı tam tur: pre-aab-full-en.txt.
- Hikâyeler, dönüş/rotasyon, bildirim, puan/kilit/günlük bonus, Google test ödüllü/banner/geçiş reklamları ve sıfırlama: pre-aab-end-to-end.txt.

Tamamlanan koşuların sayıları ve süreleri aşağıya eklenir. Canlı reklam tıklanmaz; Google test reklamı kullanılır. Ağ hatası/Google test kreatifinin kapanma süresi gibi sınırlamalar olursa ayrıca belirtilir.

Son servis grubu **7 test / 109.4 saniye / 0 başarısızlık** ile tamamlandı (pre-aab-services-retry.txt): İngilizce hikâyeler, iki bildirim testi, öğrenme yolu ve günlük bonus, ödüllü/banner, geçiş reklamı ve ilerleme sıfırlama. Başarılı yeni cihaz koşuları toplam **11 test**, birim testleri **32 test**. Türkçe ve İngilizce tam turların her biri 21 atölye / 63 görev, 23 temel seviye, 10 rota ve dilin bütün öğrenme kartlarını kapsar. Hikâye testleri iki dilde 105'er sayfayı kapsar. Ses oynatma akışının çalışması, anlatımın kullanıcı tarafından doğal bulunması anlamına gelmez; ses kalitesi eksikliği devam ediyor.

Türkçe tam tur 191.6 saniyede, İngilizce tam tur 173.4 saniyede geçti. Toplu son grupta hikâye ekranı dönüş adımı ilerlemedi; ana Activity RESUMED durumunda ve dumpsys yanıt verirken sistemde ANR yoktu. İlerlemeyen test oturumu tarafımızdan force-stop ile sonlandırıldı; pre-aab-end-to-end.txt içindeki “Process crashed” bu kontrollü sonlandırmanın sonucudur, kendiliğinden uygulama çökmesi olarak yorumlanmamalı. Hikâye/rotasyon grubu ayrı oturumda, süre sınırıyla tekrar koşuldu: **iki test 39.2 saniyede geçti** (pre-aab-recovery-retry.txt). Bu arada üretim kodu değiştirilmedi. Kalan koşular pre-aab-services-retry.txt içinde.
