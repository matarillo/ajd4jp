/*
 * AJD4JP
 * Copyright (c) 2011  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp;

import ajd4jp.util.*;


/**
 * 十二支。
 */
public enum EarthlyBranch {
	/** 子(ね) */
	SHI( "子", 0 ),
	/** 丑(うし) */
	CHU( "丑", 1 ),
	/** 寅(とら) */
	IN( "寅", 2 ),
	/** 卯(う) */
	BOU( "卯", 3 ),
	/** 辰(たつ) */
	SHIN( "辰", 4 ),
	/** 巳(み) */
	SI( "巳", 5 ),
	/** 午(うま) */
	GO( "午", 6 ),
	/** 未(ひつじ) */
	BI( "未", 7 ),
	/** 申(さる) */
	SIN( "申", 8 ),
	/** 酉(とり) */
	YU( "酉", 9 ),
	/** 戌(いぬ) */
	JUTSU( "戌", 10 ),
	/** 亥(い) */
	GAI( "亥", 11 );

	private String name;
	private int	no;
	private EarthlyBranch( String s, int n ) {
		name = s;
		no = n;
	}

	/**
	 * 名称の取得。
	 * @return 子～亥。
	 */
	public String getName() {
		return name;
	}

	/**
	 * 通し番号の取得。
	 * @return 0(子)～11(亥)。
	 */
	public int getNo() { return no; }

	private static final EarthlyBranch[]	eb = values();
	private static EarthlyBranch fix( int v ) {
		v %= eb.length;
		if ( v < 0 )	v += eb.length;
		return eb[v];
	}

	/**
	 * 年の十二支の取得。
	 * @param date 年の取得先。
	 * @return その年を表す十二支。
	 */
	public static EarthlyBranch getYear( Day date ) {
		int	yy = date.getYear();
		if ( yy < 0 )	yy++;
		return fix( yy - 4 );
	}

	/**
	 * 月の十二支の取得。
	 * @param date 月の取得先。
	 * @return その月を表す十二支。
	 */
	public static EarthlyBranch getMonth( Day date ) {
		return fix( date.getMonth() - 11 );
	}

	/**
	 * 日の十二支の取得。
	 * @param date 日の取得先。
	 * @return その日を表す十二支。
	 */
	public static EarthlyBranch getDay( Day date ) {
		AJD	ajd = new AJD( date.getAJD() );
		return fix( (int)Calc.cut( ajd.trim().getAJD() ) - 10 );
	}

	/**
	 * 時間の十二支の取得。
	 * @param date 時間の取得先。
	 * @return その時間を表す十二支。
	 */
	public static EarthlyBranch getTime( Day date ) {
		return fix( (date.getHour() + 1) / 2 );
	}
}

