/*
 * AJD4JP
 * Copyright (c) 2011  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp;

import java.math.*;

/**
 * 旧暦の日を表します。
 */
public class LSCD implements Day {
	private static final long serialVersionUID = 1;

	/** 六曜。 */
	public enum Rokuyo {
		/** 先勝 */
		SENSHO( "先勝" ),
		/** 友引 */
		TOMOBIKI( "友引" ),
		/** 先負 */
		SENBU( "先負" ),
		/** 仏滅 */
		BUTSUMETSU( "仏滅" ),
		/** 大安 */
		TAIAN( "大安" ),
		/** 赤口 */
		SHAKKO( "赤口" );

		private String name;
		private Rokuyo( String s ) {
			name = s;
		}
		/**
		 * 名称の取得。
		 * @return 先勝～赤口。
		 */
		public String getName() {
			return name;
		}
	}

	private int	year, mon, day;
	private boolean	leap_f;
	private AJD	ajd;

	LSCD( int y, boolean f, int m, int d, AJD mine ) {
		year = y;
		mon = m;
		leap_f = f;
		day = d;
		ajd = mine;
	}

	LSCD( int y, boolean f, int m, AJD start, AJD mine ) {
		this( y, f, m, new Span( start, mine.trim() ).getDayPart() + 1, mine );
	}

	/**
	 * 年を返します。
	 * @return 年。
	 */
	public int getYear() {
		return year;
	}
	/**
	 * 月を返します。
	 * @return 月。
	 */
	public int getMonth() {
		return mon;
	}
	/**
	 * 閏月であるか返します。
	 * @return true:閏月、false:平月。
	 */
	public boolean isLeapMonth() {
		return leap_f;
	}
	/**
	 * 月の日本語名を返します。
	 * @return 睦月～師走。閏の文字が付与される場合があります。
	 */
	public String getMonthName() {
		StringBuilder	buf = new StringBuilder( leap_f? "閏": "" );
		return buf.append( Month.jp[mon - 1] ).toString();
	}
	/**
	 * 日を返します。
	 * @return 日。
	 */
	public int getDay() {
		return day;
	}

	/**
	 * 時の取得。
	 * @return 時。0～23。
	 */
	public int getHour() { return ajd.getHour(); }
	/**
	 * 分の取得。
	 * @return 分。0～59。
	 */
	public int getMinute() { return ajd.getMinute(); }
	/**
	 * 秒の取得。
	 * @return 秒。0～59。
	 */
	public int getSecond() { return ajd.getSecond(); }

	/**
	 * 六曜を返します。
	 * @return 六曜。
	 */
	public Rokuyo getRokuyo() {
		return Rokuyo.values()[(mon + day - 2) % 6];
	}

	/**
	 * ユリウス通日の取得。
	 * @return ユリウス通日。
	 */
	public AJD toAJD() {
		return ajd;	
	}

	/**
	 * ユリウス通日の取得。
	 * @return ユリウス通日。
	 */
	public BigDecimal getAJD() {
		return ajd.getAJD();
	}

	/**
	 * 文字列化。
	 * @return yyyy/mm/dd hh:mm:ss形式のフォーマットで返します。
	 * 月には閏の文字が付与される場合があります。
	 */
	public String toString() {
		String	leap = leap_f?	"閏":	"";
		return String.format( "%d/%s%02d/%02d %02d:%02d:%02d", year, leap, mon, day,
			getHour(), getMinute(), getSecond()
		); 
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
}

