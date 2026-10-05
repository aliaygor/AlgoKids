# 2.0 güncelleme notu

Güncel yayın kaynağı: [RELEASE_2.0_TR.md](docs/RELEASE_2.0_TR.md). Bu sürümde AdMob geçiş reklamı var; Play Console Ads: Yes olmalı. TTS telaffuzu cihaz ses paketine bağlıdır.

# AlgoKids Google Play Hazırlık Notları

## Teknik durum

- Paket adı: `com.algokids`
- Sürüm: `versionCode 7`, `versionName 2.0`
- Minimum SDK: 24
- Target SDK: 36
- Google Mobile Ads 25.5.0 ve UMP 4.0.0 var; çocuklara yönelik, kişiselleştirmesiz, G içerik sınırı uygulanır.
- In-app purchase yok.
- Hesap, giriş, üyelik yok.
- INTERNET ve ACCESS_NETWORK_STATE var. Kamera, mikrofon, konum, rehber ve AD_ID izni yok.
- İsteğe bağlı yerel hatırlatıcı için POST_NOTIFICATIONS ve yeniden başlatmada zamanlama için RECEIVE_BOOT_COMPLETED var. Hatırlatıcı varsayılan iki günde bir; ayrıca ebeveyn onayı ekranı yok. Android bildirim izni gerekir; ayarlardan sıklık değiştirilebilir veya kapatılabilir.
- Uygulama yedeği kapalı: `android:allowBackup="false"`
- Release AAB üretilebilir; Play'e yüklemek için upload key ile imzalanmış AAB gerekir.

## Play Console cevap önerileri

- App category: Educational / Education
- Target audience: Gerçek içerik ve aile denemesiyle yeniden belirlenmeli; ileri görevler okul öncesi için uygun olmayabilir.
- Ads: Yes
- In-app purchases: No
- User generated content: No
- News app: No
- Government app: No
- COVID/contact tracing: No
- Data safety: AdMob bulunduğundan “veri toplanmaz/paylaşılmaz” beyanını kullanma. SDK açıklamasına göre IP tabanlı yaklaşık konum, reklam etkileşimleri, tanılama ve kullanılan tanımlayıcı kategorilerini gerçek yapılandırmayla değerlendir: https://developers.google.com/admob/android/privacy/play-data-disclosure . AD_ID kaldırılmış olması tüm SDK veri aktarımını kaldırmaz.
- Data encrypted in transit: AdMob HTTPS kullanır; tüm kullanılan hizmetlerin davranışıyla doğrula.
- Account deletion: Not applicable, app has no accounts.

## Gizlilik politikası taslağı

Başlık: AlgoKids Privacy Policy

AlgoKids çocuklara yönelik eğitici oyun ve hikaye uygulamasıdır. Uygulama hesap oluşturma, giriş yapma veya uygulama içi satın alma gerektirmez. Atölye tamamlandıktan sonra seyrek AdMob reklamı, ebeveyn ekranında banner ve ayrı görünüm ekranında isteğe bağlı renk teması için ödüllü reklam gösterebilir. Oyunlar çevrimdışı çalışır; reklamlar ve cihazın çevrimiçi anlatıcı sesi internet kullanabilir.

Google AdMob reklam sunma, güvenlik ve performans amacıyla cihazdan bilgi alabilir; örneğin IP adresi ve reklam etkileşimleri. Kişiselleştirmesiz reklam ve çocuklara yönelik işlem bayrakları uygulanır. Google gizlilik politikası: https://policies.google.com/privacy . Çevrimiçi cihaz ses motoru hikâye metnini kendi hizmetine gönderebilir; internet yokken uygun kurulu ses kullanılır. Kamera, mikrofon, konum, rehber veya dosya erişimi istemez. Oyun ilerlemesi, ilk değerlendirme puanları, tercihler, isteğe bağlı yaş seçimi ve hatırlatıcı zamanları yalnızca cihaz içinde tutulur. Doğum tarihi veya isim istenmez. Yaş tercihi ebeveyn ayarlarından silinebilir. Uygulama kaldırıldığında yerel bilgiler silinir. İsteğe bağlı yerel bildirim için Android bildirim izni istenir; hatırlatıcılar ayarlardan kapatılabilir.

Sorular için geliştirici ile Google Play geliştirici hesabında belirtilen iletişim adresinden bağlantı kurulabilir.

## Store listing - TR

Title:
AlgoKids

Short description:
Çocuklar için eğitici mantık, dikkat ve algoritma oyunları.

Full description:
AlgoKids çocuklara yönelik düşünme atölyeleri sunar. Temel alıştırmalar ve ileri görevler farklı hazır bulunuşluk düzeyleri gerektirir.

Çocuklar görsel algı, sayma, dikkat, hafıza, geometri, işitsel algı ve algoritmik düşünme oyunlarıyla temel becerilerini eğlenceli şekilde geliştirir.

Uygulamada:
- Döngü, hata ayıklama ve karar verme atölyeleri
- Sayma oyunları
- Örüntü tamamlama
- Gölge ve şekil bulma
- Hafıza oyunları
- Sesli yönerge ve anlam çıkarma oyunları
- Robotu hedefe götürme algoritma oyunu
- Çocuklara uygun kısa hikayeler

AlgoKids çocuklar için sade tutulmuştur. Üyelik, sosyal özellik, sohbet veya uygulama içi satın alma içermez. Atölye bitişlerinde seyrek reklam gösterilebilir.

Özellikler:
- Çocuk dostu büyük butonlar
- Türkçe ve İngilizce arayüz
- Sesli yönlendirme
- Skor ve hata takibi
- Çevrimdışı oyunlar; reklam ve çevrimiçi anlatım için internet
- Öğrenme çözümleri ücretsiz

AlgoKids, çocukların dikkat, problem çözme ve temel algoritmik düşünme becerilerini güvenli bir ortamda desteklemek için tasarlanmıştır.

What's new:
Yeni algoritma oyunu, daha fazla oyun içeriği, skor takibi ve çocuk dostu arayüz iyileştirmeleri eklendi.

## Store listing - EN

Title:
AlgoKids

Short description:
Educational logic, attention, and algorithm games for kids.

Full description:
AlgoKids offers thinking workshops for children, with foundation exercises and advanced missions for different readiness levels.

Children can practice visual perception, counting, attention, memory, geometry, listening, and early algorithmic thinking through playful activities.

Includes:
- Loop, debugging and decision workshops
- Counting games
- Pattern completion
- Shadow and shape games
- Memory games
- Listening games
- A robot path algorithm game
- Short child-friendly stories

AlgoKids is designed to stay simple and child-friendly. It has no accounts, social features, chat, or in-app purchases. Occasional ads may appear after a completed workshop.

Features:
- Large child-friendly controls
- Turkish and English interface
- Voice guidance
- Score and mistake tracking
- Games work offline; ads and online narration use internet
- Learning solutions remain free

AlgoKids helps children build attention, problem-solving, and early algorithmic thinking skills in a safe environment.

What's new:
Added a new algorithm game, more learning activities, score tracking, and child-friendly interface improvements.

