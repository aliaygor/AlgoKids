# AAB öncesi son kontrol — 4 Ekim 2026

## Ses değişikliği

Cihazın ses listesi gerçekten incelendi. Eski seçim tr-TR-language genel sesi idi; aynı cihazda beş ayrı Türkçe anlatıcı varyantı bulunuyor. Genel ses, belirli anlatıcılar varken öncelik almayacak şekilde seçim düzeltildi. Varsayılan Türkçe aile tr-tr-x-cfs; oyunlarda kurulu çevrimdışı varyant, hikâyelerde bağlantı varsa aynı ailenin ağ varyantı tercih edilir. Ağ hatasında çevrimdışı dönüş korunur. Motor bu aileyi sunmazsa aynı dilde uygun diğer sese dönüş var; yanlış dil kullanılmaz.

Ebeveyn ayarlarında gerçek kurulu anlatıcılar örnek dinlenip seçilebilir. Seçim dile özel kaydedilir, ekran/uygulama yeniden oluşturulduğunda korunur. Bu cihazda beş Türkçe seçenek var; başka cihazlarda sayı ve kalite motorun sunduğu paketlere bağlıdır. Profesyonel insan/stüdyo kaydı üretilmedi. Kalitenin beğenildiği veya bütün telaffuzların insan tarafından dinlendiği iddia edilmez.

Gerçek sentez örnekleri: narration-samples/final/TR_voice_1.wav ... TR_voice_5.wav. Varsayılan yeni ses örneği TR_voice_2.wav; alfabe TR_0.wav, sayılar TR_1.wav, hikâye TR_2.wav. Rapor dosyasında gerçek ses kimlikleri var. Bunlar denetim örnekleri; uygulamaya dağıtılacak kayıt paketi olarak eklenmedi.

## Alfabe ve sayılar

- Türkçe 29 harfin sırası/adı tek tek denetlendi: a, be, ce, çe, de, e, fe, ge, yumuşak ge, he, ı, i, je, ke, le, me, ne, o, ö, pe, re, se, şe, te, u, ü, ve, ye, ze.
- TDK'nin tablosundaki I/İ sırasıyla ilgili dipnot da kontrol edildi; yaygın yerleşik sıra önce I sonra İ. Harf kartlarında büyük/küçük biçim, 8 ünlü / 21 ünsüz ve örnek sözcük var. Ğ için sözcük başında kullanılmadığı belirtilir.
- Türkçe 0–100 yazılı okunuşlarının tamamı kontrol edildi. sıfır, kırk, altmış, yetmiş, yüz doğru; birleşik sayılar ayrı yazılır.
- İngilizce 26 harfin büyük/küçük biçimleri ve 0–100 sayı yazımları kontrol edildi. twenty-one, sixty-four gibi birleşik sayılar standart tireli yazıma alındı. Oyunlarda ilgili açıklama/yanıt seçenekleri aynı içerikten gelir.
- Harf adı ile kelimedeki harf sesi farklı kavramlardır; kartlarda “Harf adı” ifadesi açık tutulur. Yazı doğruluğu, her motorun telaffuz doğruluğunu garanti etmez.

Kaynaklar: [TDK alfabe](https://yazim.tdk.gov.tr/content/01-ses-harf-ve-alfabe.html), [TDK sayıların yazılışı](https://tdk.gov.tr/icerik/yazim-kurallari/sayilarin-yazilisi/).

## Doğrulama

- Debug ve release Kotlin derlendi. AAB oluşturulmadı; kullanıcı oluşturacak.
- 32 birim testi, 0 başarısızlık. Lint hata yok; mevcut uyarılar sürüyor.
- VoiceChoiceTest ve NarrationIntegrationTest geçti: gerçek ses seçimi, kaydın korunması, doğru dil ve boş olmayan WAV sentezi.
- Son iki dilli tam oyun turları geçti: Türkçe 172.8 saniye, İngilizce 169.7 saniye. Her tur bütün 21 atölye / 63 görev, temel oyunlar, 10 rota ve öğrenme kartlarını dolaştı. Son kaynakla yeni temel alıştırma bitiş ekranı da doğrulandı. Kayıtlar final-full-tr.txt ve final-full-en.txt içinde.
- Son altı kurtarma/hikâye/bildirim/sıfırlama testi 77 saniyede geçti: her iki dilde bütün 105 hikâye sayfası, döndürme/geri dönüş, bildirim dil/sıklık/kapama ve iki dilde sıfırlama → kategori açma → tekrar sıfırlama. Kayıt final-recovery-services.txt içinde.
- Test emülatörü API 37. API 36 gerçek cihaz / tablet / büyük yazı / Play ön lansman raporu ayrıca yayın hazırlığıdır; yapıldı denemez.
- Özel ses kaydı, canlı AdMob sunumu, Console beyanları ve üretim yayını otomatik olarak tamamlanmış sayılmaz. Son reklam formatları önceki Google test reklamı koşularında doğrulandı.
