/*
 * AJD4JP
 * Copyright (c) 2011  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.format;

import ajd4jp.*;

/**
 * 西暦年のフォーマット。
 */
public class GregorianYearF extends Format {
	private String[]	era;
	/**
	 * コンストラクタ。
	 * @param bc 紀元前に付ける文字列。
	 * @param ad 紀元後に付ける文字列。
	 * @param num 数値表記。
	 */
	public GregorianYearF( String bc, String ad, Numeral num ) {
		super( num );
		if ( bc == null )	bc = "";
		if ( ad == null )	ad = "";
		era = new String[] {
			bc, ad
		};
	}

	/**
	 * コンストラクタ。new GregorianYearF( "BC", "", num ) と等価です。
	 * @param num 数値表記。
	 */
	public GregorianYearF( Numeral num ) {
		this( "BC", "", num );
	}

	/**
	 * コンストラクタ。new GregorianYearF( new HalfArabia() ) と等価です。
	 */
	public GregorianYearF() {
		this( new HalfArabia() );
	}

	boolean	bc_f = false;
	int getNum( ajd4jp.Day date ) {
		int	v = date.getYear();
		if ( v < 0 ) {
			bc_f = true;
			v *= -1;
		}
		else	bc_f = false;
		return v;
	}

	String getHead() {
		return era[bc_f? 0: 1];
	}

}


