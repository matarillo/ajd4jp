/*
 * AJD4JP
 * Copyright (c) 2011-2012  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.orrery.tool;

public enum JPLItem {
	MERCURY( 3, Centric.ICRS ),
	VENUS( 3, Centric.ICRS ),
	EARTH_MOON_BARYCENTER( 3, Centric.ICRS ),
	MARS( 3, Centric.ICRS ),
	JUPITER( 3, Centric.ICRS ),
	SATURN( 3, Centric.ICRS ),
	URANUS( 3, Centric.ICRS ),
	NEPTUNE( 3, Centric.ICRS ),
	PLUTO( 3, Centric.ICRS ),
	MOON( 3, Centric.GEO ),
	SUN( 3, Centric.ICRS ),
	NUTATIONS( 2, Centric.NO ),
	LIBRATIONS( 3, Centric.NO );

	public enum Centric {
		GEO, ICRS, NO;
	}

	private String fname;
	private int param_count;
	private Centric type;
	private int no;

	private JPLItem( int cnt, Centric c ) {
		param_count = cnt;
		type = c;
		fname = toString() + ".de";
	}

	public String getFileName() { return fname; }

	public int GetDim() { return param_count; }
	public Centric GetType() { return type; }
}


