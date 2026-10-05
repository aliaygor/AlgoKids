# AlgoKids 2.0 — yayın ve doğrulama

**Güncel durum:** [Son isteklerin denetimi](FOLLOWUP_AUDIT_TR.md). Güncel kaynak debug olarak derlendi; eğitim yolu ve Google test reklamı cihaz kontrolleri geçti. Önceki turlar ile yeni koşuların ayrıntısı denetim belgesindedir. Bu çalışmada yeni AAB oluşturulmadı; kullanıcı oluşturacak.

Hazırlanan yapı: `com.algokids`, `versionName=2.0`, `versionCode=7`, `compileSdk=36`, `targetSdk=36`, `minSdk=24`.

## Bu sürüm

- Mevcut 10 kategori korunuyor. Her kategoride ayrı atölye seçimi var. 225 eski soru tek tek incelendi; 23 temel düzey arayüzde kaldı, 202 tekrarlı/belirsiz düzey emekliye ayrıldı. [Ayrıntılı inceleme](CONTENT_AUDIT_TR.md).
- 21 yeni düşünme atölyesi, toplam 63 görev: döngüler, ilk hatalı komut, koşullu karar yürütme, işlem sırası, bağımlılıklar, ipucu eleme, kural değiştirme, yer hafızası, ters hafıza, sesli yönerge ve anlam çıkarma, yön döndürme, hece birleştirme ve basamak değeri.
- Rota atölyesi 10 bölümlü. Her komut bir kare ilerletir; başlangıç karesi adım sayılmaz. İpucu ve komut bütçesi çözülebilir en kısa yoldan hesaplanır. İlk çarpışmada program durur. Çalışan komut ve gidilen yol görünür.
- Ana sayfa günlük görev, kategoriye dönüş, beceri açıklamaları ve cihazdaki ilerleme özeti sunar.
- Temel oyun ilerlemesi, atölye kontrol noktaları, tamamlanan rotalar, öğrenme kartları ve dil/ses tercihleri cihazda saklanır.
- Türkçe harf adları ve 0–100 sayı okunuşları ortak, test edilebilir içerikten gelir. I/İ ayrı tutulur. Ses motorunda dil ve çevrimdışı ses uygunluğu kontrol edilir.
- Geometride yıldızın 5 ucu / 10 kenarı ayrıldı. Özel sorular kendi yönergelerini gösterir. İngilizce yönergeler içerikte tanımlıdır.
- Yeni hafıza görevinde çocuk hazır olduğunda bilgiyi gizler; gizlenmeden yanıt verilemez. Gizleme ses motorundan bağımsızdır.
- Android 16 için güvenli ekran boşlukları kullanılır. Yeni ekranlar kaydırılabilir; temel oyun alanı kısa ve yatay ekranlarda kaydırılır.

## Yayın adımları

1. Play Console'da tüm kanallardaki en yüksek sürüm kodunu kontrol et. Buradaki 7, depodaki 6'nın devamıdır; Play'de 7 veya üzeri varsa artırılmalı.
2. Android Studio → Build → Generate Signed App Bundle / APK → Android App Bundle. Mevcut uygulamanın upload key anahtarıyla imzala. Anahtar veya parolayı depoya koyma.
3. `app/build/outputs/bundle/release/app-release.aab` Gradle çıktısıdır. İmzalama yapılandırması olmadığı için bu paket imzasızdır; doğrudan Play yüklemesi için hazır değildir. Android Studio'nun imzalı çıktısını kullan.
4. Önce dahili test kanalında yükle. Aşağıdaki cihaz/dil kontrollerini uygula ve Play'in ön lansman raporunu incele.
5. Mağaza ekran görüntülerini gerçek 2.0 ekranlarıyla değiştir. Sürüm notlarını ekle.
6. Hedef kitle, içerik derecelendirmesi ve Veri Güvenliği beyanlarının gerçek davranışla eşleştiğini kontrol et. Bu sürüm AdMob SDK'sı ekler: Ads: Yes, Veri Güvenliği ve gizlilik metni buna göre güncellenmeli.
7. Üretim kanalına yayınla; uygun olduğunda kademeli dağıtım uygula. API uyarısının kapanması, üretimde yeni paketin yayınlanmasına bağlıdır; yalnızca depodaki hedef değişikliği uyarıyı kaldırmaz.

## Cihaz üzerinde kontrol

- Android 16/API 36 ve daha eski desteklenen Android; telefon, tablet, yatay ekran, büyük yazı boyutu, hareketle geri gezinme.
- Rokette 5 ok → 5 hareket → başlangıç dahil 6 ziyaret edilen kare. Kenar/engel hatası ilk hatalı komutta durmalı.
- Her rota çözülebilir; 5×5 ileri bölümler ve yatay kaydırılan komut yuvaları.
- Yanlış yanıt → çözümü düzenleme; doğru yanıt → neden açıklaması; son görev → tamamlanma özeti.
- Uygulamayı kapatıp açınca tamamlanma/tercihler korunmalı. Etkin rota programı yeniden başlar; bölüm kontrol noktası korunur. Atölye seçimleri ve hikâyenin sayfası ekran yeniden oluşturulunca korunur; uygulama tamamen kapatılırsa tamamlanmayan atölyenin görev kontrol noktasına dönülür.
- Türkçe C, Ç, Ğ, I, İ; sıfır, kırk, altmış, yetmiş, yüz. Sesin gerçek telaffuzu kullanılan Android TTS ses paketine bağlı olduğundan Türkçe ses paketi olan cihazda dinleyerek kontrol edilmeli.
- Ses kapalı veya ses paketi yokken hafıza çalışmalı. Dinleme atölyesinde yazılı yönerge alternatifi kullanılabilmeli.

## Tekrarlanabilir doğrulama

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug lintDebug
.\gradlew.bat connectedDebugAndroidTest
.\gradlew.bat bundleRelease
```

Birim testleri: rota çözümleri ve çarpışma sonrası durma; Türkçe alfabe ve sayı adları; her görevin seçenek/yanıt bütünlüğü; çoklu seçimin tüm koşulları sağlaması; ters hafıza; sayı makinesinin işlem sırası; soruya ait eşleştirme kuralları.

## Türkçe sürüm notu

AlgoKids 2.0 ile düşünme atölyelerini keşfet! Döngü kurma, hata ayıklama, koşullar, ters hafıza ve sayı makineleriyle farklı çözümler dene. Yenilenen ana sayfa ve adım adım rota animasyonu oyunları takip etmeyi kolaylaştırıyor. İlerlemen cihazında saklanıyor. Türkçe içerik düzeltmeleri ve Android 16 uyumluluğu da bu sürümde.

## Kaynaklar

- [Google Play hedef API gereklilikleri](https://support.google.com/googleplay/android-developer/answer/11926878)
- [Android 16 hedefleyen uygulamalarda davranış değişiklikleri](https://developer.android.com/about/versions/16/behavior-changes-16)
- [AndroidX Activity sürüm notları](https://developer.android.com/jetpack/androidx/releases/activity)
- [AndroidX Test sürüm notları](https://developer.android.com/jetpack/androidx/releases/test)


## Teknik doğrulama

- 21 birim testi geçti: 20 oyun/içerik testi ve mevcut örnek birim testi. Rota, çarpışma sonrası durma, Türkçe harf/sayı, işlem sırası, bağımsız ızgara koordinatları, alternatif geçerli plan ve dikkat akışı doğrulandı.
- Debug APK ve release AAB derlendi. Release manifestinde hedef API 36, sürüm kodu 7 ve sürüm adı 2.0 doğrulandı. Release AAB imzasızdır.
- Debug lint: 0 hata, 32 uyarı. Bağımlılık, mevcut kaynak/ikon, çeviri ve ekran yapılandırması uyarıları mevcut; sıfır uyarı iddiası yok.
- ARM64 ve x86_64 yerel kütüphanelerinde ELF LOAD hizalaması 16384 byte. Debug APK için zipalign -c -P 16 4 başarılı.
- API 37 / Android 17, Pixel_9 16 KB emülatöründe müfredatın tamamı üç başarılı tur oynandı: 160.580, 172.239 ve 175.065 saniye. Her tur: 21 atölyenin 63 görevi, 23 temel düzey, 10 rota, 41 alfabe/hece ve 101 sayı kartı. İkinci/üçüncü tur kayıtları docs/emulator-pass-2.txt ve docs/emulator-pass-3.txt içinde.
- Son ızgara/hikâye iyileştirmelerinden sonra aynı müfredat bir tam tur daha geçti: 135.218 saniye; kayıt docs/emulator-final-pass.txt. Toplam dört başarılı tam tur. Emülatör yeniden başladığında kesilen test başarılı sayılmadı.

Android 16/API 36 cihazında, tablet ve büyük yazı boyutunda ayrıca dahili test gerekir. Türkçe TTS paketinin gerçek telaffuzu dinleyerek doğrulanmadı. Rakipler resmî sayfalardan incelendi; kurulup oynandığı iddia edilmez. Eğitim etkisi/zekâ artışı ölçülmedi. Play Console, üretim yayını ve reklam beyanları değiştirilmedi.

Debug APK üretim sürümünün upload anahtarıyla imzalanmaz. Üretim uygulamasının üstüne güncelleme olarak kurulamayabilir; gerçek cihaz ilerlemesini silmeden test için Play dahili test kullan. Eski play-store-ready ve app/release paketleri bu çalışmada yeniden üretilmedi; güncel 2.0 çıktısı olarak kullanma.

## Ek akış kontrolleri

Son sürümde iki ek emülatör testi geçti (25.473 saniye, docs/emulator-recovery-tests.txt):

- Yanlış kare seçimi → açıklama → geri alma → doğru çözüm; ses kapalı hafıza; yatay/dikey dönüşte seçimin korunması; İngilizce döngü çözümü ve Türkçeye dönüş.
- 10 hikâyenin tüm sayfaları; hikâye/sayfa seçiminin Activity yeniden oluşturulunca korunması; yatay ekranda hikâye metninin görünmesi; hikâye bitince hikâye listesine dönme.

Testler başlangıçtaki kayıtlı dil/ses tercihinden bağımsızlaştırıldı. Yön değiştirme testinde yeniden oluşturulan güncel Activity kullanılır. Önceki başarısız veya kesilen denemeler başarı sayısına eklenmedi. Ana sayfa, kategori seçimi, döngü, dikkat, hafıza, geometri, görsel katman ve dinleme ekranları görsel olarak incelendi; gerçek görüntüler docs/screenshots içinde.

[Rakip incelemesi](COMPETITOR_AND_DISCOVERY_TR.md), [trafik planı](GROWTH_PLAN_TR.md), [reklam kararı](ADS_DECISION_TR.md). Bu sürümde AdMob geçiş reklamı var; eski reklamsız beyanları kaldırılmalı.
