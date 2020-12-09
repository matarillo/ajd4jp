/*
 * AJD4JP
 * Copyright (c) 2011-2017  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.format;

import ajd4jp.*;

/**
 * 時のフォーマット。
 */
public class HourF extends Format {
	private String[]	ampm = null;
	/**
	 * コンストラクタ。24時間表記。
	 * @param num 数値表記。
	 */
	public HourF( Two num ) {
		super( num );
	}

	/**
	 * コンストラクタ。new Day( new TwoHalfArabia( '0' ) ) と等価です。
	 */
	public HourF() {
		this( new TwoHalfArabia( '0' ) );
	}

	/**
	 * コンストラクタ。12時間表記。
	 * @param am 午前に付ける文字列。"AM"など。
	 * @param pm 午後に付ける文字列。"PM"など。
	 * @param num 時間。
	 */
	public HourF( String am, String pm, Two num ) {
		this( num );
			if ( am == null )	am = "";
			if ( pm == null )	pm = "";
		ampm = new String[] {
			am, pm
		};
	}

	boolean	am_f = false;
	int getNum( ajd4jp.Day date ) {
		int	h = date.getHour();
		if ( ampm == null ) return h;
		am_f = h < 12;
		return h % 12;
	}

	String getHead() {
		if ( ampm == null )	return "";
		return ampm[am_f? 0: 1];
	}
}


