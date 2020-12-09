/*
 * AJD4JP
 * Copyright (c) 2011  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.format;

import ajd4jp.*;

/**
 * 書式化処理。
 */
public abstract class Format {
	private Numeral	num;

	Format( Numeral n ) {
		num = n;
	}

	String getHead() {
		return "";
	}
	abstract int getNum( ajd4jp.Day date );

	/**
	 * 書式化。
	 * @param date 書式化対象。
	 * @return 書式化した文字列。
	 */
	public String toString( ajd4jp.Day date ) {
		synchronized( this ) {
			int	n = getNum( date );
			StringBuilder	buf = new StringBuilder( getHead() );
			return buf.append( num.toString( n ) ).toString();
		}
	}
}

