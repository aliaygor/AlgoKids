# Hikâye sesi

Son kontrol: genel `tr-TR-language` sesinin, cihazdaki belirli anlatıcılardan önce seçildiği bulundu. Seçim düzeltildi; varsayılan Türkçe `tr-tr-x-cfs` ailesi. Ebeveyn ekranında kurulu anlatıcılar örnek dinlenip seçilebilir; seçim dile özel korunur. Bu emülatörde beş Türkçe anlatıcı var. Gerçek yeni WAV örnekleri `narration-samples/final` içinde. Bu değişiklik stüdyo kaydı veya her telefonda aynı ses garantisi değildir. Son denetim: [AAB öncesi kontrol](FINAL_RELEASE_CHECK_TR.md).

Eski hikâye sesi %82 hız ve yükseltilmiş perde kullanıyordu. Doğal hız/perdeye alındı. Hikâyeler internet varsa aynı dildeki en yüksek kaliteli mevcut motor sesini seçebilir; hata halinde çevrimdışı sese döner. Oyun yönergeleri çevrimdışı çalışmayı korur. Çocuğun kişisel bilgileri seslendirmeye verilmez; çevrimiçi cihaz motoru yalnızca yazılmış hikâye metnini işler.

Bu ayarlar **her cihazda aynı stüdyo anlatıcısını sağlamaz**. Daha kaliteli bir ses paketi bulunmayan cihaz hâlâ mevcut motoru kullanır. Kalıcı profesyonel çözüm: 105 Türkçe + 105 İngilizce sayfayı bir kez lisanslı doğal sesle üretip uygulamaya MP3 olarak koymak. Çocukların cihazlarında bulut hesabı veya API anahtarı gerekmez; hikâyeler sonra çevrimdışı çalınır.

`StoryNarrator` hazır kaydı önce çalar. Kayıt metninin SHA-256 değeri ekrandaki sayfayla eşleşmiyorsa eski/yanlış kaydı kullanmaz; sayfa/dil değişiminde, sesi kapatınca ve çıkınca oynatmayı durdurur. Kayıt yokken mevcut motor kullanılır ve hata açıkça gösterilir. Sayfayı tekrar dinleme düğmesi eklendi.

## Kayıt paketi

```
python tools/narration_pack.py --export docs/narration-pages.json
python tools/narration_pack.py --import-from PATH_TO_RECORDINGS --voice-credit "Actor/provider + commercial licence"
```

Dosyalar `tr/s1_1.mp3`, `en/s1_1.mp3` gibi adlandırılır. Tam paket yoksa içe aktarma başarısız olur; `--partial` yalnızca örnek dinleme için kullanılabilir. Metin ve kayıtların gerçekten aynı olduğu insan dinlemesiyle ayrıca doğrulanır; dosya adı/checksum tek başına telaffuz denetimi değildir.

Bu çalışmada özel ses kaydı **üretilmedi**: bulut hesabı/kayıt dosyaları yok. Google Cloud'un [300 doları](https://cloud.google.com/signup-faqs) yeni müşteri deneme kredisidir; kayıt ücreti değildir. Kartla kimlik doğrulaması isteyebilir. Hesap/billing açılmadı, ücretli hizmet kullanılmadı. [Chirp 3 HD](https://docs.cloud.google.com/text-to-speech/docs/chirp3-hd) Türkçeyi destekleyen bir kayıt üretim seçeneğidir; ses seçimi örnek dinlemeyle yapılmalı.
