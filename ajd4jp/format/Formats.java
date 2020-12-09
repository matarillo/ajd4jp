/*
 * AJD4JP
 * Copyright (c) 2011-2015  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.format;


/**
 * フォーマットの集合。
 */
public class Formats extends Format {
	private Format[]	fm;

	/**
	 * YYYY/MM/DD フォーマット。
	 */
	public static final Formats DATE = new Formats(
		new GregorianYearF(), new C( "/" ),
		new MonthF(), new C( "/" ),
		new DayF()
	);
	/**
	 * HH:MM:SS 24時間制フォーマット。
	 */
	public static final Formats TIME = new Formats(
		new HourF(), new C( ":" ),
		new MinuteF(), new C( ":" ),
		new SecondF()
	);
	/**
	 * YYYY/MM/DD HH:MM:SS 24時間制フォーマット。
	 */
	public static final Formats TIMESTAMP = new Formats(
		DATE, new C( " " ), TIME
	);

	/**
	 * YYYYMMDD フォーマット。
	 */
	public static final Formats YMD = new Formats(
		new GregorianYearF(), new MonthF(), new DayF()
	);
	/**
	 * HHMMSS 24時間制フォーマット。
	 */
	public static final Formats HMS = new Formats(
		new HourF(), new MinuteF(), new SecondF()
	);
	/**
	 * YYYYMMDDHHMMSS 24時間制フォーマット。
	 */
	public static final Formats YMDHMS = new Formats(
		YMD, HMS
	);

	/**
	 * コンストラクタ。
	 * @param format 出力対象。
	 */
	public Formats( Format ... format ) {
		super( null );
		fm = format;
	}

	int getNum( ajd4jp.Day date ) { return 0; }

	/**
	 * 書式化。内包する Format を連結して出力します。
	 * @param date 書式化対象。
	 * @return 書式化した文字列。
	 */
	public String toString( ajd4jp.Day date ) {
		StringBuilder	buf = new StringBuilder();
		for ( Format f: fm ) {
			buf = buf.append( f.toString( date ) );
		}
		return buf.toString();
	}
}

