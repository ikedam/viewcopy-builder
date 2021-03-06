Viewcopy Builder plugin
=======================

ビューをコピーするビルド手順を追加するJenkinsプラグイン

これはなに？
------------

Viewcopy Builder は、「ビューをコピーする」ビルド手順を追加する [Jenkins](http://jenkins-ci.org/) プラグインです: 

* 既存のビューから新しいビューを作成します。
	* ビルド手順として設定できるので、複数のビルド手順を追加することで1度のビルドで複数のビューをコピーできます。
* 以下のパラメータを設定します:
	* コピー元のビュー
		* 変数を使用できます
	* コピーして作成するビュー
		* 変数を使用できます
	* 上書きする
		* コピー先のビューが既に存在する場合に、ビューを上書きするかどうかを指定します。
* ビューをコピーするときに追加で行う処理を指定できます。
	* 文字列を置き換える: ビューの設定に含まれる文字列を置換します。
		* 置換元、置換先の文字列には変数を使用できます。
	* 正規表現を設定する: リストビューの正規表現を設定します。
	* 説明を設定する: ビューの説明を設定します。
* 追加で行う処理は[Jenkinsの拡張ポイント機能] (https://wiki.jenkins-ci.org/display/JENKINS/Extension+points) を使用して新しいものを追加することができます。

制限事項
--------

* 「ビューをコピーする」ビルド手順を設定したジョブはマスターノードで実行する必要があります。

このプラグインの動作原理
------------------------

このプラグインは以下のように動作します:

1. XSTREAMでコピー元のビューの設定XMLを生成する。
2. 追加の処理を設定XMLに適用する。
3. 変換後のXMLから新しいビューを作る。

拡張ポイント
------------

新しい追加の処理を作る場合は、`ViewcopyOperation` 抽象クラスを拡張し、以下のメソッドをオーバーライドします:

```java
public abstract Document ViewcopyOperation::perform(Document doc, EnvVars env, PrintStream logger)
```
