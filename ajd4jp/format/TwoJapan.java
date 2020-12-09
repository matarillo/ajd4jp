/*
 * AJD4JP
 * Copyright (c) 2011  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.format;


/**
 * 2桁の漢数字。
 */
public class TwoJapan extends Two {
	/**
	 * コンストラクタ。
	 * @param padding 1桁だった場合に先頭へ付与する文字。
	 * 0 やスペースなど。
	 * @param rule 変換ルール。
	 */
	public TwoJapan( char padding, Rule ... rule ) {
		super( new String( new char[]{ padding } ), rule );
	}
	/**
	 * コンストラクタ。数値が1桁の場合、先頭に何も補いません。
	 * @param rule 変換ルール。
	 */
	public TwoJapan( Rule ... rule ) {
		super( null, rule );
	}
	char[] getChar() {
		final Japan	org = new Japan();
		return org.getChar();
	}
}

