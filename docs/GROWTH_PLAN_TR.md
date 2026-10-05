# AlgoKids — trafik ve etkileşim planı

## Önce nedeni ölç

Şu anda Play Console verileri bu çalışma ortamına bağlı değil. Trafik düşüklüğünün nedeni hakkında kesin teşhis yok. Aşağıdaki plan, test edilecek önerilerden oluşur.

Son 28 günü önceki 28 günle karşılaştır: mağaza ziyaretçisi, mağaza edinimleri/dönüşüm oranı, ülke/dil ve trafik kaynağı, aktif kullanıcı, elde tutma, Android Vitals çökme/ANR. Play Console'un edinim raporu trafik kaynakları ve dönüşümü ayırır ([resmî yardım](https://support.google.com/googleplay/android-developer/answer/9859173)).

| Gözlem | Öncelikli deneme | Başarı göstergesi |
|---|---|---|
| Mağaza ziyaretçisi az | Türkçe başlık/açıklama, doğru kategori ve yetişkinlere yönelik tanıtım içeriği | Organik ziyaret ve kaynak bazlı ziyaret artışı |
| Ziyaret var, yükleme az | İlk 3 ekran görüntüsünü oyun ve kazanım odaklı değiştir | Aynı kaynaktaki dönüşüm oranı |
| Yükleme var, geri dönüş az | İlk görevin açıklığı, zorluk dengesi ve hata geri bildirimi | Elde tutma ve aktif kullanıcı eğilimi |
| Çökme/ANR artıyor | Önce hatayı ve cihaz uyumunu düzelt | Android Vitals'ta iyileşme |

Sayısal hedefleri mevcut baz değer geldikten sonra belirle. Verisiz “%X büyüme” hedefi veya indirme garantisi kullanma. Çocuklar üzerinde davranış izleme SDK'sı bu sürüme eklenmedi; uygulama içi atölye tamamlama hunisi için ileride ayrı ölçüm tasarımı gerekir.

## Mağaza metni taslağı

Başlık önerisi: **AlgoKids: Mantık Oyunları**

Kısa açıklama: **Çocuklar için rota, döngü, mantık ve hafıza atölyeleri. Planla, dene, keşfet.**

Uzun açıklama başlangıcı:

> AlgoKids, çocukların bir çözümü planlamasını, denemesini ve sonucunu incelemesini sağlayan oyun atölyeleri sunar. Rokete yol çiz, tekrar eden adımları döngülerle grupla, hatalı komutu bul ve duruma göre robotun komutlarını değiştir.
>
> Her doğru çözümün ardından kuralın nasıl çalıştığını öğren. Sayı makinelerinde işlem sırasını keşfet; hafıza atölyelerinde gizlenen konumları hatırla veya sırayı tersine çevir. Türkçe harfleri ve 0–100 sayılarını yazılı kartlarla ve cihazındaki uygun ses paketiyle çalış.
>
> Hesap gerekmez. İlerleme cihazında saklanır. Türkçe ve İngilizce içerikler sunar; Türkçe alfabe, hece ve okunuş atölyeleri Türkçe öğrenimini destekler. Tamamlanan atölyelerden sonra seyrek reklam gösterilebilir; çözüm açıklamaları ücretsizdir.

“Zekâyı artırır”, “bilimsel olarak kanıtlı” gibi ölçülmemiş sonuç iddiaları kullanma. Hedef yaş aralığını ailelerle kullanılabilirlik denemesi yaptıktan sonra ilan et. Temel oyunlar ile çok koşul / çarpma içeren ileri görevler farklı hazır bulunuşluk düzeyleri gerektirir.

## Ekran görüntülerinin anlatısı

Gerçek 2.0 uygulama ekranlarını kullan:

1. Ana sayfa: “Her gün yeni bir düşünme görevi”.
2. Rota oyunu: “Planla, adımlarını izle, düzelt”.
3. Döngü / hata ayıklama: “Tekrarlayan adımları keşfet”.
4. Koşul makinesi: “Birden çok kuralı birlikte düşün”.
5. Hafıza: “Sırayı hatırla, tersine kur”.
6. Ebeveyn özeti: “İlerleme cihazında saklanır”.

İlk önce metin veya ilk görsel gibi tek bir değişkeni dene. Trafik azsa eşzamanlı çok sayıda A/B deneyi sonuç vermeyebilir. Play'in deney ekranı gereken örneklem ve süreyi tahmin eder; sonuç yeterli güvene ulaşmadan kazanan ilan etme ([resmî deney rehberi](https://support.google.com/googleplay/android-developer/answer/12053285)).

## 30 günlük uygulanabilir çalışma

- **1. hafta:** 2.0'ı dahili testte 5–10 aileye denet; özellikle yönerge anlaşılması, ilk görev süresi, bırakılan yer ve telaffuz geri bildirimi iste. Play baz metriklerini kaydet. Bu plan katılımcılara mesaj göndermez.
- **2. hafta:** İmzalı sürümü üretime çıkar; mağaza metnini ve gerçek ekran görüntülerini güncelle. API gerekliliği ve ön lansman raporunu kontrol et.
- **3. hafta:** Yetişkinlere yönelik 3 kısa demo videosu hazırla: “Roketin hatası nerede?”, “İki kuralı aynı anda uygula”, “Aynı sırayı tersine kur”. Her videoda gerçek oyunu ve ebeveynin sorabileceği tek bir soruyu göster. Kanalları trafik kaynağına göre izlenebilir bağlantılarla ayır.
- **4. hafta:** Edinim ve geri dönüş eğilimlerini değerlendir. En çok bırakılan görevleri gerçek aile geri bildirimiyle düzelt. Yeterli trafik varsa ilk ekran görüntüsü veya kısa açıklama için tek bir mağaza deneyi aç.

Ücretli kullanıcı edinimini baz metrik ve ilk görev deneyimi netleşince değerlendir; bütçe ve sonuç hedefi kullanıcı tarafından belirlensin. Şu an ücretli kampanya, dışarıya mesaj veya mağaza yayını başlatılmadı.

Çocukları hedefleyen uygulamalarda yaş grupları ve Families beyanları gerçek içerikle uyuşmalıdır ([Google Play hedef kitle rehberi](https://support.google.com/googleplay/android-developer/answer/9867159)).
