/*
 * AJD4JP
 * Copyright (c) 2011-2012  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.util;
import java.math.*;


public class Calc {
	public static final BigDecimal HOUR = div( BigDecimal.ONE, 24 );
	public static final BigDecimal MIN = div( HOUR, 60 );
	public static final BigDecimal SEC = div( MIN, 60 );
	public static final BigDecimal MSEC = div( SEC, 1000 );
	// 日本標準時 +9h
	public static final BigDecimal JP_H = new BigDecimal( "0.375" );

	public static final BigDecimal J86400 = new BigDecimal( "86400.00000000000000000000" );


	public static long up( BigDecimal d ) {
		return d.setScale( 0, BigDecimal.ROUND_UP ).longValue();
	}
	public static long cut( BigDecimal d ) {
		return d.setScale( 0, BigDecimal.ROUND_DOWN ).longValue();
	}
	public static long half( BigDecimal d ) {
		return d.setScale( 0, BigDecimal.ROUND_HALF_UP ).longValue();
	}
	public static BigDecimal div( BigDecimal a, BigDecimal b ) {
		return a.divide( b, 30, BigDecimal.ROUND_HALF_UP );
	}
	public static BigDecimal div( BigDecimal a, long b ) {
		return div( a, new BigDecimal( b ) );
	}
	public static BigDecimal mul( BigDecimal a, long b ) {
		return a.multiply( new BigDecimal( b ) );
	}

	public static BigDecimal cos( BigDecimal r ) {
		return new BigDecimal( Double.toString( Math.cos( r.doubleValue() * 3.1415926535 / 180 ) ) );
	}

	public static String toString( BigDecimal b ) {
		String	org = b.toPlainString();
		char[]	str = org.toCharArray();
		boolean	flag = false;
		for ( int i = str.length - 1; i >= 0; i-- ) {
			if ( str[i] == '.' ) {
				flag = true;
				str[i] = ' ';
				break;
			}
			if ( str[i] != '0' )	break;
			str[i] = ' ';
		}
		int	len = 0;
		for ( ; len < str.length; len++ ) {
			if ( str[len] == ' ' )	break;
			if ( str[len] == '.' )	flag = true;
		}
		return flag?	new String( str, 0, len ): org;
	}
}

