package com.takezoux2.vactor

/**
  * Created by takezoux2 on 2016/03/23.
  */
case class AnimeElem(year: Int, title:String, characters: List[CharacterElem]){
  def isMain = characters.exists(_.isMain)
}