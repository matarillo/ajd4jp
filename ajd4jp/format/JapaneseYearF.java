/*
 * AJD4JP
 * Copyright (c) 2011-2019  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.format;

import ajd4jp.*;

/**
 * 和暦年のフォーマット。
 */
public class JapaneseYearF extends Format {
	/**
	 * 元号の付与。
	 */
	public enum Era {
		/**
		 * 漢字表記。平成など。
		 */
		KANJI,
		/**
		 * 半角1文字。平成ならばH。
		 */
		HALF,
		/**
		 * 全角1文字。平成ならばＨ。
		 */
		FULL
	}

	private Era	e;
	/**
	 * コンストラクタ。
	 * @param era 元号の指定。
	 * @param num 数値表記。
	 */
	public JapaneseYearF( Era era, Two num ) {
		super( num );
		e = era;
		if ( e == null )	e = Era.KANJI;
	}

	/**
	 * コンストラクタ。new JapaneseYearF( Era.KANJI, num ) と等価です。
	 * @param num 数値表記。
	 */
	public JapaneseYearF( Two num ) {
		this( Era.KANJI, num );
	}

	/**
	 * コンストラクタ。new JapaneseYearF( Era.KANJI, new TwoHalfArabia() ) と等価です。
	 */
	public JapaneseYearF() {
		this( Era.KANJI, new TwoHalfArabia() );
	}

	ajd4jp.Era.Year	jp;
	int getNum( ajd4jp.Day date ) {
		jp = new ajd4jp.Era.Year( date );
		return jp.getYear();
	}

	String getHead() {
		String	h = "";
		ajd4jp.Era	era = jp.getEra();
		if ( era != null ) { h = era.getName(e); }
		return h;
	}
}

