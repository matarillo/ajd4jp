/*
 * AJD4JP
 * Copyright (c) 2011  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.format;


/**
 * 桁数無制限の漢数字。〇一二三四五六七八九。
 */
public class Japan extends Numeral {
	static char[]	num = "〇一二三四五六七八九".toCharArray();
	char[] getChar() { return num; }
	/**
	 * コンストラクタ。
	 */
	public Japan() {
		super();
	}
}

