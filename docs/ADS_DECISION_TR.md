# Uygulanan reklam kararı — 4 Ekim 2026

Kullanıcının onayı üzerine Google Mobile Ads 25.5.0 ve UMP 4.0.0 entegre edildi. Gerçek kimlikler yerel ads.properties dosyasında; debug daima Google test kimliklerini kullanır. Google test reklamının yüklenmesi, açılması, kapanması ve ilerlemeyi değiştirmeden oyuna dönüşü cihazda geçti (latest-ad-test.txt). Canlı reklam tıklanmadı.

Reklam yalnızca tamamlanan atölyenin bitiş düğmesinden kategoriye dönüşte gösterilebilir. İlk oturumda gösterilmez. Sonraki oturumda en az iki farklı atölye tamamlanmalı, oturum başlayalı ve önceki reklamdan itibaren en az üç dakika geçmeli. Oturum başına en fazla iki reklam var. Reklam hazır değilse dönüş hemen gerçekleşir.

Son ekleme: Banner yalnızca ebeveyn ekranının altında tek ve etiketli yerleşim olarak var. Ayrı Görünüm seçenekleri ekranında isteğe bağlı ödüllü reklam Gün batımı renk temasını açar; standart tema ücretsizdir. Ödül SDK'nın kazanıldı geri çağrısında verilir. Geçiş ve ödüllü toplamda oturum başına en fazla iki tam ekran reklam, aralarında üç dakika sınırını paylaşır. Yanlış cevap, ipucu, çözüm, kategori açma veya başarı puanı reklamla kilitlenmez. Hikâyeler ve görevlerin içi reklamla kesilmez. Bu sıklık ürün tercihidir.

Çocuklara yönelik işlem, kişiselleştirmesiz reklam ve G içerik sınırı uygulanır; AD_ID ve AdServices reklam izinleri kaldırıldı. Release reklam isteği UMP uygunluğu sonrası yapılır. AdMob ayarlarında yüksek etkileşimli reklamları kapat; çocuklara uygun kapatma davranışını ve gerçek reklam içeriğini yayın öncesi kontrol et. [Families politikası](https://support.google.com/googleplay/android-developer/answer/9893335).

Play Console Ads: **Yes** olmalı. Eski “reklamsız” beyanları geçersizdir; güncel taslak PLAY_STORE_READINESS.md içinde. Console'a erişilmedi; çevrimiçi beyanları kullanıcı güncelleyecek. Reklam trafik yaratmaz; edinim çalışması GROWTH_PLAN_TR.md içinde.

https://algokids-e643e.web.app/app-ads.txt HTTP 200 ile doğrulandı; yayıncı satırı verilen AdMob kimliğiyle eşleşiyor. Play geliştirici web sitesi aynı alan adına işaret etmeli. AdMob doğrulama sonucu bu dosya kontrolünden ayrıdır; konsoldan yeniden kontrol başlatılmalı. [Kurulum adımları](ADMOB_SETUP_TR.md).
