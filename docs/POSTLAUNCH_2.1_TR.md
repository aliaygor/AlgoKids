# Yayın sonrası düzeltmeler — 10 Ekim 2026

## Uygulanan değişiklikler

- Devam et: son ziyaret tamamlanmışsa eğitim yolundaki ilk açık, bitmemiş sete geçer. Bütün setler tamamlandıysa düğme gösterilmez. Dil değiştirme kendi ilerlemesini kullanır.
- İlerleme: değerlendirme puanından ayrı tamamlanma çubuğu. Temel atölye bir atölye olarak hesaba katılır; örneğin Görsel Algı'da üç etkinlikten biri tamamlanınca 33/100. Görevlerin tamamlanması ara ilerleme de verir. Eski tamamlandı kayıtları korunur. Sonraki seti açmak için puanlı atölyeler yeterlidir; temel etkinlikler serbest pratik olarak kalır.
- 63 görevde adım adım örnek çözüm. Grid oyunlarında seçilen kareler sırayla gösterilir. Dikkat oyunlarında her kartın Seç/Geç kararı açık yazılır. Katman, mozaik ve dikkat oyunlarında ek kullanım açıklaması vardır.
- Otomatik gösterim günde 5; TR/EN ortak, cihazda kalıcı. Gün değişince yenilenir. Ödüllü reklamın yalnızca SDK ödül callback'i 1 ek gösterim verir. Reklam yoksa ücretsiz manuel örnek adımları kullanılabilir. Örnek ve otomatik gösterim değerlendirme puanı veya tamamlanma yazmaz; çocuk kendi yanıtını kontrol eder.
- Gün batımı görünümü ana ekran, kategori ve düşünme oyunlarının arka plan, kart ve düğmelerine uygulanır. Tema tercihi canlı yenilenir ve yeniden açılışta korunur.
- Dört renkli kare/sarı temel korunarak logo köşeleri, boşlukları ve renkleri düzenlendi. Ana ekran logosu, adaptif ikon ve eski Android ikonları aynı aileye taşındı.
- Anlatıcı deneme metinleri iki dilde kısaltıldı; normal konuşma hızı, kısa doğru yanıt sesi. Dinleme görevlerinin eğitim için gerekli yönergeleri kesilmez.
- Alt bölümde ebeveynler için Google Play değerlendirme bağlantısı; ebeveyn ekranında isteğe bağlı görünüm reklamına bağlantı.
- Tam ekran reklamlar ortak üst sınır 3/oturum. İlk oturumda yok; geçiş reklamı için iki farklı atölye, ilk reklamdan önce ve reklamlar arasında en az üç dakika gerekir. Banner oyunlarda yok. Debug yalnızca Google test birimlerini kullanır.

## Çocuk uygulaması reklam sınırı

Google Play Families, oyunu kesen reklamların (ödüllü dahil) 5 saniye sonra kapatılabilir olmasını ister. Uzun, kapatılamayan çözüm reklamı zorunluluğu eklenmedi. Geri dönmek/iptal etmek öğrenmeyi kilitlemez. Kaynak: https://support.google.com/googleplay/android-developer/answer/9893335?hl=en

## Play Console

Yayındaki sürüm 7 (2.0), hedef SDK 36, yüzde 100 dağıtım olarak doğrudan Console'da görüldü. Yeni kaynak 8 (2.1) olarak hazırlandı.

Türkçe ve İngilizce mağaza simgeleri güncellendi ve 10 Ekim'de Google incelemesine gönderildi. Gönderim anındaki durum: İncelenmekte olan değişiklikler / otomatik kontroller. Bu mağaza görseli gönderimidir; uygulama kodu yayınlanmadı. Kanıt: publishing/store-2026-10-10/console-icon-review.jpg. 512px ikon ve iki dilli sürüm notları aynı klasördedir.

## Doğrulama

İlk Türkçe cihaz turu: 21 atölye / 63 görevde her örnek adımın açılması, temel oyunlar, rotalar, öğrenme kartları; LearningPathFlowTest ve iki Google test reklamı testi dahil dört test geçti. Log: latest-postlaunch-tr.txt. Bu tur son otomatik hak özelliğinden önce koşuldu.

Son kaynakta 34 birim testi, debug ve release Kotlin derlemesi, lint kontrolü geçti. İngilizce tam atölye turu (21 atölye / 63 görev) ve anlatıcı seçimi testi geçti. İlk İngilizce toplu koşudaki kalan üç testin sorunları giderildi: test dil seçimi ve bildirim izni düzeltildi; Google test reklamı sunucusunun HTTP 500 hataları için sınırlı yeniden yükleme eklendi. Son odaklı cihaz koşusunda SolutionSupportTest, AdditionalAdsTest ve AdIntegrationTest üçü de geçti (0 başarısızlık, BUILD SUCCESSFUL). Loglar: latest-postlaunch-en.txt ve latest-postlaunch-final.txt. Son koşu günlük beş otomatik gösterimi, yeniden açılışta hakkın korunmasını, ücretsiz örnekleri, kahverengi arka planı, test reklamı ödülünü ve reklamdan uygulamaya dönüşü doğruladı.

İlk emülatör oturumunda ekran görüntüleri grafik motoru nedeniyle siyahtı; bunlar görsel QA kanıtı sayılmadı. Son görsel kontrol çalışan ANGLE grafik motoruyla yapıldı. Geçerli kanıtlar: screenshots/postlaunch/sunset-home.png, solution-support.png, ad-close-5s.png ve home-en.png.

Üretim reklamlarına tıklanmadı. Google test reklamı doğrulaması canlı reklam doluluk oranını, her gerçek yaratıcı içeriğin 5 saniyede kapatılmasını veya her cihazdaki görünümü garanti etmez. Canlı AdMob yaratıcıları ve gerçek telefon dahili testte ayrıca kontrol edilmeli. Yeni kodun kullanıcıya ulaşması için mevcut upload key ile imzalanmış 8 (2.1) AAB yüklenmelidir; mevcut app/release/app-release.aab bu değişikliklerden önceki dosyadır.

## Devam noktası

Kod ve son doğrulama sonuçları kaydedildi. Yeni sürüm henüz yayınlanmadı. Sonraki yayın adımı mevcut upload key ile 8 (2.1) AAB oluşturmak, gerçek cihaz/dahili test kontrolünü yapmak ve Play Console üzerinden yüklemektir. Bilgisayar uykuya geçtiğinde yerel çalışma devam etmez; bu sohbet ve kayıtlı dosyalardan devam edilebilir.
