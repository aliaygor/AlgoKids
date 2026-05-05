# Algo Kids - Play Store Handoff

Bu dosya telefondan veya baska bir cihazdan devam ederken gerekli bilgileri hizli bulmak icin hazirlandi.

## Uygulama Bilgileri

- Uygulama adi: Algo Kids
- Paket adi: `com.algokids`
- Tur: Oyun
- Hedef kitle: Cocuklar / egitici oyun

## Hazir Dosyalar

Play Store icin hazir kopyalar:

- AAB surum dosyasi: `play-store-ready/AlgoKids-release.aab`
- Paket dogrulama APK dosyasi: `play-store-ready/AlgoKids-verification.apk`

Orijinal build ciktilari:

- AAB: `app/build/outputs/bundle/release/app-release.aab`
- APK: `app/build/outputs/apk/release/app-release.apk`

## Paket Dogrulama Ekrani

Google Play Console'da `Android gelistirici dogrulamasi` ekraninda APK isterse:

1. Ekranda asagidaki SHA-256 fingerprint gorunuyorsa `play-store-ready/AlgoKids-verification.apk` dosyasini yukle.
2. Bu APK yalnizca paket sahipligi dogrulamasi icindir; normal uygulama surumu olarak kullanilmaz.

Paket dogrulama APK SHA-256:

`44:64:55:C9:E6:4E:9B:0A:3D:15:D8:4D:95:F3:DB:A9:13:72:06:FB:5C:CD:45:7D:AE:43:CA:EC:BF:0F:EA:AB`

Normal release/upload key SHA-256:

`9C:2C:E5:1B:11:F2:55:55:EE:A9:A7:8F:5A:53:FD:01:CC:32:A8:11:F1:F8:D3:32:37:8A:B5:10:BC:D0:FF:00`

Ekranda baska bir fingerprint gorunuyorsa bu APK yuklenmemeli; once hangi anahtar istendigini tekrar kontrol etmek gerekir.

## Normal Surum Yukleme

Uygulama surumu olustururken AAB dosyasi yuklenir:

`play-store-ready/AlgoKids-release.aab`

## Play Console Secimleri

- Uygulama mi oyun mu?: Oyun
- Ucretli mi ucretsiz mi?: Ucretsiz
- Reklam var mi?: Hayir
- Uygulama ici satin alma var mi?: Hayir
- Kullanici verisi topluyor mu?: Hayir
- Hesap olusturma/giris var mi?: Hayir
- Internet zorunlu mu?: Hayir
- Cocuklara yonelik mi?: Evet, cocuklara yonelik egitici icerik

## Dikkat

`algokids-upload-key.jks` dosyasi upload key dosyasidir. Kaybolursa ileride ayni uygulamaya guncelleme yayinlamak zorlasir veya imkansiz hale gelebilir. Bu dosya GitHub'a yuklenmemelidir; guvenli bir yerde yedeklenmelidir.
