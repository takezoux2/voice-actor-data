package com.takezoux2.vactor

/**
  * Created by takezoux2 on 2016/03/23.
  */
class WikiParser {



  val sections = List("テレビアニメ","OVA",
    "劇場アニメ","Webアニメ","ゲーム",
    "吹き替え","特撮","ドラマCD",
    "ラジオ","インターネットテレビ","ラジオドラマ","デジタルコミック","スマートフォンアプリ")

  def extractTVAnimeSection(text: String) = extractSection("テレビアニメ",text)

  def extractSection(sectionName:String, text: String) : Option[String] = {
    val sectionRegex = s"""(?s)=== (${sectionName}) ===(.*?)===""".r

    sectionRegex.findFirstMatchIn(text).map(m => m.group(2))

  }

  def getItems(sectionBody : String) : List[AnimeElem] = {
    var concatString = ""
    val yearRegex = "'''(\\d+)年'''".r
    val titleRegex = """\[\[(.*?)(\|.*?)?]]""".r
    val onlyTitleRegex = """\* (.*?)（""".r
    sectionBody.lines.foldLeft((0,List[AnimeElem]()))({
      case ((currentYear,list),_line) => {
        val line = if(concatString.length > 0) {
          val l = concatString
          concatString = ""
          l + _line
        }else _line

        yearRegex.findFirstMatchIn(line) match{
          case Some(m) => {
            val year = m.group(1).toInt
            (year,list)
          }
          case None if isAnimeLine(line) => {
            if(isCompleteLine(line)){
              val (_title,characterBody) = splitTitleAndCharacter(line)
              val title = titleRegex.findFirstMatchIn(_title) match{
                case Some(m) => {
                  if(m.group(2)!=null) m.group(2).drop(1)
                  else m.group(1)
                }
                case None => _title
              }
              val characters = extractCharacters(characterBody)
              (currentYear,AnimeElem(currentYear,title,characters) :: list)
            }else{
              println("Not complete line:" + line)
              concatString = line
              (currentYear,list)
            }
          }
          case _ =>{
            //println("Not anime line:" + line)
            (currentYear,list)
          }
        }
      }
    })._2
  }

  def isAnimeLine(line: String) = line.startsWith("*")
  def isCompleteLine(line: String) : Boolean = {
    if(line.endsWith("）"))return true
    val refOpen = """\<ref""".r.findAllMatchIn(line).size
    val refClose = """\</ref>""".r.findAllMatchIn(line).size
    val refClose2 = """/>""".r.findAllMatchIn(line).size
    refOpen == refClose + refClose2
  }
  def splitTitleAndCharacter(_line: String) : (String,String) = {
    val line = {
      val l1 = if(_line.contains("）※")) _line.substring(0,_line.lastIndexOf("※"))
      else _line
      val s = if(l1.startsWith("*")) l1.drop(2).trim else l1.trim
      if(s.endsWith("）")) s.dropRight(1) else s
    }
    var index = line.length - 1
    var closeCount = 1
    for(i <- line.length - 1 to 0 by -1){
      line(i) match{
        case '（' => {
          closeCount -= 1
          if(closeCount == 0){
            return (line.substring(0,i),line.substring(i+1,line.length))
          }
        }
        case '）' => {
          closeCount += 1
        }
        case _ =>
      }
    }
    (line,"")


  }

  def extractCharacters(characterPart: String) = {

    val refRegex = """<ref.*?</ref>""".r
    val refSlashRegex = """<ref.*?/>""".r
    val rem = refSlashRegex.replaceAllIn(refRegex.replaceAllIn(characterPart,""),"")

    val names = rem.split("/|、").map(_.trim)

    names.map(name => {
      if(name.contains("'''")){
        CharacterElem(name.replace("'''",""),true)
      }else{
        CharacterElem(name,false)
      }
    }).toList


  }
}
