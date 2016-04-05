package com.takezoux2.vactor.aggregation

import com.takezoux2.vactor.{VoiceActor, AnimeElem}

/**
  * Created by takezoux2 on 2016/04/05.
  */
case class RankingMaker(filter: AnimeElem => Boolean,sortBy: RankingSortBy.Value) extends Function1[List[VoiceActor],CSV]{
  override def apply(vas: List[VoiceActor]): CSV = {

    val sortByFunc : ((String,Int,Int,Double)) => Double = sortBy match{
      case RankingSortBy.TotalCastNumber => (t : (String,Int,Int,Double)) => -t._2
      case RankingSortBy.MainCastNumber => (t : (String,Int,Int,Double) ) => -t._3
      case RankingSortBy.MainCastRate => (t : (String,Int,Int,Double) ) => -t._4
    }

    CSV(
      List("声優","出演数","メイン","メイン率"),
      vas.map(va => {
        val filtered = va.tvAnime.filter(filter)
        val total = filtered.size
        val main = filtered.count(_.isMain)
        (va.name,total,main,main.toDouble / total)
      }).filter(_._2> 0).sortBy(sortByFunc).map(t => {
        List(t._1,t._2.toString,t._3.toString,t._4.toString)
      }))
  }
}

object RankingSortBy extends Enumeration{

  val TotalCastNumber,
  MainCastNumber,
  MainCastRate = Value
}
