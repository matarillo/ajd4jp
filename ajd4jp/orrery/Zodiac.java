/*
 * AJD4JP
 * Copyright (c) 2011-2017  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.orrery;
import ajd4jp.*;
import java.io.*;


/**
 * 黄道十二宮。
 */
public enum Zodiac {
	/**
	 * 白羊宮(&#x2648;)。
	 */
	ARIES( 30, "白羊宮", "牡羊座", "\u2648" ),
	/**
	 * 金牛宮(&#x2649;)。
	 */
	TAURUS( 60, "金牛宮", "牡牛座", "\u2649" ),
	/**
	 * 双児宮(&#x264A;)。
	 */
	GEMINI( 90, "双児宮", "双子座", "\u264A" ),
	/**
	 * 巨蟹宮(&#x264B;)。
	 */
	CANCER( 120, "巨蟹宮", "蟹座", "\u264B" ),
	/**
	 * 獅子宮(&#x264C;)。
	 */
	LEO( 150, "獅子宮", "獅子座", "\u264C" ),
	/**
	 * 処女宮(&#x264D;)。
	 */
	VIRGO( 180, "処女宮", "乙女座", "\u264D" ),
	/**
	 * 天秤宮(&#x264E;)。
	 */
	LIBRA( 210, "天秤宮", "天秤座", "\u264E" ),
	/**
	 * 天蝎宮(&#x264F;)。
	 */
	SCORPIO( 240, "天蝎宮", "蠍座", "\u264F" ),
	/**
	 * 人馬宮(&#x2650;)。
	 */
	SAGITTARIUS( 270, "人馬宮", "射手座", "\u2650" ),
	/**
	 * 磨羯宮(&#x2651;)。
	 */
	CAPRICORN( 300, "磨羯宮", "山羊座", "\u2651" ),
	/**
	 * 宝瓶宮(&#x2652;)。
	 */
	AQUARIUS( 330, "宝瓶宮", "水瓶座", "\u2652" ),
	/**
	 * 双魚宮(&#x2653;)。
	 */
	PISCES( 360, "双魚宮", "魚座", "\u2653" );


	private String[] jp_name;
	private String code;
	private Angle	start, end;
	private Zodiac( int a, String jp1, String jp2,  String c ) {
		end = new Angle( a, Angle.Unit.DEGREE );
		start = end.subtract( new Angle( 30, Angle.Unit.DEGREE ) );
		jp_name = new String[]{ jp1, jp2 };
		code = c;
	}

	/**
	 * 星座の日本語名。<br>
	 * constellation が true なら「牡羊座」のような名称を、
	 * false なら「白羊宮」のような名称を返します。
	 * @param constellation true:星座名称、false:十二宮名称。
	 * @return 日本語名。
	 */
	public String getJpName(boolean constellation) {
		return jp_name[constellation? 1: 0];
	}

	/**
	 * UTF環境で表示可能なシンボルを表す文字を返します。
	 * @return 「&#x2648;」のような文字列。
	 */
	public String getSymbol() { return code; }

	/**
	 * 指定された黄経を含むか判定します。
	 * @param longitude 黄経。
	 * @return true:含む、false:含まない。
	 */
	public boolean isRange( Angle longitude ) {
		longitude = longitude.convert( Angle.Unit.DEGREE60 );
		if ( start.compareTo( longitude ) > 0 )	return false;
		if ( end.compareTo( longitude ) <= 0 )	return false;
		return true;
	}

	/**
	 * 対象星座の取得。
	 * @param jed 判定する日時。
	 * @param planet 判定する惑星。
	 * @return 星座。
	 * @throws AJDException 引数ミス。
	 * @throws IOException ファイルIOエラー。
	 */
	public static Zodiac getZodiac( ETD jed, Planet planet ) throws AJDException, IOException {
		Pole.PL	pole = Pole.plot( jed, planet, Pole.Plane.ECLIPTIC ).get( Angle.Unit.SHIFT_DEGREE60, Angle.Unit.DEGREE60 );
		for ( Zodiac z: values() ) {
			if ( z.isRange( pole.lon ) )	return z;
		}
		return null;
	}
}

