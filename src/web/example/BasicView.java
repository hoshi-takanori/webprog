package web.example;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import web.servlet.Response;

/**
 * 基本の View。
 */
public class BasicView {
	/**
	 * レスポンス (出力)。
	 */
	protected Response response;

	/**
	 * コンストラクタ。
	 * @param response レスポンス (出力)
	 */
	public BasicView(Response response) {
		this.response = response;
	}

	/**
	 * HTML エスケープした文字列を返す。
	 * @param str HTML エスケープする文字列
	 * @param br true なら改行コードを <br> に変換
	 * @return HTML エスケープした文字列
	 */
	public String htmlEscape(String str, boolean br) {
		if (str == null) {
			return null;
		}
		str = str.replace("&", "&amp;");
		str = str.replace("<", "&lt;");
		str = str.replace(">", "&gt;");
		str = str.replace("\"", "&quot;");
		if (br) {
			str = str.replace("\n", "&lt;br&gt;");
		}
		return str;
	}

	/**
	 * 開きタグ <tag attrs[0]="attrs[1]" attrs[2]="attrs[3]" ...> を返す。
	 * @param tag タグ
	 * @param attrs 属性のキーと値
	 * @return 開きタグの文字列
	 */
	public String openTag(String tag, String... attrs) {
		String str = "<" + tag;
		for (int i = 0; i < attrs.length; i += 2) {
			str += " " + attrs[i];
			if (i + 1 < attrs.length && attrs[i + 1] != null) {
				str += "=\"" + htmlEscape(attrs[i + 1], false) + "\"";
			}
		}
		return str + ">";
	}

	/**
	 * 閉じタグ </tag> を返す。
	 * @param tag タグ
	 * @return 閉じタグ
	 */
	public String closeTag(String tag) {
		return "</" + tag + ">";
	}

	/**
	 * タグで囲まれた文字列 <tag attrs[0]="attrs[1]" attrs[2]="attrs[3]" ...>str</tag> を返す。
	 * @param tag タグ
	 * @param str タグで囲む文字列
	 * @param attrs 属性のキーと値
	 * @return タグで囲まれた文字列
	 */
	public String taggedStr(String tag, String str, String... attrs) {
		return openTag(tag, attrs) + str + closeTag(tag);
	}

	/**
	 * リンクタグ <a href="path?params[0]=params[1]&params[2]=params[3]...">str</a> を返す。
	 * @param str タグで囲む文字列
	 * @param path リンク先の URL
	 * @param params URL パラメーターのキーと値
	 * @return リンクタグ
	 */
	public String linkTag(String str, String path, String... params) {
		if (params != null && params.length > 0) {
			String sep = "?";
			for (int i = 0; i < params.length; i += 2) {
				path += sep + params[i];
				if (i + 1 < params.length) {
					try {
						path += "=" + URLEncoder.encode(params[i + 1], "UTF-8");
					} catch (UnsupportedEncodingException e) {
						path += "=" + params[i + 1];
					}
				}
				sep = "&";
			}
		}
		return taggedStr("a", str, "href", path);
	}

	/**
	 * 外部 CSS へのリンクタグ <link rel="stylesheet" type="text/css" href="path"> を返す。
	 * @return 外部 CSS へのリンクタグ
	 */
	public String cssLinkTag(String path) {
		return openTag("link", "rel", "stylesheet", "type", "text/css", "href", path);
	}

	/**
	 * 外部 JavaScript へのリンクタグ <script type="text/javascript" src="path"></script> を返す。
	 * @return 外部 JavaScript へのリンクタグ
	 */
	public String scriptLinkTag(String path) {
		return taggedStr("script", "", "type", "text/javascript", "src", path);
	}

	/**
	 * 入力タグ <input type="type" name="name" value="value" attrs> を返す。
	 * @param type 入力タグの種類
	 * @param name 入力タグの名前
	 * @param value 入力タグの値
	 * @param attrs おまけの属性 (checked など)
	 * @return 入力タグ
	 */
	public String inputTag(String type, String name, String value, String... attrs) {
		ArrayList<String> list = new ArrayList<String>();
		list.add("type");
		list.add(type);
		if (name != null) {
			list.add("name");
			list.add(name);
		}
		if (value != null) {
			list.add("value");
			list.add(value);
		}
		for (String attr : attrs) {
			list.add(attr);
		}
		String[] array = new String[list.size()];
		list.toArray(array);
		return openTag("input", array);
	}

	/**
	 * 開きタグ <tag attrs[0]="attrs[1]" attrs[2]="attrs[3]" ...> を出力する。
	 * @param tag タグ
	 * @param attrs 属性のキーと値
	 */
	public void printOpenTag(String tag, String... attrs) {
		response.println(openTag(tag, attrs));
	}

	/**
	 * 閉じタグ </tag> を出力する。
	 * @param tag タグ
	 */
	public void printCloseTag(String tag) {
		response.println(closeTag(tag));
	}

	/**
	 * タグで囲まれた文字列 <tag attrs[0]="attrs[1]" attrs[2]="attrs[3]" ...>str</tag> を出力する。
	 * @param tag タグ
	 * @param str タグで囲む文字列
	 * @param attrs 属性のキーと値
	 */
	public void printTag(String tag, String str, String... attrs) {
		response.println(taggedStr(tag, str, attrs));
	}

	/**
	 * 入力タグ <input type="type" name="name" value="value" attrs> を出力する。
	 * @param type 入力タグの種類
	 * @param name 入力タグの名前
	 * @param value 入力タグの値
	 * @param attrs おまけの属性 (checked など)
	 */
	public void printInputTag(String type, String name, String value, String... attrs) {
		response.println(inputTag(type, name, value, attrs));
	}

	/**
	 * ページのボディ開始部を出力する。
	 * @param title ページのタイトル
	 */
	public void printOpenBodyTag(String title) {
		printOpenTag("body");
		printTag("h1", title);
	}

	/**
	 * HTML ヘッダーを出力する。
	 * @param title ページのタイトル
	 * @param tags 追加するタグ
	 */
	public void printHead(String title, String... tags) {
		response.setStatus(Response.STATUS_OK);
		response.setHeader("Content-Type", "text/html; charset=UTF-8");

		response.println("<!DOCTYPE html>");
		printOpenTag("html");
		printOpenTag("head");
		printTag("title", title);
		for (String tag : tags) {
			response.println(tag);
		}
		printCloseTag("head");
		printOpenBodyTag(title);
	}

	/**
	 * HTML の終わりを出力する。
	 */
	public void printTail() {
		printCloseTag("body");
		printCloseTag("html");
	}
}
