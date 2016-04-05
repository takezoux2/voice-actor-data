package com.takezoux2.vactor.aggregation

import java.io.{File, FileOutputStream}

/**
  * Created by takezoux2 on 2016/04/05.
  */
case class CSV(header: List[String],data: List[List[String]]) {


  def save(filename: String) = {

    val file = new File(filename)
    val dir = file.getParentFile()
    if(!dir.exists()){
      dir.mkdirs()
    }


    val stream = new FileOutputStream(filename)

    def writeLine(line: List[String]) = {
      val v = line.map(v => "\"" + v + "\"").mkString(",") + "\n"
      stream.write(v.getBytes("utf-8"))
    }

    try{
      writeLine(header)
      for(line <- data){
        writeLine(line)
      }

    }finally{
      if(stream != null){
        stream.close()
      }
    }

  }



}
