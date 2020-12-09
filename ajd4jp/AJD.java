/*
 * AJD4JP
 * Copyright (c) 2011-2019  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp;

import ajd4jp.util.*;
import java.math.*;
import java.util.*;


/**
 * ユリウス通日および現在一般的に使われているグレゴリオ暦での１日を表します。<br>全てのコンストラクタはミリ秒情報を切り捨てます。
 */
public class AJD implements Day {
	private static final long serialVersionUID = 1;

	static final BigDecimal	FIX05 = new BigDecimal( "0.50000000" );
	static final BigDecimal	FIX_05 = new BigDecimal( "-0.50000000" );
	private BigDecimal	ajd;

	// エポック(1970/01/01 00:00:00)
	private static final BigDecimal EPOCH = new BigDecimal( "2440587.50000000000000000000" );

	private int year, mon, day, hour, min, sec;

	/**
	 * UTCからの日数差。
	 * @return 0.375(UTC+9.0を表す日数)。
	 */
	protected BigDecimal getOffsetDays() {
		return Calc.JP_H;
	}

	/**
	 * エポック(1970/01/01 00:00:00 GMT)からのミリ秒を返す。
	 * Java標準の日時系クラスへの設定値に使用できます。
	 * @return ミリ秒。
	 */
	public long getTime() {
		return Calc.up( Calc.mul( Calc.mul( Calc.mul( Calc.mul( ajd.subtract( EPOCH ), 24 ), 60 ), 60 ), 1000 ) ) / 1000 * 1000;
	}

	/**
	 * 指定秒を加算したインスタンスを返す。
	 * @param ss 秒。負数も可。
	 * @return 新しいインスタンス。
	 */
	public AJD addSecond( long ss ) {
		if ( ss == 0 )	return this;
		return from(
			ajd.add( new BigDecimal( ss ).multiply( Calc.SEC ) )
		);
	}

	/**
	 * 指定分を加算したインスタンスを返す。
	 * @param mm 分。負数も可。
	 * @return 新しいインスタンス。
	 */
	public AJD addMinute( long mm ) {
		if ( mm == 0 )	return this;
		return from(
			ajd.add( new BigDecimal( mm ).multiply( Calc.MIN ) )
		);
	}

	/**
	 * 指定時間を加算したインスタンスを返す。
	 * @param hh 時間。負数も可。
	 * @return 新しいインスタンス。
	 */
	public AJD addHour( long hh ) {
		if ( hh == 0 )	return this;
		return from(
			ajd.add( new BigDecimal( hh ).multiply( Calc.HOUR ) )
		);
	}

	/**
	 * 指定日数を加算したインスタンスを返す。
	 * @param dd 日数。負数も可。日未満の小数表現も可。
	 * @return 新しいインスタンス。
	 */
	public AJD addDay( Number dd ) {
		BigDecimal	d = new BigDecimal( dd.toString() );
		if ( d.compareTo( BigDecimal.ZERO ) == 0 )	return this;
		return from( ajd.add( d ) );
	}

	/**
	 * 保持している時刻を文字列にします。
	 * @return yyyy/mm/dd hh:mm:ss[ユリウス通日]のフォーマット。
	 */
	public String toString() {
		return String.format( "%d/%02d/%02d %02d:%02d:%02d[%s]", year, mon, day, hour, min, sec, Calc.toString( ajd ) );
	}

	/**
	 * 日本時間の西暦年の取得。
	 * @return 西暦年。負数なら紀元前。0は返りません。
	 */
	public int getYear() { return year; }
	private Era.Year era = null;
	/**
	 * 日本時間の和暦の取得。
	 * @return 和暦。明治より前は西暦のまま返します。
	 */
	public Era.Year getEra() {
		if ( era == null )	era = new Era.Year( this );
		return era;
	}

	/**
	 * 日本時間の月の取得。
	 * @return 月。1～12。
	 */
	public int getMonth() { return mon; }

	/**
	 * この日付が示すMonthを返す。
	 * @return 月。
	 */
	public Month toMonth() {
		return new Month(this);
	}

	/**
	 * 日本時間の日の取得。
	 * @return 日。1～N。
	 */
	public int getDay() { return day; }
	private Week week = null;
	/**
	 * 日本時間の曜日の取得。
	 * @return 曜日。
	 */
	public Week getWeek() {
		if ( week == null )	week = Week.get( this );
		return week;
	}

	/**
	 * 日本時間の時の取得。
	 * @return 時。0～23。
	 */
	public int getHour() { return hour; }
	/**
	 * 日本時間の分の取得。
	 * @return 分。0～59。
	 */
	public int getMinute() { return min; }
	/**
	 * 日本時間の秒の取得。
	 * @return 秒。0～59。
	 */
	public int getSecond() { return sec; }

	/**
	 * コンストラクタ。現在時刻が設定されます。
	 */
	public AJD() {
		this( new Date() );
	}

	/**
	 * コンストラクタ。引数の日時を表すインスタンスを生成します。
	 * @param date 日時。
	 */
	public AJD( java.util.Date date ) {
		this(date, Calc.JP_H);
	}
	/**
	 * コンストラクタ。引数の日時を表すインスタンスを生成します。
	 * @param date 日時。
	 * @param offset UTCからの時差(日数)。
	 */
	protected AJD( java.util.Date date, BigDecimal offset ) {
		int min = Calc.mul(offset, 24 * 60).intValue();
		int hour = min / 60;
		min = min % 60;
		String gmt = "GMT";
		if (hour >= 0 && min >= 0) { gmt = String.format("GMT+%02d%02d", hour, min); }
		else { gmt = String.format("GMT-%02d%02d", hour * -1, min * -1); }
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(gmt));
		calendar.setTime(date);
		try {
			initAJD(
				calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH) + 1,
				calendar.get(Calendar.DATE),
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE),
				calendar.get(Calendar.SECOND),
				offset
			);
		}
		catch(AJDException e){}
	}

	/**
	 * コンストラクタ。引数の日時を表すインスタンスを生成します。
	 * @param date 日時。
	 */
	public AJD( Day date ) {
		this( date.getAJD() );
	}

	/**
	 * コンストラクタ。引数の日時を表すインスタンスを生成します。
	 * @param calendar 日時。
	 */
	public AJD( Calendar calendar ) {
		this( calendar.getTime() );
	}

	private static final BigDecimal
		J122_1 = new BigDecimal( "122.1" ),
		J365_25 = new BigDecimal( "365.25" ),
		J30_6001 = new BigDecimal( "30.6001" );

	private void set( BigDecimal offset ) {
		BigDecimal	tmp = ajd.add( offset );
		double	org = ajd.add( offset ).doubleValue();
		long	jd = Calc.cut( tmp );
		tmp = tmp.subtract( new BigDecimal( jd ) );
		if ( tmp.compareTo( FIX05 ) >= 0 ) {
			jd++;
			tmp = tmp.subtract( FIX05 );
		}
		else	tmp = tmp.add( FIX05 );

		if ( org >= 2299160.5 ) jd = jd + 1 + ( jd - 1867216 ) / 36524 - ( jd - 1867216 ) / 146096;
		jd += 1524;
		BigDecimal	bjd = new BigDecimal( jd );

		long c = Calc.cut( Calc.div( bjd.subtract( J122_1 ), J365_25 ) );
		long k = c * 365 + c / 4;
		long e = Calc.cut( Calc.div( bjd.subtract( new BigDecimal( k ) ), J30_6001 ) );

		year = (int)( c - 4716 );
		mon = (int)( e - 1 );
		if ( mon > 12 ) {
			mon -= 12;
			year++;
		}
		if ( year <= 0 )	year--;
		day = (int)( jd - k - Calc.cut( new BigDecimal( "30.6" ).multiply( new BigDecimal( e ) ) ) );

		long s = Calc.cut( tmp.multiply( Calc.J86400 ).add( FIX05 ) );
		hour = (int)( s / 3600 );
		if ( hour >= 24 ) {
			hour -= 24;
			day++;
			int last = Month.getLastDay( year, mon );
			if ( day > last ) {
				day = 1;
				mon++;
				if ( mon > 12 ) {
					mon = 1;
					year++;
					if ( year == 0 )	year = 1;
				}
			}
		}
		min = (int)( ( s % 3600 ) / 60 );
		sec = (int)( s % 60 );
	}

	/**
	 * ユリウス通日からのインスタンス生成。
	 * @param num ユリウス通日。
	 * @return インスタンス。
	 */
	protected AJD from(Number num) {
		return new AJD(num);
	}

	/**
	 * 年月日時分秒からのインスタンス生成。
	 * @param yyyy 年
	 * @param mm 月
	 * @param dd 日
	 * @param hh 時
	 * @param mi 分
	 * @param ss 秒
	 * @return インスタンス。
	 * @throws AJDException 日時不正。
	 */
	protected AJD from(int yyyy, int mm, int dd, int hh, int mi, int ss) throws AJDException {
		return new AJD(yyyy, mm, dd, hh, mi, ss);
	}

	/**
	 * コンストラクタ。負数は指定できません。入力値の絶対値を採用します。
	 * @param num ユリウス通日。
	 */
	public AJD( Number num ) {
		this(num, Calc.JP_H);
	}
	/**
	 * コンストラクタ。負数は指定できません。入力値の絶対値を採用します。
	 * @param num ユリウス通日。
	 * @param offset UTCからの時差(日数)。
	 */
	protected AJD(Number num, BigDecimal offset) {
		ajd = new BigDecimal( num.toString() ).abs();
		set(offset);
	}

	/**
	 * コンストラクタ。日時は日本時間とみなされます。
	 * @param yyyy 年。0は指定できません。BC1年は-1です。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @param hh 時。0～23。
	 * @param mi 分。0～59。
	 * @param ss 秒。0～59。
	 * @throws AJDException 不正な日付。
	 */
	public AJD( int yyyy, int mm, int dd, int hh, int mi, int ss ) throws AJDException {
		this( yyyy, mm, dd, hh, mi, ss, Calc.JP_H );
	}
	/**
	 * コンストラクタ。
	 * @param yyyy 年。0は指定できません。BC1年は-1です。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @param hh 時。0～23。
	 * @param mi 分。0～59。
	 * @param ss 秒。0～59。
	 * @param offset UTCからの時差(日数)。
	 * @throws AJDException 不正な日付。
	 */
	protected AJD( int yyyy, int mm, int dd, int hh, int mi, int ss, BigDecimal offset ) throws AJDException {
		initAJD( yyyy, mm, dd, hh, mi, ss, offset );
	}

	AJD( int yyyy, int mm, int dd, int dummy ) {
		try {
			initAJD( yyyy, mm, dd, 0, 0, 0, Calc.JP_H );
		}
		catch( AJDException e ) {}
	}

	/**
	 * コンストラクタ。日時は日本時間とみなされます。
	 * 時間は 00:00:00 となります。
	 * @param yyyy 年。0は指定できません。BC1年は-1です。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @throws AJDException 不正な日付。
	 */
	public AJD( int yyyy, int mm, int dd )	throws AJDException {
		this( yyyy, mm, dd, 0, 0, 0 );
	}
	/**
	 * コンストラクタ。日時は日本時間とみなされます。
	 * 時間は 00:00:00 となります。
	 * @param yyyy 年。0は指定できません。BC1年は-1です。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @param offset UTCからの時差(日数)。
	 * @throws AJDException 不正な日付。
	 */
	protected AJD( int yyyy, int mm, int dd, BigDecimal offset )	throws AJDException {
		this( yyyy, mm, dd, 0, 0, 0, offset );
	}

	/**
	 * コンストラクタ。日時は日本時間とみなされます。
	 * @param yyyy 年。0は指定できません。BC1年は-1です。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @param hh 時。0～23。
	 * @param mi 分。0～59。
	 * @param ss 秒。0～59。
	 * @throws AJDException 不正な日付。
	 */
	public AJD(String yyyy, String mm, String dd, String hh, String mi, String ss) throws AJDException {
		this(
			Integer.parseInt(yyyy),
			Integer.parseInt(mm),
			Integer.parseInt(dd),
			Integer.parseInt(hh),
			Integer.parseInt(mi),
			Integer.parseInt(ss)
		);
	}
	/**
	 * コンストラクタ。日時は日本時間とみなされます。
	 * 時間は 00:00:00 となります。
	 * @param yyyy 年。0は指定できません。BC1年は-1です。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @throws AJDException 不正な日付。
	 */
	public AJD(String yyyy, String mm, String dd) throws AJDException {
		this(yyyy, mm, dd, "0", "0", "0");
	}

	static int check( int yy, int mm ) throws AJDException {
		if ( yy == 0 )	throw new AJDException( "年が0です。" );
		if ( mm < 1 || mm > 12 )	throw new AJDException( "月が範囲外です。" );
		if ( yy < 0 )	yy++;
		return yy;
	}

	void initAJD( int yy, int mm, int dd, int hh, int mi, int ss, BigDecimal offset ) throws AJDException {
		if ( dd < 1 || dd > new Month( yy, mm ).getLastDay() )
			throw new AJDException( "日が範囲外です。" );
		if ( hh < 0 || hh > 23 )	throw new AJDException( "時が範囲外です。" );
		if ( mi < 0 || mi > 59 )	throw new AJDException( "分が範囲外です。" );
		if ( ss < 0 || ss > 59 )	throw new AJDException( "秒が範囲外です。" );
		if (yy == 1582 && mm == 10 && dd > 4 && dd < 15) {
			throw new AJDException( "日が範囲外です。" );
		}

		year = yy;
		yy = check( yy, mm );
		mon = mm;
		day = dd;
		hour = hh;
		min = mi;
		sec = ss;

		boolean	bc_f = yy <= 0;
		boolean gre_f = yy > 1582 || ( yy == 1582 && mm > 10 ) ||
			( yy == 1582 && mm == 10 && dd >= 15 );
		if ( mm <= 2 ) {
			yy--;
			mm += 12;
		}

		long	jd;
		BigDecimal	ret;
		if ( hh < 12 ) {
			jd = 0;
			ret = FIX05;
		}
		else {
			jd = 1;
			ret = FIX_05;
		}
		ret = ret.add( Calc.div( new BigDecimal( hh * 3600 + mi * 60 + ss ), Calc.J86400 ) );

		jd += bc_f?	( yy - 3 ) / 4:	yy / 4;
		if ( gre_f )	jd += ( 2 - yy / 100 + yy / 400 );
		jd += ( 1720994 + yy * 365 + ( mm + 1 ) * 30 + ( mm + 1 ) * 3 / 5 + dd );
		ajd = ret.add( new BigDecimal( jd ) ).subtract( offset );
		if ( ajd.signum() == -1 )	throw new AJDException( "ユリウス通日基準日より過去の日付になりました。" );
	}

	/**
	 * ユリウス通日の取得。
	 * @return ユリウス通日。
	 */
	public BigDecimal getAJD() {
		return ajd;
	}

	/**
	 * thisを返します。
	 * @return this。
	 */
	public AJD toAJD() {
		return this;
	}

	/**
	 * ユリウス通日の比較。
	 * ユリウス通日を格納しているBigDecimalのcompareToを使用します。
	 * @param jd 比較対象。
	 * @return -1:this&lt;jd(thisが過去)、0:this==jd、1:this&gt;jd(thisが未来)。
	 */
	public int compareTo( Day jd ) {
		return ajd.compareTo( jd.getAJD() );
	}

	private transient Integer	hash = null;
	/**
	 * ユリウス通日を格納しているBigDecimalをtoString()したStringの
	 * ハッシュコードを返します。
	 * @return ハッシュコード。
	 */
	public int hashCode() {
		if ( hash == null ) {
			hash = Calc.toString( ajd ).hashCode();
		}
		return hash;
	}

	/**
	 * ユリウス通日の比較。
	 * {@link AJD#compareTo(Day)}が0か否かで判定します。
	 * BigDecimalのequalsは使用しません。
	 * @return true:ユリウス通日が一致、false:ユリウス通日が不一致。
	 */
	public boolean equals( Object o ) {
		if ( o instanceof Day )	return compareTo( (Day)o ) == 0;
		return false;
	}

	/**
	 * 日に丸めこみ、時間を 00:00:00 にしたインスタンスを返します。
	 * @return 当日0時のインスタンス。
	 */
	public AJD trim() {
		AJD	ret = null;
		try {
			ret = from( year, mon, day, 0, 0, 0 );
			if ( ret.equals( this ) )	return this;
		}
		catch( AJDException e ) {}
		return ret;
	}

	/**
	 * SQL用ラッパー。
	 * @return Date型。
	 */
	public java.sql.Date toDate() {
		return new java.sql.Date( trim().getTime() );
	}

	/**
	 * SQL用ラッパー。
	 * @return Time型。
	 */
	public java.sql.Time toTime() {
		try {
			return new java.sql.Time( from( 1970, 1, 1, getHour(), getMinute(), getSecond() ).getTime() );
		}
		catch( AJDException e ) {}
		return null;
	}

	/**
	 * SQL用ラッパー。
	 * @return Timestamp型。
	 */
	public java.sql.Timestamp toTimestamp() {
		return new java.sql.Timestamp( getTime() );
	}
}

