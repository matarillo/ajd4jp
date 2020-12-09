/*
 * AJD4JP
 * Copyright (c) 2011  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.format;


/**
 * 桁数無制限の全角アラビラ数字。０１２３４５６７８９。
 */
public class FullArabia extends Numeral {
	static final char[] num = "０１２３４５６７８９".toCharArray();
	char[] getChar() { return num; }
	/**
	 * コンストラクタ。
	 */
	public FullArabia() {
		super();
	}
}

