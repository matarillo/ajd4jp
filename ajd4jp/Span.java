/*
 * AJD4JP
 * Copyright (c) 2011  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp;

import ajd4jp.util.*;
import java.math.*;


/**
 * 2点の差を表します。
 */
public class Span implements Comparable<Span>, java.io.Serializable {
	private BigDecimal	sub;
	private int d, h, m, s;

	/**
	 * コンストラクタ。a - b の日差(絶対値)を生成します。
	 * @param a 差を取る時点。
	 * @param b 差を取る時点。
	 */
	public Span( Day a, Day b ) {
		sub = a.getAJD().subtract( b.getAJD() ).abs();
		d = (int)Calc.cut( sub );
		long	tmp = Calc.cut( sub.multiply( Calc.J86400 ).add( AJD.FIX05 ) );
		h = (int)( ( tmp / 3600 ) % 24 );
		m = (int)( ( tmp % 3600 ) / 60 );
		s = (int)( tmp % 60 );
	}

	/**
	 * 差の日数を返します。日未満の時間部は小数で表現されます。
	 * @return 日数。
	 */
	public BigDecimal getDays() { return sub; }

	/**
	 * 差の日未満を切り捨てた整数を返します。
	 * @return 日数。
	 */
	public int getDayPart() { return d; }

	/**
	 * 差の日未満のうち、時間部分を返します。
	 * @return 時間。0～23。
	 */
	public int getHourPart() { return h; }

	/**
	 * 差の日未満のうち、分部分を返します。
	 * @return 分。0～59。
	 */
	public int getMinutePart() { return m; }

	/**
	 * 差の日未満のうち、秒部分を返します。
	 * @return 秒。0～59。
	 */
	public int getSecondPart() { return s; }

	/**
	 * 文字列化。
	 * @return 小数点表現付きの日数。
	 */
	public String toString() {
		return sub.toString();
	}

	/**
	 * 日数差の比較。
	 * 日数を格納しているBigDecimalのcompareToを使用します。
	 * @param span 比較対象。
	 * @return -1:this&lt;span(thisが短期間)、0:this==span、
	 * 1:this&gt;span(thisが長期間)。
	 */
	public int compareTo( Span span ) {
		return sub.compareTo( span.sub );
	}

	private transient Integer	hash = null;
	/**
	 * 日数を格納しているBigDecimalをtoString()したStringのハッシュコードを返します。
	 * @return ハッシュコード。
	 */
	public int hashCode() {
		if ( hash == null ) {
			hash = sub.toString().hashCode();
		}
		return hash;
	}

	/**
	 * 日数差の比較。
	 * {@link Span#compareTo(Span)}が0か否かで判定します。
	 * BigDecimalのequalsは使用しません。
	 * @return true:日数が一致、false:日数が不一致。
	 */
	public boolean equals( Object o ) {
		if ( o instanceof Span )	return compareTo( (Span)o ) == 0;
		return false;
	}
}

