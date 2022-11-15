/*
 * AJD4JP
 * Copyright (c) 2011-2021  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;

import ajd4jp.iso.Year;
import ajd4jp.util.AjdFactory;
import ajd4jp.util.Calc;

class Saku implements java.io.Serializable {
	private static final long serialVersionUID = 1;
	AJD ajd;
	int sun;
	BigDecimal moon;
	boolean start_f = false;
	boolean leap_f = false;
	int year;
	int mon;

	Saku() {
	}

	public String toString() {
		return String.format("%d/%d(%s)[sun:%d/moon:%s]: %s", year, mon, leap_f, sun, moon, ajd);
	}
}

/**
 * 旧暦の一年間を表します。旧暦取得処理の起点となるクラスです。<br>
 * 旧暦とは明治5(1872)年まで使用されていた天保暦を指し、太陰太陽暦に
 * 分類されます。<br>
 * ただし、当時の天保暦は経験的な定数値を使用していたのに対し、現在一般に流通している
 * いわゆる「旧暦」は、天文学的に正確な値を使用しています。
 * また、当時は京都の経度に基づいた時刻を使用していますが、現在は日本標準時の時刻を
 * 使用します。
 * そのため、当時の天保暦と現在の「旧暦」では日付が異なる場合があります。<br>
 * 本クラスも、現在の「旧暦」に基づいた処理を行います。
 * また、太陰太陽暦(旧暦)は以下の点で太陽暦(新暦)とは異なるため、本クラスを扱う場合、
 * 注意して下さい。
 * <ul>
 * <li>旧暦の１ヶ月の長さは、大の月(30日)と小の月(29日)のどちらかです。
 * <li>旧暦では季節のずれを調整するために、数年に１度、閏月を設けます。<br>
 * 通常、一年は12ヶ月ですが、閏月が入ると13ヶ月になります。<br>
 * 1月、2月、3月、閏3月、4月 ... 12月<br>
 * のように、同じ数字が続けて２回登場します。
 * </ul>
 * 本クラスでは、冬至(11月)のみから月を決定します。
 * 本来は、春分、夏至、秋分も月の決定基準ですが、処理として実装していません。
 * 閏月が必要な場合、冬至の次に、最初に登場する中気(*1)を含まない月を採用します。
 * <dl>
 * <dt>*1 中気
 * <dd>二十四節気の、
 * 雨水・春分・穀雨・小満・夏至・大暑・処暑・秋分・霜降・小雪・冬至・大寒のこと。
 * </dl>
 */
public class LunisolarYear implements Comparable<LunisolarYear>, java.io.Serializable {
	private static final long serialVersionUID = 1;
	private static final int START = Equinox.WINTER;
	private static final int BASE_MON = 11;
	private static HashMap<Year, LunisolarYear> map = new HashMap<Year, LunisolarYear>();

	private static boolean isHit(int start, int end) {
		if (start > end)
			end += 360;
		for (int i = start / 30 * 30; i < end; i += 30) {
			if (start <= i && i < end)
				return true;
		}
		return false;
	}

	private static Saku[] countMon(int year, Saku[] saku, int start, int end) {
		int mm = BASE_MON;
		int lim = 13;
		if (!saku[start].start_f) {
			mm++;
			lim--;
		}

		ArrayList<Saku> list = new ArrayList<Saku>();
		for (int cnt = start; cnt < end; cnt++) {
			if (saku[cnt].start_f) {
				list.add(saku[cnt]);
			}
		}
		saku = list.toArray(new Saku[0]);
		if (saku.length < lim) {
			for (int i = 0; i < saku.length; i++) {
				saku[i].year = year;
				saku[i].mon = mm;
				mm++;
				if (mm > 12) {
					mm = 1;
					year++;
					if (year == 0) {
						year++;
					}
				}
			}
			return saku;
		}

		mm--;
		int i = 1;
		boolean set_f = false;
		for (; i < saku.length; i++) {
			if (set_f || isHit(saku[i - 1].sun, saku[i].sun)) {
				mm++;
				if (mm > 12) {
					mm = 1;
					year++;
					if (year == 0)
						year++;
				}
			} else
				saku[i - 1].leap_f = set_f = true;
			saku[i - 1].year = year;
			saku[i - 1].mon = mm;
		}
		saku[i - 1].year = year;
		saku[i - 1].mon = BASE_MON - 1;
		saku[i - 1].leap_f = saku[i - 1].mon == saku[i - 2].mon;
		return saku;
	}

	private static int getSun(AJD ajd) {
		BigDecimal sun = Equinox.getSun(ajd.getAJD());
		if (sun.compareTo(BigDecimal.ZERO) < 0)
			sun = sun.add(Equinox.R360);
		int ret = (int) Calc.up(sun);
		return ret;
	}

	private static LunisolarYear get11(AJD ajd) {
		int sun = getSun(ajd);
		int year = ajd.getYear();
		if (sun > START) {
			year--;
			if (year == 0)
				year--;
		}
		int yyyy = year - 1;
		AJD[] toji = new AJD[4];
		if (yyyy == 0)
			yyyy--;
		for (int i = 0; i < toji.length; i++) {
			toji[i] = STCD.getStart(Year.fromAJD(yyyy, ajd.getZoneId()), START).trim();
			yyyy++;
			if (yyyy == 0)
				yyyy++;
		}
		int day = new Span(toji[0], toji[3]).getDayPart();
		Saku[] saku = new Saku[day + 1];
		for (int i = 0; i < saku.length; i++)
			saku[i] = new Saku();
		saku[0].ajd = toji[0];
		saku[0].sun = START;
		saku[day].ajd = toji[3];
		saku[day].sun = START;
		ajd = ajd.trim();

		saku[0].moon = Equinox.getMoonPhase(toji[0]);
		day = 1;
		while (day < saku.length) {
			saku[day].ajd = toji[0].addDay(day);
			saku[day].sun = getSun(saku[day].ajd);
			saku[day].moon = Equinox.getMoonPhase(saku[day].ajd);
			int pre = day - 1;
			if (saku[pre].moon == null) {
				day++;
				continue;
			}
			if (saku[day].moon.compareTo(saku[pre].moon) < 0) {
				if (saku[day].moon.compareTo(BigDecimal.ZERO) == 0) {
					saku[day].start_f = true;
				} else {
					saku[pre].start_f = true;
				}
			}

			int s_sub = 30;
			for (int i = 1; i <= 2; i++) {
				int cmp = toji[i].compareTo(saku[day].ajd);
				if (cmp < 0) {
					continue;
				}
				s_sub = new Span(toji[i], saku[day].ajd).getDayPart();
				s_sub--;
				if (s_sub < 1) {
					s_sub = 1;
				}
				break;
			}
			int m_sub = 27 - (int) Calc.cut(saku[day].moon);
			if (m_sub < 1)
				m_sub = 1;
			day += (s_sub > m_sub ? m_sub : s_sub);
		}

		int[] mid = new int[2];
		day = 1;
		for (int i = 0; i < mid.length; i++) {
			for (; day < saku.length; day++) {
				if (toji[i + 1].equals(saku[day].ajd)) {
					for (int j = day; j >= 0; j--) {
						if (saku[j].start_f) {
							mid[i] = j;
							break;
						}
					}
					break;
				}
			}
		}

		Saku[][] line = new Saku[][] {
				countMon((year - 1) == 0 ? -1 : year - 1, saku, 0, mid[0]),
				countMon(year, saku, mid[0], mid[1]),
				countMon((year + 1) == 0 ? 1 : year + 1, saku, mid[1], saku.length - 1)
		};
		ArrayList<Saku> list = new ArrayList<Saku>();
		for (int i = 0; i < line[0].length; i++) {
			if (line[0][i].year == year)
				list.add(line[0][i]);
		}
		for (int i = 0; i < line[1].length; i++) {
			list.add(line[1][i]);
		}
		for (int i = 0; i < line[2].length; i++) {
			if (line[2][i].year != year)
				break;
			list.add(line[2][i]);
		}
		int cnt = list.size();
		for (int i = 1; i < cnt; i++) {
			Saku s = list.get(i - 1);
			if (s.ajd.compareTo(ajd) <= 0 &&
					list.get(i).ajd.compareTo(ajd) > 0) {
				year = s.year;
				break;
			}
		}
		LunisolarYear ls = new LunisolarYear(year, list);
		map.put(Year.fromAJD(year, ajd.getZoneId()), ls);
		return ls;
	}

	/**
	 * 旧暦の年の取得。
	 * 新暦と旧暦は年始がずれるため、同じ年でない場合があります。
	 * @param hint この日付が含まれる年を返す。
	 * @return hintが含まれる年。
	 */
	public static LunisolarYear getLunisolarYear(AJD hint) {
		int year = hint.getYear() - 1;
		if (year == 0)
			year = -1;
		synchronized (map) {
			for (int i = 0; i < 3; i++, year++) {
				if (year == 0)
					year++;
				LunisolarYear y = map.get(Year.fromAJD(year, hint.getZoneId()));
				if (y != null) {
					if (y.getLSCD(hint) != null)
						return y;
				}
			}
			return get11(hint);
		}
	}

	/**
	 * 旧暦の年を取得。指定年のものを返します。<br>
	 * ここで指定する年は、旧暦での年です。
	 * 旧暦と新暦では年始年末の年が一致しません。
	 * @param year 年。
	 * @return yearの年。
	 */
	public static LunisolarYear getLunisolarYear(Year year) {
		synchronized (map) {
			LunisolarYear y = map.get(year);
			if (y != null)
				return y;
			return get11(AjdFactory.makeAJD(year.getAjdYear(), 6, 1, year.getZoneId()));
		}
	}

	/**
	 * 旧暦の年を取得。指定年のものを返します。<br>
	 * ここで指定する年は、旧暦での年です。
	 * 旧暦と新暦では年始年末の年が一致しません。
	 * @param year 年。0は指定できません。
	 * @return yearの年。
	 * @throws AJDException 不正な年。
	 */
	public static LunisolarYear getLunisolarYear(int year) throws AJDException {
		return getLunisolarYear(Year.fromAJD(year, AJD.OFFSET));
	}

	/**
	 * 旧暦の年を取得。指定年のものを返します。<br>
	 * ここで指定する年は、旧暦での年です。
	 * 旧暦と新暦では年始年末の年が一致しません。
	 * @param year 年。0は指定できません。
	 * @param zone タイムゾーン。
	 * @return yearの年。
	 * @throws AJDException 不正な年。
	 */
	public static LunisolarYear getLunisolarYear(int year, ZoneId zone) throws AJDException {
		return getLunisolarYear(Year.fromAJD(year, zone));
	}

	private int yy;
	private Saku[] month;
	private Saku limit;

	private LunisolarYear(int y, ArrayList<Saku> list) {
		yy = y;
		int cnt = list.size();
		int start = 0;
		ArrayList<Saku> mine = new ArrayList<Saku>();
		for (; start < cnt; start++) {
			if (list.get(start).year == y)
				break;
		}
		for (; start < cnt; start++) {
			Saku s = list.get(start);
			if (s.year != y) {
				limit = s;
				break;
			}
			mine.add(s);
		}
		month = mine.toArray(new Saku[0]);
		if (limit == null) {
			int ny = y + 1;
			if (ny == 0) {
				ny = 1;
			}
			LunisolarYear next = null;
			try {
				next = getLunisolarYear(ny);
			} catch (AJDException e) {
			}
			limit = next.month[0];
		}
	}

	/**
	 * 旧暦の年を返します。
	 * @return 年。
	 */
	public int getYear() {
		return yy;
	}

	/**
	 * 旧暦の日付を取得。
	 * @param dd 検索対象。時間も引き継ぎます。
	 * @return ddに対応する旧暦の日付。年の範囲外であればnull。
	 */
	public LSCD getLSCD(AJD dd) {
		AJD ajd = dd.trim();
		if (ajd.compareTo(month[0].ajd) < 0) {
			return null;
		}
		if (ajd.compareTo(limit.ajd) >= 0) {
			return null;
		}
		int i = 1;
		for (; i < month.length; i++) {
			if (ajd.compareTo(month[i].ajd) < 0)
				break;
		}
		return new LSCD(
				month[i - 1].year, month[i - 1].leap_f, month[i - 1].mon,
				month[i - 1].ajd, dd);
	}

	/**
	 * 月の総数を返します。
	 * 旧暦の1年は、12ヶ月の場合と13ヶ月(閏月を含む)の場合があります。
	 * @return 月の総数。
	 */
	public int getMonthCount() {
		return month.length;
	}

	/**
	 * 閏月の月を返します。
	 * @return -1:平年、1～12:閏月。
	 */
	public int getLeapMonth() {
		for (int i = 0; i < month.length; i++) {
			if (month[i].leap_f)
				return month[i].mon;
		}
		return -1;
	}

	private Saku[] getSaku(int mm, boolean leap_f) throws AJDException {
		for (int i = 0; i < month.length; i++) {
			if (month[i].leap_f == leap_f && month[i].mon == mm) {
				Saku next = (i == month.length - 1) ? limit : month[i + 1];
				// [0]当月、[1]翌月
				return new Saku[] {
						month[i], next
				};
			}
		}
		throw new AJDException("指定月が存在しません。");
	}

	/**
	 * 月の最終日(晦日 つごもり)を返します。
	 * 指定月が存在しないと例外が発生します。
	 * @param mm 月。1～12。
	 * @param leap_f true:閏月、false:平月。
	 * @return その月の最終日。
	 * @throws AJDException 不正な月。
	 */
	public int getLastDay(int mm, boolean leap_f) throws AJDException {
		Saku[] mon = getSaku(mm, leap_f);
		return new Span(mon[0].ajd, mon[1].ajd).getDayPart();
	}

	/**
	 * 旧暦の日付を取得。
	 * 指定月が存在しない、または指定日が存在しない場合、例外が発生します。<br>
	 * 時間は 00:00:00 になります。
	 * @param mm 月。1～12。
	 * @param leap_f true:閏月、false:平月。
	 * @param dd 日。
	 * @return 旧暦の日付。
	 * @throws AJDException 不正な月日。
	 */
	public LSCD getLSCD(int mm, boolean leap_f, int dd) throws AJDException {
		return getLSCD(mm, leap_f, dd, 0, 0, 0);
	}

	/**
	 * 旧暦の日付を取得。
	 * 指定月が存在しない、または指定日が存在しない場合、例外が発生します。
	 * @param mm 月。1～12。
	 * @param leap_f true:閏月、false:平月。
	 * @param dd 日。
	 * @param hh 時。
	 * @param mi 分。
	 * @param ss 秒。
	 * @return 旧暦の日付。
	 * @throws AJDException 不正な月日。
	 */
	public LSCD getLSCD(int mm, boolean leap_f, int dd, int hh, int mi, int ss) throws AJDException {
		if (dd < 1 || dd > getLastDay(mm, leap_f))
			throw new AJDException("指定日が存在しません。");
		Saku[] mon = getSaku(mm, leap_f);
		return new LSCD(yy, leap_f, mm, dd, mon[0].ajd.addDay(dd - 1).addHour(hh).addMinute(mi).addSecond(ss));
	}

	/**
	 * 旧正月(春節)の取得。指定された新暦の年内で登場する旧正月を返します。
	 * @param yyyy 新暦の年。
	 * @return 旧正月の日。
	 */
	public static AJD getNewYear(Year yyyy) {
		return LunisolarYear.getLunisolarYear(yyyy).getLSCD(1, false, 1).toAJD();
	}

	/**
	 * 年を表す数字を返します。
	 * @return 年を文字列にしたもの。
	 */
	public String toString() {
		return Integer.toString(yy);
	}

	/**
	 * 年の比較。
	 * @param y 年。
	 * @return -1:this&lt;y(thisが過去)、0:this==y、1:this&gt;y(thisが未来)。
	 */
	public int compareTo(LunisolarYear y) {
		int ret = yy - y.yy;
		if (ret < 0)
			return -1;
		if (ret > 0)
			return 1;
		return 0;
	}

	/**
	 * ハッシュコード。年をそのまま返します。
	 * @return ハッシュコード。
	 */
	public int hashCode() {
		return yy;
	}

	/**
	 * 年の比較。
	 * @param o 比較対象。
	 * @return true:年が一致、false:年が不一致。
	 */
	public boolean equals(Object o) {
		if (o instanceof LunisolarYear)
			return yy == ((LunisolarYear) o).yy;
		return false;
	}
}
