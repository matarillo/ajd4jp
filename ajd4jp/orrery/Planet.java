/*
 * AJD4JP
 * Copyright (c) 2011-2018  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.orrery;

import ajd4jp.orrery.tool.*;
import java.math.*;


/**
 * 惑星。ただし便宜上、太陽等もここに含みます。
 */
public enum Planet {
	/** 太陽系の重力中心点。 */
	SOLAR_SYSTEM_BARYCENTER( null, "太陽系の重力中心点", false, "　" ),
	/** 太陽(&#x2609;)。 */
	SUN( JPLItem.SUN, "太陽", true,  "\u2609" ),
	/** 水星(&#x263f;)。 */
	MERCURY( JPLItem.MERCURY, "水星", true,  "\u263f" ),
	/** 金星(&#x2640;)。 */
	VENUS( JPLItem.VENUS, "金星", true,  "\u2640" ),
	/** 地球(&#x2641;)。 */
	EARTH( null, "地球", true,  "\u2641" ),
	/** 地球と月の重力中心点。 */
	EARTH_MOON_BARYCENTER( JPLItem.EARTH_MOON_BARYCENTER, "地球と月の重力中心点", false, "　" ),
	/** 月(&#x263d;)。 */
	MOON( JPLItem.MOON, "月", true,  "\u263d" ),
	/** 火星(&#x2642;)。 */
	MARS( JPLItem.MARS, "火星", true,  "\u2642" ),
	/** 木星(&#x2643;)。 */
	JUPITER( JPLItem.JUPITER, "木星", true,  "\u2643" ),
	/** 土星(&#x2644;)。 */
	SATURN( JPLItem.SATURN, "土星", true,  "\u2644" ),
	/** 天王星(&#x2645;)。 */
	URANUS( JPLItem.URANUS, "天王星", true,  "\u2645" ),
	/** 海王星(&#x2646;)。 */
	NEPTUNE( JPLItem.NEPTUNE, "海王星", true,  "\u2646" ),
	/** 冥王星(&#x2647;)。 */
	PLUTO( JPLItem.PLUTO, "冥王星", true,  "\u2647" );

	JPLItem	item;
	private String	jp_name;
	private boolean body_f;
	private String code;
	private Planet( JPLItem p, String jp, boolean b_f , String c) {
		item = p;
		jp_name = jp;
		body_f = b_f;
		code = c;
	}
	/**
	 * 惑星の日本語名を返します。
	 * @return 惑星名。
	 */
	public String getJpName() {
		return jp_name;
	}
	/**
	 * 天体であるか？<br>
	 * これが false のものは重力中心点です。
	 * @return true:天体、false:天体ではない。
	 */
	public boolean isBody() {
		return body_f;
	}

	/**
	 * UTF環境で表示可能なシンボルを表す文字を返します。
	 * 天体のみで、重力中心点のシンボルはありません。
	 * @return 「&#x2609;」のような文字列。
	 */
	public String getSymbol() { return code; }
}

