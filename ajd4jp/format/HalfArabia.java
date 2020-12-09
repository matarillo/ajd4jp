/*
 * AJD4JP
 * Copyright (c) 2011  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.format;

/**
 * 桁数無制限の半角アラビア数字。0123456789。
 */
public class HalfArabia extends Numeral {
	static final char[]	num = "0123456789".toCharArray();
	char[] getChar() { return num; }
	/**
	 * コンストラクタ。
	 */
	public HalfArabia(){
		super();
	}
}

