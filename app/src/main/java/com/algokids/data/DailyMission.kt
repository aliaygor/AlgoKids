package com.algokids.data

import kotlin.random.Random

data class DailyQuestion(val tr:String,val en:String,val answer:Int,val options:List<Int>,val whyTr:String,val whyEn:String)
object DailyMission {
    fun question(age:Int,day:Long):DailyQuestion {
        val random=Random(day xor age.toLong())
        val start=random.nextInt(2,6)
        val step=random.nextInt(2,4)
        val result:Int
        val tr:String;val en:String;val whyTr:String;val whyEn:String
        when(age.coerceIn(4,12)) {
            in 4..5 -> {
                result=start+step
                tr="Sepette $start elma var. $step elma daha ekledik. Şimdi kaç elma var?"
                en="There are $start apples in a basket. Add $step more. How many now?"
                whyTr="$start elmaya $step ekleriz: $start + $step = $result."
                whyEn="Add $step to $start: $start + $step = $result."
            }
            in 6..7 -> {
                result=start+3*step
                tr="Sayılar her adımda aynı miktarda artıyor: $start, ${start+step}, ${start+2*step}, ?"
                en="The numbers increase by the same amount: $start, ${start+step}, ${start+2*step}, ?"
                whyTr="Her adımda $step ekleniyor. ${start+2*step} + $step = $result."
                whyEn="Add $step each time. ${start+2*step} + $step = $result."
            }
            in 8..9 -> {
                result=(start+step)*2
                tr="Bir makine önce $step ekliyor, sonra sonucu 2 ile çarpıyor. Girdi $start ise çıktı kaç?"
                en="A machine adds $step, then multiplies the result by 2. What is the output for $start?"
                whyTr="Önce $start + $step = ${start+step}. Sonra ${start+step} × 2 = $result."
                whyEn="First $start + $step = ${start+step}. Then ${start+step} × 2 = $result."
            }
            else -> {
                result=random.nextInt(12,36)
                val output=(result+step)*3-start
                tr="Makine önce $step ekliyor, sonucu 3 ile çarpıyor, sonra $start çıkarıyor. Çıktı $output ise başlangıç sayısı kaçtı?"
                en="A machine adds $step, multiplies by 3, then subtracts $start. Its output is $output. What was the input?"
                whyTr="İşlemleri ters sırayla geri al: $output + $start = ${output+start}; ${output+start} ÷ 3 = ${result+step}; ${result+step} − $step = $result."
                whyEn="Undo in reverse: $output + $start = ${output+start}; ${output+start} ÷ 3 = ${result+step}; ${result+step} − $step = $result."
            }
        }
        val options=setOf(result,result+1,(result-1).coerceAtLeast(0),result+step+2).toList().shuffled(random)
        return DailyQuestion(tr,en,result,options,whyTr,whyEn)
    }
}
