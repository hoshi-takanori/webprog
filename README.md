# webprog

某社の新人研修で使用したネットワークおよび Web プログラミング入門の教材です。

参考：[講義予定](https://gist.github.com/hoshi-takanori/8a6aabb8108e702cf668) および
[ちょーかんたんなサーバー](https://gist.github.com/hoshi-takanori/33b3a060e7502f7be836)。

## chat

コマンドラインのチャットサーバーおよびクライアント。

上位互換性のある以下の 3 レベルのプロトコルを実装しています。

* レベル 1 ... READ と WRITE のみ、コマンドを 1 つ実行したら接続を切る
* レベル 2 ... USER と EXIT を追加、スレッドを利用し、接続を維持する
* レベル 3 ... STREAM を追加、サーバー側からのプッシュ送信を実装

## web.server, web.servlet, web.example

Servlet 風のインターフェイスを持つ Web サーバーエンジンおよびそのサンプル。

ToyWeb (ToyContainer) として公開していたものを手直ししました。

## web.chat

Web チャット。

最初は以下の 3 種類の画面に分けて実装し、最終的にひとつにまとめました。

* / ... トップ画面 (/read と /write へのリンクのみ) (ChatServlet)
* /read ... 読む画面 (ChatReadServlet)
* /write ... 書く画面 (ChatWriteServlet)

## web.shop

簡単なショッピングサイト「色売り屋」。以下の 4 種類の画面があります。

* / ... トップおよび色選択画面 (ColorServlet)
* /cart ... カート画面 (CartServlet)
* /login ... ログインおよびログイン済み画面 (LoginServlet)
* /sales ... 購入履歴および購入詳細画面 (SalesServlet)

## 注意事項

web.chat および web.shop ではデータベースに PostgreSQL を使ってます。  
JDBC ドライバは別途追加してください。
