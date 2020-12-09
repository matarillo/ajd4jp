/*
 * AJD4JP
 * Copyright (c) 2011-2015  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp;

import ajd4jp.util.*;


/**
 * 曜日を表します。
 */
public enum Week implements OffProvider.Off {
	/** 日曜 */
	SUNDAY( 0, "Sun", "Sunday", "日" ),
	/** 月曜 */
	MONDAY( 1, "Mon", "Monday", "月" ),
	/** 火曜 */
	TUESDAY( 2, "Tue", "Tuesday", "火" ),
	/** 水曜 */
	WEDNESDAY( 3, "Wed", "Wednesday", "水" ),
	/** 木曜 */
	THURSDAY( 4, "Thu", "Thursday", "木" ),
	/** 金曜 */
	FRIDAY( 5, "Fri", "Friday", "金" ),
	/** 土曜 */
	SATURDAY( 6, "Sat", "Saturday", "土" );

	private String sh, lg, jp;
	private int no;
	private Week( int n, String s, String l, String j ) {
		no = n;
		sh = s;
		lg = l;
		jp = j;
	}
	/**
	 * 曜日の英語名略称を返します。
	 * @return Sun～Sat。
	 */
	public String getShortName() { return sh; }
	/**
	 * 曜日の英語名を返します。
	 * @return Sunday～Saturday。
	 */
	public String getLongName() { return lg; }
	/**
	 * 曜日の日本語名を返します。
	 * @return 日～土。
	 */
	public String getJpName() { return jp; }

	/**
	 * 曜日を判定します。
	 * @param date 対象日。
	 * @return 曜日。
	 */
	public static Week get( Day date ) {
		return values()[(int)(Calc.cut( date.toAJD().trim().getAJD() ) + 2) % 7];
	}

	int get() { return no; }
}

