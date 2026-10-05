# AlgoKids 2.0 — kategori ve oyun incelemesi

İnceleme tarihi: 4 Ekim 2026. Depodaki 225 eski sorunun kimliği, yönergesi, türü, cevabı ve arayüzde kalma kararı [CSV dosyasında](CONTENT_AUDIT_225.csv) kayıtlıdır. Resim değişmesi ayrı oyun sayılmadı. Eski kaynak dosyası korunuyor; arayüz sadece Curriculum.kt içindeki 23 temel düzeyi açıyor. 202 eski düzey arayüzden emekliye ayrıldı.

| Kategori | Eski sorun | Yeni çözüm süreçleri | Temel düzey |
|---|---|---|---:|
| Algoritma | Resimli seçimler komut yürütme gerektirmiyordu | 10 gerçek rota; döngü sıkıştırma; ilk hata; koşullu karar | 10 rota |
| Sayısal mantık | 30 kez aynı sayma; n20/n21 içerik tekrarı | İşlem zinciri; terazi ile bilinmeyen | 1–10 sayma |
| Mantık | Çoğunlukla farklı ikonlarla örüntü | Bağımlılık planlama; birden çok ipucuyla eleme | 3 açık periyot |
| Dikkat | 30 kez farklı olan nesneyi bulma | Hedef olmayan kartta tepkiyi durdurma; akış içinde kural değiştirme | Yok |
| Hafıza | Tek nesne, aynı cevap seçimi | Gizlenen 3×3 konumlar; tekrar içeren ters sıra | Yok |
| Dinleme | Yazılı ses taklidi ve cevabı ele veren ipucu | Konuşulan yönergeyi eyleme çevirme; konuşmadan anlam çıkarma | Yok |
| Geometri | Sayma ve görsel eşleme başka kategoriyle çakışıyor | Zihinsel döndürme; yatay/dikey yansıma | 4 şekil özelliği |
| Görsel algı | Aynı nesne/işlev eşleme ve belirsiz büyüklük | Katman birleşimi; modelde eksik parçayı bulma | 6 siluet |
| Alfabe | Harf/hece keşfi | Türkçe alfabetik sıra; hece sentezi | 29 harf + 12 hece |
| Sayılar | Sayı adı keşfi | Okunuştan rakama; onluk/birlik ayrımı | 0–100 |

21 atölyenin her biri 3 görev içerir: toplam 63 görev. İlk görev öğrenmeye giriş, sonraki görevler daha fazla kural/öğe/dönüş içerir. Bazı okunuş ve hece görevlerinde ilerleme farklı örneklerle pratiktir; her son görevin ölçülmüş daha zor olduğunu iddia etmiyoruz.

Geometri ve görsel algı aynı ızgara bileşenini kullanır ancak çözüm farklıdır: geometri konumu dönüştürür, görsel algı parçaları birleştirir veya çıkarır. Sayısal mantık işlem yapar; Sayılar kategori­si sayı adını ve basamak değerini çalıştırır. Mantıkta alternatif geçerli planlar kabul edilir; tek bir ezber sıraya zorlanmaz. Dinleme hafıza sırası kopyalatmaz: aradaki konum, tekrar ve ortak anlam ilişkileri kullanır.

Çocuk zekâsının arttığına ilişkin ölçüm yapılmadı. İçerik belirli düşünme süreçlerine pratik sunar. Eğitim uzmanı ve aile denemesiyle yaş/hazır bulunuşluk, yönerge anlaşılması ve aktarım ayrıca değerlendirilmelidir. Eski “5 yaş ve altı” mağaza konumlandırması çarpma/bölme ve çok koşullu yeni görevleri doğru temsil etmeyebilir.

Türkçe içerik: C=ce, Ç=çe, Ğ=yumuşak ge, I=ı, İ=i; 0=sıfır, 40=kırk, 60=altmış, 70=yetmiş, 100=yüz. Yazı ve TTS aynı içerikten beslenir. Gerçek ses, cihazdaki TTS paketine bağlıdır; yazı kontrolü dinleyerek telaffuz kontrolünün yerine geçmez.

Doğrulama: çözüm bütünlüğü ve bağımsız matematik/ızgara/plan testleri; emülatörde tüm atölye görevleri, temel düzeyler, rotalar ve öğrenme kartları üzerinden uçtan uca akış. Güncel sonuçlar [yayın raporunda](RELEASE_2.0_TR.md).
