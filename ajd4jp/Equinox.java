/*
 * AJD4JP
 * Copyright (c) 2011-2021  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp;

import java.math.BigDecimal;
import java.time.ZoneId;

import ajd4jp.iso.Year;
import ajd4jp.orrery.Angle;
import ajd4jp.orrery.ETD;
import ajd4jp.orrery.Planet;
import ajd4jp.orrery.Pole;
import ajd4jp.util.AjdFactory;
import ajd4jp.util.Calc;

/**
 * 二十四節季演算。<br>
 * まず、太陽黄経は太陽の位置を表す座標の値で、春分点を原点とした
 * 黄道を基準に測った経度で、東回りに、0°～360°の値をとります。<br>
 * 次に、二十四節季とは太陽黄経を15°単位で区切ったものです。
 * 二十四節季と太陽黄経の対応は下表のようになります。<br>
 * 特に春分と秋分は日本での祝日にあたり、年によりその日が異なります。
 * 国立天文台の情報に基づき毎年閣議決定されていますが、天文学上の春分・秋分点と
 * 同日を必ずしも祝日に決定するわけではありません。
 * しかし、それ以外の日を祝日とした例は今のところありません(2016年現在)。<br>
 * {@link Holiday}は、このクラスの演算結果を元に、春分の日と秋分の日を
 * 決定しています。<br>
 * {@link LunisolarYear}は、このクラスの演算結果を元に旧暦を生成します。<br>
 * {@link STCD}は、このクラスの演算結果を元に生成されます。
<TABLE border="1">
<caption>黄経と二十四節季</caption>
  <TBODY>
    <TR>
      <TH>黄経</TH>
      <TH>名称</TH>
      <TH>黄経</TH>
      <TH>名称</TH>
      <TH>黄経</TH>
      <TH>名称</TH>
      <TH>黄経</TH>
      <TH>名称</TH>
    </TR>
    <TR>
      <TD align="right" style="background-color:#ffff00;">0</TD>
      <TD style="background-color:#ffff00;">春分</TD>
      <TD align="right">90</TD>
      <TD>夏至</TD>
      <TD align="right" style="background-color:#ffff00;">180</TD>
      <TD style="background-color:#ffff00;">秋分</TD>
      <TD align="right">270</TD>
      <TD>冬至</TD>
    </TR>
    <TR>
      <TD align="right">15</TD>
      <TD>清明</TD>
      <TD align="right">105</TD>
      <TD>小暑</TD>
      <TD align="right">195</TD>
      <TD>寒露</TD>
      <TD align="right">285</TD>
      <TD>小寒</TD>
    </TR>
    <TR>
      <TD align="right">30</TD>
      <TD>穀雨</TD>
      <TD align="right">120</TD>
      <TD>大暑</TD>
      <TD align="right">210</TD>
      <TD>霜降</TD>
      <TD align="right">300</TD>
      <TD>大寒</TD>
    </TR>
    <TR>
      <TD align="right">45</TD>
      <TD>立夏</TD>
      <TD align="right">135</TD>
      <TD>立秋</TD>
      <TD align="right">225</TD>
      <TD>立冬</TD>
      <TD align="right">315</TD>
      <TD>立春</TD>
    </TR>
    <TR>
      <TD align="right">60</TD>
      <TD>小満</TD>
      <TD align="right">150</TD>
      <TD>処暑</TD>
      <TD align="right">240</TD>
      <TD>小雪</TD>
      <TD align="right">330</TD>
      <TD>雨水</TD>
    </TR>
    <TR>
      <TD align="right">75</TD>
      <TD>芒種</TD>
      <TD align="right">165</TD>
      <TD>白露</TD>
      <TD align="right">255</TD>
      <TD>大雪</TD>
      <TD align="right">345</TD>
      <TD>啓蟄</TD>
    </TR>
  </TBODY>
</TABLE>
 */
public class Equinox {
	private Equinox() {
	}

	/**
	 * 0。春分点。
	 */
	public static final int VERNAL = 0;
	/**
	 * 90。夏至。
	 */
	public static final int SUMMER = 90;
	/**
	 * 180。秋分点。
	 */
	public static final int AUTUMNAL = 180;
	/**
	 * 270。冬至
	 */
	public static final int WINTER = 270;

	private static final BigDecimal[] sun_k = new BigDecimal[] {
			new BigDecimal("31557"), new BigDecimal("29930"),
			new BigDecimal("2281"), new BigDecimal("155"),
			new BigDecimal("33718"), new BigDecimal("9038"),
			new BigDecimal("3035"), new BigDecimal("65929"),
			new BigDecimal("22519"), new BigDecimal("45038"),
			new BigDecimal("445267"), new BigDecimal("19"),
			new BigDecimal("32964"), new BigDecimal("71998.1"),
			new BigDecimal("35999.05"), new BigDecimal("35999.05"),
			new BigDecimal("0"), new BigDecimal("0"),
	};
	private static final BigDecimal[] sun_o = new BigDecimal[] {
			new BigDecimal("161"), new BigDecimal("48"),
			new BigDecimal("221"), new BigDecimal("118"),
			new BigDecimal("316"), new BigDecimal("64"),
			new BigDecimal("110"), new BigDecimal("45"),
			new BigDecimal("352"), new BigDecimal("254"),
			new BigDecimal("208"), new BigDecimal("159"),
			new BigDecimal("158"), new BigDecimal("265.1"),
			new BigDecimal("267.52"), new BigDecimal("267.52"),
			new BigDecimal("0"), new BigDecimal("0"),
	};
	static final BigDecimal R_180 = new BigDecimal(-180),
			R180 = new BigDecimal(180),
			R360 = new BigDecimal(360);

	private static BigDecimal edit(BigDecimal r) {
		while (true) {
			if (r.compareTo(R_180) < 0) {
				r = r.add(R360);
				continue;
			}
			if (r.compareTo(R180) >= 0) {
				r = r.subtract(R360);
				continue;
			}
			break;
		}
		return r;
	}

	private static final BigDecimal E2451545 = new BigDecimal(2451545),
			E36525 = new BigDecimal(36525);

	private static final BigDecimal[] sun_a = new BigDecimal[] {
			new BigDecimal("0.0004"), new BigDecimal("0.0004"),
			new BigDecimal("0.0005"), new BigDecimal("0.0005"),
			new BigDecimal("0.0006"), new BigDecimal("0.0007"),
			new BigDecimal("0.0007"), new BigDecimal("0.0007"),
			new BigDecimal("0.0013"), new BigDecimal("0.0015"),
			new BigDecimal("0.0018"), new BigDecimal("0.0018"),
			new BigDecimal("0.0020"), new BigDecimal("0.0200"),
			new BigDecimal("-0.0048"), new BigDecimal("1.9147"),
			new BigDecimal("36000.7695"), new BigDecimal("280.4659")
	};

	private static BigDecimal getOrigin(BigDecimal jd) {
		return Calc.div(jd.subtract(E2451545), E36525);
	}

	static BigDecimal getSun(BigDecimal jd) {
		try {
			ETD etd = new ETD(new AJD(jd));
			Pole.PL pl = Pole.plot(etd, Planet.SUN, Pole.Plane.ECLIPTIC).get(Angle.Unit.DEGREE60, Angle.Unit.DEGREE60);
			return edit(pl.lon.convert(Angle.Unit.DEGREE).getTop());
		} catch (Exception e) {
		}

		BigDecimal t = getOrigin(jd);
		BigDecimal ret = BigDecimal.ZERO;

		for (int i = 0; i < sun_a.length; i++) {
			BigDecimal in = edit(sun_k[i].multiply(t).add(sun_o[i]));
			BigDecimal a = null;
			switch (i) {
			case 14:
			case 16:
				a = sun_a[i].multiply(t);
				break;
			default:
				a = sun_a[i];
				break;
			}
			if (in.compareTo(BigDecimal.ZERO) == 0)
				ret = ret.add(a);
			else
				ret = ret.add(a.multiply(Calc.cos(in)));
		}
		return edit(ret);
	}

	private static final BigDecimal E60 = new BigDecimal(60),
			E24 = new BigDecimal(24),
			E365_2 = new BigDecimal("365.2");
	private static final BigDecimal SUN = Calc.div(E365_2, R360),
			SUN5 = Calc.div(Calc.div(Calc.div(new BigDecimal("0.5"), E60), E60), E24),
			SUN_5 = Calc.div(Calc.div(Calc.div(new BigDecimal("-0.5"), E60), E60), E24);

	private static BigDecimal getEquinox(BigDecimal seed, BigDecimal r) {
		while (true) {
			BigDecimal sun = getSun(seed).subtract(r);
			if (sun.compareTo(R180) >= 0)
				sun = R360.subtract(sun);
			BigDecimal t = sun.multiply(SUN);
			seed = seed.subtract(t);
			if (t.compareTo(SUN5) < 0 && t.compareTo(SUN_5) > 0)
				break;
		}
		return seed;
	}

	private static final BigDecimal E30 = new BigDecimal(30),
			E4 = new BigDecimal(4);

	/**
	 * 太陽黄経に対応する日時を返します。
	 * @param yyyy 西暦年。
	 * @param ang 太陽黄経。
	 * @return 日時。
	 */
	public static AJD getAJD(Year yyyy, Angle ang) {
		return getAJD(yyyy.getAjdYear(), ang, yyyy.getZoneId());
	}
	/**
	 * 太陽黄経に対応する日時を返します。
	 * @param yyyy 西暦年。0を指定すると例外が発生します。
	 * @param ang 太陽黄経。
	 * @return 日時。
	 * @throws AJDException 不正な年。
	 */
	public static AJD getAJD(int yyyy, Angle ang) throws AJDException {
		return getAJD(yyyy, ang.convert(Angle.Unit.DEGREE).getTop(), AJD.OFFSET);
	}
	/**
	 * 太陽黄経に対応する日時を返します。
	 * @param yyyy 西暦年。0を指定すると例外が発生します。
	 * @param ang 太陽黄経。
	 * @param zone タイムゾーン。
	 * @return 日時。
	 * @throws AJDException 不正な年。
	 */
	public static AJD getAJD(int yyyy, Angle ang, ZoneId zone) throws AJDException {
		return getAJD(yyyy, ang.convert(Angle.Unit.DEGREE).getTop(), zone);
	}

	/**
	 * 太陽黄経に対応する日時を返します。
	 * @param yyyy 西暦年。
	 * @param ang 太陽黄経。単位は度。
	 * @return 日時。
	 */
	public static AJD getAJD(Year yyyy, Number ang) {
		return getAJD(yyyy.getAjdYear(), ang, yyyy.getZoneId());
	}
	/**
	 * 太陽黄経に対応する日時を返します。
	 * @param yyyy 西暦年。0を指定すると例外が発生します。
	 * @param ang 太陽黄経。単位は度。
	 * @return 日時。
	 * @throws AJDException 不正な年。
	 */
	public static AJD getAJD(int yyyy, Number ang) throws AJDException {
		return getAJD(yyyy, ang, AJD.OFFSET);
	}
	/**
	 * 太陽黄経に対応する日時を返します。
	 * @param yyyy 西暦年。0を指定すると例外が発生します。
	 * @param ang 太陽黄経。単位は度。
	 * @param zone タイムゾーン。
	 * @return 日時。
	 * @throws AJDException 不正な年。
	 */
	public static AJD getAJD(int yyyy, Number ang, ZoneId zone) throws AJDException {
		AJD.check(yyyy, 1);
		int org_y = yyyy;
		if (yyyy < 0)
			yyyy++;
		int mm;
		BigDecimal angle = new BigDecimal(ang.toString());

		angle = edit(angle);
		mm = Calc.div(angle, E30).add(E4).intValue();
		if (mm < 1)
			mm += 12;
		AJD ret = AjdFactory.makeAJD(yyyy, mm, 1, 0, 0, 0, zone);
		ret = AjdFactory.makeAJD(getEquinox(ret.getAJD(), angle), zone);
		while (true) {
			if (ret.getYear() == org_y)
				break;
			ret = AjdFactory.makeAJD(getEquinox(ret.getAJD().add(E365_2), angle), zone);
		}
		return ret;
	}

	private static final BigDecimal[] moon_k = new BigDecimal[] {
			new BigDecimal("2322131.0"), new BigDecimal("4067.0"),
			new BigDecimal("549197.0"), new BigDecimal("1808933.0"),
			new BigDecimal("349472.0"), new BigDecimal("381404.0"),
			new BigDecimal("958465.0"), new BigDecimal("12006.0"),
			new BigDecimal("39871.0"), new BigDecimal("509131.0"),
			new BigDecimal("1745069.0"), new BigDecimal("1908795.0"),
			new BigDecimal("2258267.0"), new BigDecimal("111869.0"),
			new BigDecimal("27864.0"), new BigDecimal("485333.0"),
			new BigDecimal("405201.0"), new BigDecimal("790672.0"),
			new BigDecimal("1403732.0"), new BigDecimal("858602.0"),
			new BigDecimal("1920802.0"), new BigDecimal("1267871.0"),
			new BigDecimal("1856938.0"), new BigDecimal("401329.0"),
			new BigDecimal("341337.0"), new BigDecimal("71998.0"),
			new BigDecimal("990397.0"), new BigDecimal("818536.0"),
			new BigDecimal("922466.0"), new BigDecimal("99863.0"),
			new BigDecimal("1379739.0"), new BigDecimal("918399.0"),
			new BigDecimal("1934.0"), new BigDecimal("541062.0"),
			new BigDecimal("1781068.0"), new BigDecimal("133.0"),
			new BigDecimal("1844932.0"), new BigDecimal("1331734.0"),
			new BigDecimal("481266.0"), new BigDecimal("31932.0"),
			new BigDecimal("926533.0"), new BigDecimal("449334.0"),
			new BigDecimal("826671.0"), new BigDecimal("1431597.0"),
			new BigDecimal("1303870.0"), new BigDecimal("489205.0"),
			new BigDecimal("1443603.0"), new BigDecimal("75870.0"),
			new BigDecimal("513197.9"), new BigDecimal("445267.1"),
			new BigDecimal("441199.8"), new BigDecimal("854535.2"),
			new BigDecimal("1367733.1"), new BigDecimal("377336.3"),
			new BigDecimal("63863.5"), new BigDecimal("966404.0"),
			new BigDecimal("35999.0"), new BigDecimal("954397.7"),
			new BigDecimal("890534.2"), new BigDecimal("413335.3"),
			new BigDecimal("477198.86"), new BigDecimal("0"), new BigDecimal("0")
	};

	private static final BigDecimal[] moon_o = new BigDecimal[] {
			new BigDecimal("191.0"), new BigDecimal("70.0"),
			new BigDecimal("220.0"), new BigDecimal("58.0"),
			new BigDecimal("337.0"), new BigDecimal("354.0"),
			new BigDecimal("340.0"), new BigDecimal("187.0"),
			new BigDecimal("223.0"), new BigDecimal("242.0"),
			new BigDecimal("24.0"), new BigDecimal("90.0"),
			new BigDecimal("156.0"), new BigDecimal("38.0"),
			new BigDecimal("127.0"), new BigDecimal("186.0"),
			new BigDecimal("50.0"), new BigDecimal("114.0"),
			new BigDecimal("98.0"), new BigDecimal("129.0"),
			new BigDecimal("186.0"), new BigDecimal("249.0"),
			new BigDecimal("152.0"), new BigDecimal("274.0"),
			new BigDecimal("16.0"), new BigDecimal("85.0"),
			new BigDecimal("357.0"), new BigDecimal("151.0"),
			new BigDecimal("163.0"), new BigDecimal("122.0"),
			new BigDecimal("17.0"), new BigDecimal("182.0"),
			new BigDecimal("145.0"), new BigDecimal("259.0"),
			new BigDecimal("21.0"), new BigDecimal("29.0"),
			new BigDecimal("56.0"), new BigDecimal("283.0"),
			new BigDecimal("205.0"), new BigDecimal("107.0"),
			new BigDecimal("323.0"), new BigDecimal("188.0"),
			new BigDecimal("111.0"), new BigDecimal("315.0"),
			new BigDecimal("246.0"), new BigDecimal("142.0"),
			new BigDecimal("52.0"), new BigDecimal("41.0"),
			new BigDecimal("222.5"), new BigDecimal("27.9"),
			new BigDecimal("47.4"), new BigDecimal("148.2"),
			new BigDecimal("280.7"), new BigDecimal("13.2"),
			new BigDecimal("124.2"), new BigDecimal("276.5"),
			new BigDecimal("87.53"), new BigDecimal("179.93"),
			new BigDecimal("145.7"), new BigDecimal("10.74"),
			new BigDecimal("44.963"), new BigDecimal("0"), new BigDecimal("0")
	};

	private static final BigDecimal[] moon_a = new BigDecimal[] {
			new BigDecimal("0.0003"), new BigDecimal("0.0003"),
			new BigDecimal("0.0003"), new BigDecimal("0.0003"),
			new BigDecimal("0.0003"), new BigDecimal("0.0003"),
			new BigDecimal("0.0003"), new BigDecimal("0.0004"),
			new BigDecimal("0.0004"), new BigDecimal("0.0005"),
			new BigDecimal("0.0005"), new BigDecimal("0.0005"),
			new BigDecimal("0.0006"), new BigDecimal("0.0006"),
			new BigDecimal("0.0007"), new BigDecimal("0.0007"),
			new BigDecimal("0.0007"), new BigDecimal("0.0007"),
			new BigDecimal("0.0008"), new BigDecimal("0.0009"),
			new BigDecimal("0.0011"), new BigDecimal("0.0012"),
			new BigDecimal("0.0016"), new BigDecimal("0.0018"),
			new BigDecimal("0.0021"), new BigDecimal("0.0021"),
			new BigDecimal("0.0021"), new BigDecimal("0.0022"),
			new BigDecimal("0.0023"), new BigDecimal("0.0024"),
			new BigDecimal("0.0026"), new BigDecimal("0.0027"),
			new BigDecimal("0.0028"), new BigDecimal("0.0037"),
			new BigDecimal("0.0038"), new BigDecimal("0.0040"),
			new BigDecimal("0.0040"), new BigDecimal("0.0040"),
			new BigDecimal("0.0050"), new BigDecimal("0.0052"),
			new BigDecimal("0.0068"), new BigDecimal("0.0079"),
			new BigDecimal("0.0085"), new BigDecimal("0.0100"),
			new BigDecimal("0.0107"), new BigDecimal("0.0110"),
			new BigDecimal("0.0125"), new BigDecimal("0.0154"),
			new BigDecimal("0.0304"), new BigDecimal("0.0347"),
			new BigDecimal("0.0409"), new BigDecimal("0.0458"),
			new BigDecimal("0.0533"), new BigDecimal("0.0571"),
			new BigDecimal("0.0588"), new BigDecimal("0.1144"),
			new BigDecimal("0.1851"), new BigDecimal("0.2136"),
			new BigDecimal("0.6583"), new BigDecimal("1.2740"),
			new BigDecimal("6.2888"), new BigDecimal("218.3162"),
			new BigDecimal("481267.8809")
	};

	private static BigDecimal getMoon(BigDecimal jd) {
		try {
			ETD etd = new ETD(new AJD(jd));
			Pole.PL pl = Pole.plot(etd, Planet.MOON, Pole.Plane.ECLIPTIC).get(Angle.Unit.DEGREE60, Angle.Unit.DEGREE60);
			return edit(pl.lon.convert(Angle.Unit.DEGREE).getTop());
		} catch (Exception e) {
		}

		BigDecimal t = getOrigin(jd);
		BigDecimal ret = BigDecimal.ZERO;

		for (int i = 0; i < moon_a.length; i++) {
			BigDecimal in = edit(moon_k[i].multiply(t).add(moon_o[i]));
			BigDecimal a = null;
			switch (i) {
			case 62:
				a = moon_a[i].multiply(t);
				break;
			default:
				a = moon_a[i];
				break;
			}
			if (in.compareTo(BigDecimal.ZERO) == 0)
				ret = ret.add(a);
			else
				ret = ret.add(a.multiply(Calc.cos(in)));
		}
		return edit(ret);
	}

	private static final BigDecimal M28 = Calc.div(new BigDecimal(360), 28);

	/**
	 * 月相の取得。月の満ち欠け(朔望月)を 0 ～ 28(28未満)で表します。<br>
	 * 月相0の時間を含む日が、旧暦での朔日(ついたち＝月の１日目)にあたります。<br>
	 * また、月相は海の潮汐の指標になります。以下の表はその一例です。
	<table>
	<caption>月相と潮汐</caption>
	<tbody>
	<tr>
	  <th colspan="3">月相</th>
	  <th>潮汐</th>
	</tr>
	<tr>
	  <td>27.1</td>
	  <td>～</td>
	  <td>2.8</td>
	  <td>大潮</td>
	</tr>
	<tr>
	  <td>2.8</td>
	  <td>～</td>
	  <td>5.6</td>
	  <td>中潮</td>
	</tr>
	<tr>
	  <td>5.6</td>
	  <td>～</td>
	  <td>8.4</td>
	  <td>小潮</td>
	</tr>
	<tr>
	  <td>8.4</td>
	  <td>～</td>
	  <td>9.3</td>
	  <td>長潮</td>
	</tr>
	<tr>
	  <td>9.3</td>
	  <td>～</td>
	  <td>10.3</td>
	  <td>中潮</td>
	</tr>
	<tr>
	  <td>10.3</td>
	  <td>～</td>
	  <td>16.8</td>
	  <td>大潮</td>
	</tr>
	<tr>
	  <td>16.8</td>
	  <td>～</td>
	  <td>19.6</td>
	  <td>中潮</td>
	</tr>
	<tr>
	  <td>19.6</td>
	  <td>～</td>
	  <td>22.4</td>
	  <td>小潮</td>
	</tr>
	<tr>
	  <td>22.4</td>
	  <td>～</td>
	  <td>23.3</td>
	  <td>長潮</td>
	</tr>
	<tr>
	  <td>23.3</td>
	  <td>～</td>
	  <td>24.3</td>
	  <td>若潮</td>
	</tr>
	<tr>
	  <td>24.3</td>
	  <td>～</td>
	  <td>27.1</td>
	  <td>中潮</td>
	</tr>
	</tbody>
	</table>
	 * @param day 日時。
	 * @return 月相。0:朔/新月 ～ 7:上弦 ～ 14:望/満月 ～ 21:下弦。
	 */
	public static BigDecimal getMoonPhase(Day day) {
		BigDecimal moon = getMoon(day.getAJD()).subtract(getSun(day.getAJD()));
		while (true) {
			if (moon.compareTo(BigDecimal.ZERO) < 0) {
				moon = moon.add(R360);
				continue;
			}
			if (moon.compareTo(R360) >= 0) {
				moon = moon.subtract(R360);
				continue;
			}
			break;
		}
		return Calc.div(moon, M28);
	}
}
