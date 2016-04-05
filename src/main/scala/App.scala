import com.takezoux2.vactor.aggregation.{TotalCastNumber, CastNumberPerYear}
import com.takezoux2.vactor.{VoiceActorDataAggregator, WikiParser, WikiDBReader}

/**
  * Created by takezoux2 on 2016/04/05.
  */
object App {


  def main(args: Array[String]) : Unit = {

    val dbReader = new WikiDBReader().init()
    val parser = new WikiParser()
    val aggregator = new VoiceActorDataAggregator(parser,dbReader)


    val data = aggregator.getFemaleVoiceActorsInJapan()

    CastNumberPerYear(2015,data).save("output/female2015.csv")
    TotalCastNumber(data).save("output/female-total.csv")



  }

}
