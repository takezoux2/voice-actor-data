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

  it should "parse collectly" in {

    val items = parser.getItems(soraAmamiya)
    items.foreach(println(_))

    assert(items.size == 33)
    assert(items.count(_.isMain) == 18)

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


    // 半角(使用
    assert(parser.splitTitleAndCharacter("""* [[この素晴らしい世界に祝福を!]] ('''アクア'''<ref>{{Cite web|publisher=アニメ『この素晴らしい世界に祝福を！』公式サイト|url=http://konosuba.com/staff_cast/|title=STAFF・CAST|accessdate=2015-10-26}}</ref>)""") ==
      ("[[この素晴らしい世界に祝福を!]]","""'''アクア'''<ref>{{Cite web|publisher=アニメ『この素晴らしい世界に祝福を！』公式サイト|url=http://konosuba.com/staff_cast/|title=STAFF・CAST|accessdate=2015-10-26}}</ref>""")
    )


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


  val soraAmamiya = """
                      |=== テレビアニメ ===
                      |'''2012年'''
                      |* [[アイカツ! (アニメ)|アイカツ!]]（中山ユナ、久世若葉、早瀬小夏）
                      |* [[新世界より (小説)|新世界より]]（美鈴）
                      |* [[となりの怪物くん]]（女生徒E）
                      |'''2013年'''
                      |* [[ガイストクラッシャー]]（緑ヒスイ<ref>{{Cite web|publisher=テレビ東京・あにてれ　ガイストクラッシャー|title=スタッフ・キャスト|url=http://www.tv-tokyo.co.jp/anime/gaist/staff/index.html|accessdate=2013-08-02}}</ref>）
                      |* [[銀河機攻隊 マジェスティックプリンス]]（ロナ）
                      |* [[銀の匙 Silver Spoon]]（女子生徒B）
                      |* [[世界でいちばん強くなりたい!]]（早瀬愛華<ref>{{Cite web|publisher=アニメイトTV|url=http://www.animate.tv/news/details.php?id=1375327133&p=2|title=10月新番『世界でいちばん強くなりたい！』より花澤香菜さんほか追加キャスト発表＆“悶絶”ビジュアルを公開！|accessdate=2013-08-01}}</ref>）
                      |* [[ジュエルペット ハッピネス]]（女生徒）
                      |* [[超次元ゲイム ネプテューヌ]]（国民B）
                      |* [[とある科学の超電磁砲|とある科学の超電磁砲S]]（女の子）
                      |* [[ログ・ホライズン]]（リリアナ<ref>{{Cite web|publisher=NHKアニメワールド ログ・ホライズン|url=http://www9.nhk.or.jp/anime/loghorizon/#characterArea|title=CHARACTER|accessdate=2013-08-05}}</ref>）
                      |'''2014年'''
                      |* [[アカメが斬る!]]（'''アカメ'''<ref>{{Cite web|publisher=TVアニメ『アカメが斬る！』公式サイト|title=STAFF&CAST|url=http://akame.tv/staff_cast.html|accessdate=2014-05-21}}</ref>）
                      |* [[アルドノア・ゼロ]]（'''アセイラム・ヴァース・アリューシア'''<ref>{{Cite web|publisher=アニメイトTV|title=【AJ2014】速報『Fate/Zero』コンビ、あおきえい＆虚淵玄が３年ぶりに挑む『アルドノア・ゼロ』のキャスト＆メインスタッフ公開！　放送は2014年7月！|url=http://www.animate.tv/news/details.php?id=1395374117|accessdate=2014-03-22}}</ref>）
                      |* [[一週間フレンズ。]]（'''藤宮香織'''<ref>{{Cite web|publisher=TVアニメ『一週間フレンズ。』公式サイト|title=キャラクター|url=http://oneweekfriends.com/character.html|accessdate=2014-03-10}}</ref>）
                      |* [[東京喰種トーキョーグール]]（'''霧嶋董香'''<ref>{{Cite web|publisher=コミックナタリー|url=http://natalie.mu/comic/news/112490|title=「東京喰種」キャスト発表、マスク被る金木のビジュアルも|accessdate=2014-03-20}}</ref>、ヘタレ<ref>{{Cite web|publisher=Twitter|url=https://twitter.com/tkg_anime/statuses/505017384256565249|title=東京喰種アニメ公式|accessdate=2014-08-28}}</ref>）
                      |* [[七つの大罪 (漫画)|七つの大罪]]（'''エリザベス'''<ref>{{Cite web|publisher=TVアニメ「七つの大罪」公式サイト|title=Character|url=http://www.7-taizai.net/character2.html|accessdate=2014-08-06}}</ref>）
                      |* [[ノブナガ・ザ・フール]]（オペB）
                      |* [[ブレイドアンドソウル]]（'''ジン・ハズキ'''<ref>{{Cite web |url=http://www.tbs.co.jp/anime/blade/staffcast/ |title=スタッフ&キャスト |work=ブレイドアンドソウル 公式ホームページ |publisher=TBSテレビ |accessdate=2014-02-07}}</ref>）
                      |* [[魔弾の王と戦姫]]（少女）
                      |* [[魔法科高校の劣等生]]（'''光井ほのか'''<ref>{{Cite web|publisher=アニメ『魔法科高校の劣等生』|title=公式サイト|url=http://mahouka.jp/staff/|accessdate=2013-12-31}}</ref>）
                      |* [[ラブライブ! (テレビアニメ)|ラブライブ! 2nd Season]]（1年生A、クラスメート）
                      |* ログ・ホライズン（オペレーター3）
                      |'''2015年'''
                      |* [[Classroom☆Crisis]]（'''白崎イリス'''<ref>{{Cite web|publisher=TVアニメ「Classroom☆Crisis」オフィシャルサイト|title=Staff|url=http://www.classroom-crisis.com/cast_staff/|accessdate=2015-05-23}}</ref>）
                      |* [[電波教師]]（'''叶美奈子'''<ref>{{Cite web|work=電波教師|publisher=[[読売テレビ]]|url=http://www.ytv.co.jp/denpa/cast/index.html|title=キャスト・スタッフ|accessdate=2015-02-18}}</ref>）
                      |* 東京喰種トーキョーグール√A（'''霧嶋董香'''<ref>{{Cite web|work=TVアニメ『東京喰種トーキョーグール』公式サイト|url=http://www.marv.jp/special/tokyoghoul/staff.html|title=第二期 Staff & Cast|accessdate=2014-12-01}}</ref>）
                      |* [[パンチライン_(アニメ)|パンチライン]]（'''成木野みかたん'''<ref>{{Cite web|publisher=TVアニメ「パンチライン」|url=http://www.punchline.jp/staffcast|title=STAFF CAST|accessdate=2015-02-20}}</ref>)
                      |* [[プラスティック・メモリーズ]]（'''アイラ'''<ref>{{Cite web|publisher=TVアニメ「プラスティック・メモリーズ」オフィシャルサイト|url=http://www.plastic-memories.jp/character|title=Character|accessdate=2015-01-30}}</ref>）
                      |* [[モンスター娘のいる日常]]（'''ミーア'''<ref>{{Cite web|work=TVアニメ『モンスター娘のいる日常』公式サイト|url=http://monmusu.tv/staffcast/|title=Cast&Staff.|accessdate=2015-04-17}}</ref>）
                      |'''2016年'''
                      |* [[この素晴らしい世界に祝福を!]] ('''アクア'''<ref>{{Cite web|publisher=アニメ『この素晴らしい世界に祝福を！』公式サイト|url=http://konosuba.com/staff_cast/|title=STAFF・CAST|accessdate=2015-10-26}}</ref>)
                      |* [[ディバインゲート]]（'''ユカリ'''<ref>{{Cite web|work=TVアニメ『ディバインゲート』公式サイト|url=http://www.marv.jp/special/divinegate/?id=staff_page|title=STAFF&CAST|accessdate=2015-09-26}}</ref>）
                      |* 七つの大罪 聖戦の予兆（'''エリザベス'''<ref>{{Cite web|work=TVアニメ「七つの大罪　聖戦の予兆」公式サイト|title=STAFF & CAST|url=http://www.7-taizai.net/staffcast/|accessdate=2016-03-29}}</ref>）
                      |* [[はいふり]]（'''知名もえか'''<ref>{{Cite web|publisher=TVアニメ「はいふり」オフィシャルサイト|url=http://www.hai-furi.com/staff/|title=すたっふ / きゃすと|accessdate=2016-03-03}}</ref>）
                      |* [[WORKING!! (WEB版)|WWW.WORKING!!]]（'''鎌倉志保'''）
                      |""".stripMargin


}
