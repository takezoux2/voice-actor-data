package com.takezoux2.vactor.aggregation

import com.takezoux2.vactor.VoiceActor

/**
  * Created by takezoux2 on 2016/04/05.
  */
object TotalCastNumber extends Function1[List[VoiceActor],CSV] {


  override def apply(vas: List[VoiceActor]): CSV = {

    CSV(
      List("声優","出演数","メイン","メイン率"),
      vas.map(va => {
      val total = va.tvAnime.size
      val main = va.tvAnime.count(_.isMain)
      (va.name,total,main,main.toDouble / total)
    }).sortBy(l => -l._2).map(t => {
      List(t._1,t._2.toString,t._3.toString,t._4.toString)
    }))
  }
}


