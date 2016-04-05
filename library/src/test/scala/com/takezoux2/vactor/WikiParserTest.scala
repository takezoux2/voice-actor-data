package com.takezoux2.vactor

import org.scalatest.{Matchers, FlatSpec}

/**
  * Created by takezoux2 on 2016/03/23.
  */
class WikiParserTest extends FlatSpec with Matchers{

  val parser = new WikiParser()

  it should "extract section" in {
    val wikiText =
      """
        |
        |=== テレビアニメ ===
        |
        |* hoge(hoge)
        |* fuga(fuga)
        |
        |=== OVA ===
        |
        |* aaa(aaa)
        |
      """.stripMargin

    assert(parser.extractSection("テレビアニメ",wikiText) == Some(
      """
        |
        |* hoge(hoge)
        |* fuga(fuga)
        |
        |""".stripMargin))



  }


  it should "split title and character" in {

    // ）で終わらず、※移行の注釈付き
    assert(parser.splitTitleAndCharacter("""* [[山田くんと7人の魔女]]（'''白石うらら'''<ref name="yamajo">{{Cite web|url=http://www.yamajo-anime.com/|title=アニメ「山田くんと7人の魔女」公式サイト|accessdate=2014-11-29}}</ref>）※第5話エンドカードも担当。""") ==
      ("[[山田くんと7人の魔女]]","""'''白石うらら'''<ref name="yamajo">{{Cite web|url=http://www.yamajo-anime.com/|title=アニメ「山田くんと7人の魔女」公式サイト|accessdate=2014-11-29}}</ref>"""))

    assert(parser.splitTitleAndCharacter("""* [[森田さんは無口]]（'''森田真由'''）※コミックス第3巻の特装版に付属<ref>{{Cite web|publisher=コミックナタリー|title=佐野妙「森田さんは無口」アニメ化、森田さん役は花澤香菜|url=http://natalie.mu/comic/news/41233|accessdate=2016-1-8}}</ref> """) ==
      ("[[森田さんは無口]]","""'''森田真由'''"""))
    // 三角括弧が含まれる
    assert(parser.splitTitleAndCharacter("""* [[おじさんとマシュマロ]]（'''MIO5〈デガス〉'''<ref>{{Cite web|work=ドリームクリエイション|url=http://www.dreamcreation.co.jp/ojimasyu/|title=アニメ「おじさんとマシュマロ」公式サイト|accessdate=2015-11-13}}</ref>）""") ==
      ("[[おじさんとマシュマロ]]","""'''MIO5〈デガス〉'''<ref>{{Cite web|work=ドリームクリエイション|url=http://www.dreamcreation.co.jp/ojimasyu/|title=アニメ「おじさんとマシュマロ」公式サイト|accessdate=2015-11-13}}</ref>"""))
    // タイトルに[[]]無し
    assert(parser.splitTitleAndCharacter("""* ソードアート・オンラインII（シリカ / 綾野珪子<ref>{{Cite web|title=Calibur|publisher=ソードアート・オンライン|url=http://www.swordart-online.net/calibur/staff/|accessdate=2014-10-13}}</ref>）""") ==
      ("ソードアート・オンラインII","""シリカ / 綾野珪子<ref>{{Cite web|title=Calibur|publisher=ソードアート・オンライン|url=http://www.swordart-online.net/calibur/staff/|accessdate=2014-10-13}}</ref>"""))

    // 作品名の後ろにrefタグ
    assert(parser.splitTitleAndCharacter("""* [[艦隊これくしょん -艦これ-]]<ref>{{Cite web||publisher=アニメ「艦隊これくしょん -艦これ-」公式サイト|title=スタッフ&キャスト|url=http://kancolle-anime.jp/staffcast/|accessdate=2015-01-27}}</ref>（'''睦月'''、如月、弥生、望月、）""") ==
      ("[[艦隊これくしょん -艦これ-]]<ref>{{Cite web||publisher=アニメ「艦隊これくしょん -艦これ-」公式サイト|title=スタッフ&キャスト|url=http://kancolle-anime.jp/staffcast/|accessdate=2015-01-27}}</ref>","""'''睦月'''、如月、弥生、望月、"""))

  }

  it should "extract character" in {
    // refタグあり
    assert(parser.extractCharacters("""'''白石うらら'''<ref name="yamajo">{{Cite web|url=http://www.yamajo-anime.com/|title=アニメ「山田くんと7人の魔女」公式サイト|accessdate=2014-11-29}}</ref>""") ==
      List(CharacterElem("白石うらら",true)))
    assert(parser.extractCharacters("""'''MIO5〈デガス〉'''<ref>{{Cite web|work=ドリームクリエイション|url=http://www.dreamcreation.co.jp/ojimasyu/|title=アニメ「おじさんとマシュマロ」公式サイト|accessdate=2015-11-13}}</ref>""") ==
      List(CharacterElem("MIO5〈デガス〉",true)))

    // 複数キャラ名、区切り文字/
    assert(parser.extractCharacters("""シリカ / 綾野珪子<ref>{{Cite web|title=Calibur|publisher=ソードアート・オンライン|url=http://www.swordart-online.net/calibur/staff/|accessdate=2014-10-13}}</ref>""") ==
      List(CharacterElem("シリカ",false),CharacterElem("綾野珪子",false)))
    //区切りが、かつ、フォーマットミス
    assert(parser.extractCharacters("""'''睦月'''、如月、弥生、望月、""") ==
      List(CharacterElem("睦月",true),CharacterElem("如月",false),CharacterElem("弥生",false),CharacterElem("望月",false)))

    // キャラ名が他ページヘリンク(今は正規化していない)
    assert(parser.extractCharacters("""'''[[豊臣秀吉|豊臣ヒデヨシ]]'''〈'''日出佳乃'''〉""") ==
      List(CharacterElem("[[豊臣秀吉|豊臣ヒデヨシ]]〈日出佳乃〉",true)))

    // １キャラか２キャラか判断が難しいが、2キャラとして判断
    assert(parser.extractCharacters("""ニコ〈上月由仁子 / スカーレット・レイン〉""") ==
      List(CharacterElem("ニコ〈上月由仁子",false),CharacterElem("スカーレット・レイン〉",false)))


  }


}
