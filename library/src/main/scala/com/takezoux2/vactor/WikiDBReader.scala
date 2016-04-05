package com.takezoux2.vactor

import java.util.Properties

import scalikejdbc._

/**
  * Created by takezoux2 on 2016/03/23.
  */
class WikiDBReader {


  def init() : WikiDBReader = {
    init("db.properties")
  }
  def init(propPath: String) : WikiDBReader = {

    val prop = loadProp(propPath)

    val db = prop.getProperty("db")
    val username = prop.getProperty("username")
    val password = prop.getProperty("password")

    Class.forName("com.mysql.jdbc.Driver")
    ConnectionPool.singleton(db,
      username, password)

    this
  }

  def loadProp(propPath: String) = {

    val prop = new Properties()
    val stream = getClass.getClassLoader().getResourceAsStream(propPath)

    try{
      prop.load(stream)
    }finally{
      if(stream != null) {
        stream.close()
      }
    }

    prop
  }


  val excludeActorNames = Set(
    "八武崎碧" // 悠木碧の旧名
  )

  def getActoresses() = getActors("日本の女性声優")
  def getMenActors() = getActors("日本の男性声優")

  /**
    * return pageID and actorName list
    */
  def getActors(categoryTitle: String) : List[(Int,String)] = {
    implicit val session = AutoSession
    sql"""
    select cl_from,cl_sortkey from categorylinks where cl_to=${categoryTitle};
    """.map(r => {
      val pageId = r.int(1)
      val key = new String(r.bytes(2),"utf-8")
      val lines = key.lines.toList

      if(lines.size >= 2) (pageId,lines(1))
      else (-1,"")
    }).list.apply.filter(p => p._1 > 0 && !excludeActorNames.contains(p._2))
  }


  /**
    * Get page text by page id
    * @param pageId
    * @return
    */
  def getText(pageId: Int) : Option[String] = {
    implicit val session = AutoSession
    checkRedirect(sql"""
  select old_text from text where old_id = (
    select rev_text_id from revision where rev_page = ${pageId}
  )
    """.map(r => new String(r.bytes(1),"utf-8")).headOption.apply)
  }

  /**
    * Get page text by actor name.
    * @param actorName
    * @return
    */
  def getText(actorName: String) : Option[String] = {
    implicit val session = AutoSession

    checkRedirect(sql"""
  select old_text from text where old_id = (
    select rev_text_id from revision where rev_page = (
      select page_id from page where page_namespace=0 and page_title=${actorName}
    )
  )""".map(r => new String(r.bytes(1),"utf-8")).headOption.apply)


  }

  /**
    * If page is redirect page, get redirect target page text.
    * @param text
    * @return
    */
  def checkRedirect(text: Option[String]) : Option[String] = {
    val redirectRegex = """\#REDIRECT \[\[(.*?)]]""".r
    text match{
      case Some(t) => redirectRegex.findFirstMatchIn(t) match{
        case Some(m) => getText(m.group(1))
        case None => Some(t)
      }
      case None => None
    }
  }




}
