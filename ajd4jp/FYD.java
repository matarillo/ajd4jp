/*
 * AJD4JP
 * Copyright (c) 2011-2012  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp;

import java.math.*;

/**
 * 年度換算したAJDを表します。
 */
public class FYD implements Day {
	private static final long serialVersionUID = 2;

	private int[] date;
	AJD ajd;

	public BigDecimal getAJD() {
		return ajd.getAJD();
	}
	public AJD toAJD() {
		return ajd;
	}
	/**
	 * 年度を返します。
	 * @return 年度。
	 */
	public int getYear() {
		return date[0];
	}
	/**
	 * 締め月を返します。
	 * @return 締め月。
	 */
	public int getMonth() {
		return date[1];
	}
	/**
	 * 締め日を返します。
	 * @return 締め日。
	 */
	public int getDay() {
		return date[2];
	}
	public int getHour() {
		return ajd.getHour();
	}
	public int getMinute() {
		return ajd.getMinute();
	}
	public int getSecond() {
		return ajd.getSecond();
	}

	private FYD(){}
	FYD( FiscalYear f, AJD a ) {
		ajd = a;
		if ( ajd == null )	return;
		date = f.getDate( ajd );
	}

	/**
	 * ユリウス通日の比較。
	 * ユリウス通日を格納しているBigDecimalのcompareToを使用します。
	 * @param jd 比較対象。
	 * @return -1:this&lt;jd(thisが過去)、0:this==jd、1:this&gt;jd(thisが未来)。
	 */
	public int compareTo( Day jd ) {
		return ajd.compareTo( jd );
	}

	/**
	 * ユリウス通日のハッシュコードを返します。
	 * @return ハッシュコード。
	 */
	public int hashCode() {
		return ajd.hashCode();
	}

	/**
	 * ユリウス通日の比較。
	 * @return true:ユリウス通日が一致、false:ユリウス通日が不一致。
	 */
	public boolean equals( Object o ) {
		return ajd.equals( o );
	}

	/**
	 * 文字列化。
	 * @return yyyy/mm/dd hh:mm:ss形式のフォーマットで返します。
	 */
	public String toString() {
		return String.format( "%d/%02d/%02d %02d:%02d:%02d",
			getYear(), getMonth(), getDay(),
			getHour(), getMinute(), getSecond()
		);
	}
}

