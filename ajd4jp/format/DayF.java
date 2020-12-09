/*
 * AJD4JP
 * Copyright (c) 2011  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.format;

import ajd4jp.*;


/**
 * 日のフォーマット。
 */
public class DayF extends Format {
	/**
	 * コンストラクタ。
	 * @param num 数値表記。
	 */
	public DayF( Two num ) {
		super( num );
	}

	/**
	 * コンストラクタ。new DayF( new TwoHalfArabia( '0' ) ) と等価です。
	 */
	public DayF() {
		this( new TwoHalfArabia( '0' ) );
	}

	int getNum( ajd4jp.Day date ) {
		return date.getDay();
	}
}

