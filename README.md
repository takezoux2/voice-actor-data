# 声優集計プログラム

Wikiのページを解析して、声優の情報を集計するプログラムです。
Wikiはスクレイピング禁止なので、ローカルにダンプデータを持ってきて集計します。



# 実行手順

そのうち自動化しますが、今は手作業でお願いします。

### 1:データベースの準備

※検証はMySQLで行いました

適当な名前でデータベースを作成し、
db-def/tables.sql にあるSQLを実行してテーブルを作成して下さい。
なお、最新版は 
https://phabricator.wikimedia.org/diffusion/MW/browse/master/maintenance/tables.sql からダウンロード可能です。



### 2:各種データのダウンロード

[こちら](https://dumps.wikimedia.org/jawiki/latest/)から最新のダンプデータをダウンロード出来ます。

[Wikiの説明](https://ja.wikipedia.org/wiki/Wikipedia:%E3%83%87%E3%83%BC%E3%82%BF%E3%83%99%E3%83%BC%E3%82%B9%E3%83%80%E3%82%A6%E3%83%B3%E3%83%AD%E3%83%BC%E3%83%89)を参考に、

* [pages-articles.xml.bz2](https://dumps.wikimedia.org/jawiki/latest/jawiki-latest-pages-articles.xml.bz2)
* [categorylinks.sql.gz](https://dumps.wikimedia.org/jawiki/latest/jawiki-latest-categorylinks.sql.gz)

をダウンロードして、データベースへデータを挿入して下さい。

* page
* revision
* text
* categorylinks

のテーブルにデータが入っていればOKです。


### 3:DB接続の設定

db.properties.sampleをdb.propertiesの名前でコピーし、DBの接続情報を設定して下さい。


### 4:実行

    sbt run
    
を実行すると、2015年の女性声優の出演数ランキングと、全女性声優の出演数ランキングがoutput/ディレクトリに出力されます。



# 注意事項

* メインキャラクターの判定は、Wikipedia上で太文字になっているかどうかで判定しています。







