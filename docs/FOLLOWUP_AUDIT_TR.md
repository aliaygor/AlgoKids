# Son isteklerin durumu — 4 Ekim 2026

Güncel kaynak debug olarak derlendi; release Kotlin ve manifest kontrolleri geçti. Kullanıcının isteğiyle yeni AAB oluşturulmadı. Play Console ve üretim yayını kullanıcıya ait; çevrimiçi beyanlar buradan değiştirilmedi.

## Tamamlananlar

- Son ses/harf/sayı denetimi: genel Türkçe ses yerine belirli anlatıcı seçimi ve kalıcı, örnek dinlenebilir ses seçenekleri eklendi. 29 harfin adı/küçük biçimi ve 0–100 iki dilli sayı yazımları bağımsız içerik testinden geçti. Harf açıklamaları örnek/ünlü-ünsüz bilgisiyle geliştirildi; İngilizce birleşik sayılar tireli yazıldı. Güncel 32 birim testi başarılı, lint 0 hata / 59 uyarı. Ayrıntılar FINAL_RELEASE_CHECK_TR.md; özel stüdyo kayıtları hâlâ üretilmedi.

- Ek reklam isteği: Gerçek ödüllü ve banner kimlikleri bağlandı. Ödüllü reklam ayrı görünüm ekranında onayla yalnızca renk temasını açar; öğrenme puanı/kilitleri etkilemez. Ebeveyn ekranında tek banner bulunur, oyunlara eklenmedi. Tam ekran formatları ortak sıklık sınırını kullanır. AdditionalAdsTest 31.9 saniyede geçti: açık onayı iptal etmek ödül vermedi; Google test reklamı tamamlanınca tema açıldı, öğrenme puanları değişmedi, banner yüklendi ve çıkışta kaldırıldı. Sonuç latest-additional-ads.txt içinde; debug, release Kotlin, 30 birim testi ve lint başarılı (0 hata, 58 uyarı). Canlı reklam tıklanmadı; release kimlikleri oluşturulan BuildConfig içinde doğrulandı.

- Son düzeltme: Atölyeler ekranında onaylı Sıfırla düğmesi iki dilin bütün eğitim ilerlemesini, puanları, temel alıştırmaları, rotaları, kart kontrol noktalarını ve günlük bonusları temizler; ses/yaş/bildirim ayarları korunur. Tamamlanan setlerin etiketi “Tamamlandı”; açık ama bitmemiş set “Açık” olarak ayrıldı. Temel alıştırmalarda bitiş özeti ve kalıcı tamamlanma işareti var; set geçişi için puanlı atölyelerin de tamamlanması gerektiği açıklanır. Yeni ProgressResetTest (18.6 saniye) sıfırlama → temel oyun bitişi → set açma → yeniden oluşturma → tekrar sıfırlama akışında geçti. Son debug derleme, 30 birim testi ve lint de başarılı. Aşağıdaki iki uzun tur bu son UI düzeltmesinden önceki koşulardır; yeni bitiş penceresi tam tur testine uyarlandı.

- API 36 hedefi, sürüm 2.0 / kod 7. Play'deki en yüksek kod ayrıca kontrol edilmeli.
- 225 eski kayıt incelendi; 23 temel düzey bırakıldı, 202 tekrar kullanım dışı. 21 farklı düşünme atölyesi / 63 görev, 10 çözülebilir rota, yeni ana sayfa ve açıklamalar mevcut.
- Rokette başlangıç karesi hareket sayılmaz; ilk çarpışmada program durur. Yol ve komut sayıları ortak motorla kontrol edilir.
- Eğitim yolu: Görsel → Dikkat → Hafıza → Dinleme → Sayılar → Alfabe → Geometri → Sayısal Mantık → Mantık → Algoritma. Setin bütün puanlı atölyeleri tamamlanınca sonraki açılır. Temel alıştırmalar isteğe bağlı pratiktir.
- Kilit hatası giderildi: son doğru yanıt hemen tamamlanmayı kaydeder; eski tamamlanma kayıtları da kilidi açar. Eski kayıtta puan yoksa puan uydurulmaz. TR/EN ilerlemeleri ayrıdır.
- Görev ilk değerlendirmesi 100; her yanlış kontrol −25, taban 25. Hata ve puan kalıcıdır; tekrar oynamak ilk sonucu silmez.
- Yerel yaş tercihine göre günlük soru; doğru yanıta günlük bir kez +20 keşif puanı. Bonus kategori açmaz; çözüm ücretsiz.
- Hatırlatma ilk kullanımda otomatik iki günde bir. Ayrı ebeveyn onayı yok; Android bildirim izni gerekiyor. Son ziyaretten sonra yaklaşık 18.00, sessiz tek bildirim; ayarlardan günlük/iki günlük/kapalı seçilebilir. Kilitli kategori bildirimin üzerinden açılmaz.
- 10 akıcı, iki dilli hikâye; 6–15 sayfa, dil başına 105 sayfa. Seçili dilde kısadan uzuna sıralı; otomatik sayfa atlama yok.
- TR harf ve sayı adları düzeltildi; İngilizce görevler ve öğrenme kartları ayrı içerik kullanır.
- AdMob gerçek app/unit kimlikleri ayarlandı. Debug yalnızca Google test reklamı kullanır. İlk oturum reklam yok; sonraki oturumlarda en az iki farklı atölye / üç dakika, oturumda en fazla iki geçiş reklamı. Banner veya çözümü reklamla kilitleme yok.
- app-ads.txt verilen Firebase adresinde HTTP 200 ve doğru yayıncı satırıyla doğrulandı. AdMob'un kendi doğrulama sonucu ve Play geliştirici web sitesi alanı kullanıcı tarafından kontrol edilmeli.
- Rakip ilk 20 araştırması ve trafik planı hazır. Rakipler cihazda kurulup oynanmadı; kampanya gönderilmedi, para harcanmadı. Google sıralama garantisi veya ölçülmüş zekâ artışı iddiası yok.

## Güncel doğrulama

- 30 birim testi geçti. Son Gradle koşusu: testDebugUnitTest, lintDebug, compileReleaseKotlin, processReleaseManifest, assembleDebugAndroidTest başarılı. Lint hata yok; uyarılar mevcut.
- LearningPathFlowTest geçti: son yanıt sonrası kilit açma, yanlışla puan düşmesi ve yeniden açılışta günlük bonusun çoğalmaması.
- AdIntegrationTest geçti: Google test reklamı yükleme/açma/kapatma, dönüş ve puanların korunması.
- ReminderIntegrationTest iki test geçti: iki günlük otomatik başlangıç, manuel kapatmanın korunması, TR/EN bildirim, tekrar göndermeme ve kapatma. Android'in asenkron bildirim kaldırmasını beklemeyen ilk test düzeltildi; eski başarısız kayıt latest-services-recovery.txt içinde tarihsel olarak korunuyor.
- Gerçek bildirim panelinde bildirime dokunuldu; Hafıza atölyesine döndüğü XML ve ekran görüntüsüyle doğrulandı.
- NarrationIntegrationTest, WorkshopRecoveryTest (iki kontrol) ve EnglishStoryTest geçti. Her iki dilde bütün 105 hikâye sayfası ve kurtarma/rotasyon akışları kontrol edildi; ses sentezi altı örnek üretti. Bu bütün telaffuzların insan dinlemesiyle onayı değildir.
- Güncel tam oyun turları iki dilde geçti: Türkçe 194 saniye, İngilizce 191 saniye. Her tur 21 atölye / 63 görev, 23 temel düzey, 10 rota ve tüm öğrenme kartlarını dolaştı. Sonuçlar latest-full-tr.txt / latest-full-en.txt içinde. İngilizce ilk denemede uzun listede henüz oluşturulmamış öğeyi doğrudan arayan test seçicisi başarısız oldu; listeyi kaydırarak arama düzeltildikten sonra tam tur geçti. Üretim kodu bu test düzeltmesiyle değişmedi.

## Açık kalanlar

- Profesyonel, cihazdan bağımsız stüdyo hikâye sesleri henüz üretilmedi. Daha doğal hız/normal perde ve mevcut en kaliteli uygun cihaz sesi seçimi uygulandı; çevrimiçi ses hatasında çevrimdışı sese dönüş var. Lisanslı kayıtları metin karmasıyla bağlayan altyapı ve 210 sayfalık dışa aktarım hazır. Bu, her telefonda aynı profesyonel ses sağlandığı anlamına gelmez. NATURAL_NARRATION_TR.md ayrıntıları içerir.
- Android 16 gerçek cihaz, tablet, büyük yazı ve uzun süreli 24/48 saat fiziksel bekleme ayrıca dahili testte kontrol edilmeli. Güncel emülatör API 37; API 36 cihaz testi yapıldı denemez.
- Play Console Ads: Yes, Veri Güvenliği, gizlilik politikası ve mağazadaki reklamsız ifadeleri güncellenmeli. Kaynak taslakları değiştirildi; canlı Console/site politikası değiştirilmedi.
- AdMob doğrulaması, canlı reklam sunumu ve üretim yayını tamamlandı denemez. AAB kullanıcı tarafından oluşturulacak.

[Reklam kurulumu](ADMOB_SETUP_TR.md) · [Doğal ses](NATURAL_NARRATION_TR.md) · [Trafik planı](GROWTH_PLAN_TR.md) · [Rakip ilk 20](TOP20_AND_PRODUCT_DIRECTION_TR.md)
