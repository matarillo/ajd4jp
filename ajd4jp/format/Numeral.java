/*
 * AJD4JP
 * Copyright (c) 2011  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.format;

/**
 * 数字表記。
 */
public abstract class Numeral {
	Numeral() {}

	abstract char[] getChar();

	/**
	 * 文字列化。
	 * @param n 数値。
	 * @return 文字列。
	 */
	public String toString( int n ) {
		char[]	num = getChar();
		StringBuilder	buf = new StringBuilder();
		if ( n == 0 )	return new String( new char[]{ num[0] } );
		boolean	sign = n < 0;
		if ( sign )	n = n * -1;
		while( n > 0 ) {
			buf = buf.insert( 0, num[n % 10] );
			n /= 10;
		}
		if ( sign )	buf = buf.insert( 0, "－" );
		return buf.toString();
	}
}

