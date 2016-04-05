package com.takezoux2.vactor.aggregation

import com.takezoux2.vactor.VoiceActor

/**
  * Created by takezoux2 on 2016/04/05.
  */
object CastNumberPerYear extends Function2[Int,List[VoiceActor],CSV] {


  override def apply(year: Int, vas: List[VoiceActor]): CSV = {

    CSV(
      List("声優","出演数","メイン","メイン率"),
      vas.map(va => {
      val filtered = va.tvAnime.filter(d => d.year == year)
      val total = filtered.size
      val main = filtered.count(_.isMain)
      (va.name,total,main,main.toDouble / total)
    }).filter(_._2> 0).sortBy(l => -l._3).map(t => {
      List(t._1,t._2.toString,t._3.toString,t._4.toString)
    }))


  }
}
