package com.algokids.game
import com.algokids.data.LearningContent
import org.junit.Assert.*
import org.junit.Test

class FinalLearningAuditTest {
 @Test fun allTurkishLetterNamesAndCaseFormsMatchTheAlphabetReference() {
  val names="a,be,ce,çe,de,e,fe,ge,yumuşak ge,he,ı,i,je,ke,le,me,ne,o,ö,pe,re,se,şe,te,u,ü,ve,ye,ze".split(',')
  val symbols="ABCÇDEFGĞHIİJKLMNOÖPRSŞTUÜVYZ".map {it.toString()}
  assertEquals(symbols,LearningContent.turkishLetters.map {it.first})
  assertEquals(names,LearningContent.turkishLetters.map {it.second})
  val cards=LearningContent.alphabet(true).take(29)
  assertEquals(8,cards.count {it.detailTr.contains("Ünlü harf")})
  assertTrue(cards[10].detailTr.contains("Büyük: I · Küçük: ı"))
  assertTrue(cards[11].detailTr.contains("Büyük: İ · Küçük: i"))
  assertTrue(cards[8].detailTr.contains("sözcük başında kullanılmaz"))
  cards.forEachIndexed { index,item ->assertTrue(item.detailTr.startsWith("Harf adı: ${names[index]}\n"));assertTrue(item.detailTr.contains("Örnek:"))}
 }
 @Test fun everyNumberCardHasTheExpectedTurkishAndEnglishReading() {
  val trOnes="sıfır,bir,iki,üç,dört,beş,altı,yedi,sekiz,dokuz".split(',')
  val trTens="on,yirmi,otuz,kırk,elli,altmış,yetmiş,seksen,doksan".split(',')
  val enSmall="zero,one,two,three,four,five,six,seven,eight,nine,ten,eleven,twelve,thirteen,fourteen,fifteen,sixteen,seventeen,eighteen,nineteen".split(',')
  val enTens="twenty,thirty,forty,fifty,sixty,seventy,eighty,ninety".split(',')
  LearningContent.numbers().forEachIndexed { n,c ->
   val tr=when {n<10->trOnes[n];n==100->"yüz";else->trTens[n/10-1]+if(n%10==0) "" else " "+trOnes[n%10]}
   val en=when {n<20->enSmall[n];n==100->"one hundred";else->enTens[n/10-2]+if(n%10==0) "" else "-"+enSmall[n%10]}
   assertEquals(n.toString(),c.symbol);assertEquals("Okunuşu: $tr",c.detailTr);assertEquals("Read: $en",c.detailEn)
   assertEquals(tr,c.speakTr);assertEquals(en,c.speakEn)
  }
 }
}
