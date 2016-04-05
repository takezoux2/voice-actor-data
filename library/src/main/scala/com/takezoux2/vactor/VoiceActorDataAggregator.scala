package com.takezoux2.vactor

/**
  * Created by takezoux2 on 2016/03/23.
  */
class VoiceActorDataAggregator(parser: WikiParser,wikiDBReader: WikiDBReader) {


  def getFemaleVoiceActorsInJapan() = {
    wikiDBReader.getActoresses().flatMap({
      case (pageId,name) => {
        getVoiceActor(name,pageId)
      }
    })

  }


  def getVoiceActor(name: String): Option[VoiceActor] = {
    wikiDBReader.getText(name).map(body => {
      val animeElems = for(section <- parser.extractTVAnimeSection(body)
      ) yield parser.getItems(section)

      VoiceActor(name,animeElems.map(_.toList).getOrElse(Nil))
    })
  }
  def getVoiceActor(name: String,pageId: Int): Option[VoiceActor] = {
    wikiDBReader.getText(pageId).map(body => {
      val animeElems = for(section <- parser.extractTVAnimeSection(body)
      ) yield parser.getItems(section)

      VoiceActor(name,animeElems.map(_.toList).getOrElse(Nil))
    })
  }




}
