/*
 * AJD4JP
 * Copyright (c) 2011  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp;

import ajd4jp.util.*;


/**
 * 十干。
 */
public enum HeavenlyStem {
	/** 甲(きのえ) */
	KOU( "甲", 0 ),
	/** 乙(きのと) */
	OTSU( "乙", 1 ),
	/** 丙(ひのえ) */
	HEI( "丙", 2 ),
	/** 丁(ひのと) */
	TEI( "丁", 3 ),
	/** 戊(つちのえ) */
	BO( "戊", 4 ),
	/** 己(つちのと) */
	KI( "己", 5 ),
	/** 庚(かのえ) */
	KO( "庚", 6 ),
	/** 辛(かのと) */
	SHIN( "辛", 7 ),
	/** 壬(みずのえ) */
	JIN( "壬", 8 ),
	/** 癸(みずのと) */
	CI( "癸", 9 );

	private String name;
	private int	no;
	private HeavenlyStem( String s, int n ) {
		name = s;
		no = n;
	}
	/**
	 * 名称の取得。
	 * @return 甲～癸。
	 */
	public String getName() {
		return name;
	}

	/**
	 * 通し番号の取得。
	 * @return 0(甲)～9(癸)。
	 */
	public int getNo() { return no; }

	private static final HeavenlyStem[]	hs = values();
	private static HeavenlyStem fix( int v ) {
		v %= hs.length;
		if ( v < 0 )	v += hs.length;
		return hs[v];
	}

	/**
	 * 年の十干の取得。
	 * @param date 年の取得先。
	 * @return その年を表す十干。
	 */
	public static HeavenlyStem getYear( Day date ) {
		int	yy = date.getYear();
		if ( yy < 0 )	yy++;
		return fix( yy - 4 );
	}

	/**
	 * 月の十干の取得。
	 * @param date 月の取得先。
	 * @return その月を表す十干。
	 */
	public static HeavenlyStem getMonth( Day date ) {
		int	yy = date.getYear();
		if ( yy < 0 )	yy++;
		return fix( (yy % 5) * 2 + 3 + date.getMonth() );
	}

	/**
	 * 日の十干の取得。
	 * @param date 日の取得先。
	 * @return その日を表す十干。
	 */
	public static HeavenlyStem getDay( Day date ) {
		AJD	ajd = new AJD( date.getAJD() );
		return fix( (int)Calc.cut( ajd.trim().getAJD() ) );
	}

	/**
	 * 時間の十干の取得。
	 * @param date 時間の取得先。
	 * @return その時間を表す十干。
	 */
	public static HeavenlyStem getTime( Day date ) {
		AJD	ajd = new AJD( date.getAJD() );
		if ( ajd.getHour() == 23 )	ajd = ajd.addHour( 1 );
		HeavenlyStem	day = getDay( ajd );
		EarthlyBranch	hour = EarthlyBranch.getTime( ajd );
		return fix( (day.getNo() % 5) * 2 + hour.getNo() );
	}
}

