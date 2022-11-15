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

/**
 * 節(*1)切りでの日付を表します。立春を年始とし、12ヶ月に区切ります。<br>
 * 旧暦の時代は農業で利用されていましたが、現在では占い等でしか見られません。
 * また、俳句の季語(*2)は節切りに則って季節を取ります。
 * <dl>
 * <dt>*1 節
 * <dd>二十四節気の、
 * 立春(1月)・啓蟄・清明・立夏・芒種・小暑・立秋・白露・寒露・立冬・大雪・小寒(12月)
 * のこと。節切り(節を初日とする)での月を節月といいます。<br>
 * 東洋では節で区切りますが、西洋では中気({@link LunisolarYear}参照)を初日として
 * 12ヶ月に区切ります。
 * 西洋占星術での12宮がそれにあたり、春分が牡羊座の開始日になります。
 * <dt>*2 季語
 * <dd>新春の文字通り、かつて年始は春でしたが明治改暦以降、年始が冬になったため、
 * 四季を表す季語とは別に「新年」を表す季語のカテゴリーが設けられました。
 * </dl>
 */
public class STCD implements Day {
	private static final long serialVersionUID = 1;
	private static final int YEAR = 315;
	private static HashMap<Year, HashMap<Integer, AJD>> start_day = new HashMap<Year, HashMap<Integer, AJD>>();

	static AJD getStart(Year year, int r) {
		synchronized (start_day) {
			AJD ret = null;
			HashMap<Integer, AJD> ymap = start_day.get(year);
			if (ymap != null) { ret = ymap.get(r); }
			if (ret == null) {
				try {
					ret = Equinox.getAJD(year, r).trim();
				} catch (AJDException e) {
				}
				if (ymap == null) { start_day.put(year, ymap = new HashMap<Integer, AJD>()); }
				ymap.put(r, ret);
			}
			return ret;
		}
	}

	/**
	 * 土用。
	 */
	public enum Doyo {
		/** 冬。 */
		WINTER(315),
		/** 春。 */
		SPRING(45),
		/** 夏。 */
		SUMMER(135),
		/** 秋。 */
		AUTUMN(225);

		private int start, end;

		private Doyo(int r) {
			start = r - 18;
			end = r;
		}

		/**
		 * 土用の期間を返します。土用最終日が節分となります。
		 * @param year 取得したい西暦年。節月の年ではありません。
		 * @param filter 特定の干支のみ(例えば丑)必要な場合に指定します。
		 * 無指定ならば全ての土用を返します。
		 * @return 取得した全日程。
		 */
		public AJD[] getDoyo(Year year, EarthlyBranch... filter) {
			AJD[] ajd = new AJD[] {
					getStart(year, start), getStart(year, end)
			};
			ArrayList<AJD> list = new ArrayList<AJD>();
			for (AJD k = ajd[0]; k.compareTo(ajd[1]) < 0; k = k.addDay(1)) {
				boolean hit = filter.length == 0;
				for (EarthlyBranch f : filter) {
					if (EarthlyBranch.getDay(k) == f) {
						hit = true;
						break;
					}
				}
				if (hit) {
					list.add(k);
				}
			}
			return list.toArray(new AJD[0]);
		}

		/**
		 * 土用の期間を返します。土用最終日が節分となります。
		 * @param year 取得したい西暦年。節月の年ではありません。
		 * @param filter 特定の干支のみ(例えば丑)必要な場合に指定します。
		 * 無指定ならば全ての土用を返します。
		 * @return 取得した全日程。
		 * @exception AJDException 年の指定に誤りがある場合。
		 */
		public AJD[] getDoyo(int year, EarthlyBranch... filter) throws AJDException {
			return getDoyo(year, AJD.OFFSET, filter);
		}

		/**
		 * 土用の期間を返します。土用最終日が節分となります。
		 * @param year 取得したい西暦年。節月の年ではありません。
		 * @param zone タイムゾーン
		 * @param filter 特定の干支のみ(例えば丑)必要な場合に指定します。
		 * 無指定ならば全ての土用を返します。
		 * @return 取得した全日程。
		 * @exception AJDException 年の指定に誤りがある場合。
		 */
		public AJD[] getDoyo(int year, ZoneId zone, EarthlyBranch... filter) throws AJDException {
			return getDoyo(Year.fromAJD(year, zone), filter);
		}
	}

	private AJD ajd;
	private int yy, mm, dd;

	/**
	 * コンストラクタ。指定日と等価な、節切りでの日付を生成します。
	 * @param day 日付。
	 */
	public STCD(Day day) {
		this(day, false);
	}

	private STCD(Day day, boolean test_f) {
		ZoneId zone = day.getZoneId();
		ajd = AjdFactory.makeAJD(day.getAJD(), zone);
		yy = ajd.getYear();
		if (ajd.compareTo(getStart(ajd.toYear(), YEAR)) < 0) {
			yy--;
			if (yy <= 0)
				yy--;
		}
		int sun = getSun(ajd);
		mm = sun - YEAR;
		if (mm < 0)
			mm += 360;
		mm = mm / 30 + 1;
		sun = (mm - 1) * 30 + YEAR;
		if (sun > 360)
			sun -= 360;
		dd = new Span(getStart(ajd.toYear(), sun), ajd).getDayPart() + 1;
		if (dd > 31) {
			dd = new Span(getStart(Year.fromAJD(yy, zone), sun), ajd).getDayPart() + 1;
		}
		if (!test_f && dd > 20) {
			STCD test = new STCD(ajd.addDay(1), true);
			if (test.dd == 2) {
				yy = test.yy;
				mm = test.mm;
				dd = 1;
			}
		}
	}

	private static int getSun(AJD ajd) {
		BigDecimal sun = Equinox.getSun(ajd.getAJD());
		if (sun.compareTo(BigDecimal.ZERO) < 0)
			sun = sun.add(Equinox.R360);
		int ret = (int) Calc.cut(sun);
		ret = ret / 15 * 15;
		if ((ret % 10) == 0) {
			ret -= 15;
			if (ret < 0)
				ret += 360;
		}
		return ret;
	}

	/**
	 * ユリウス通日の比較。
	 * ユリウス通日を格納しているBigDecimalのcompareToを使用します。
	 * @param jd 比較対象。
	 * @return -1:this&lt;jd(thisが過去)、0:this==jd、1:this&gt;jd(thisが未来)。
	 */
	public int compareTo(Day jd) {
		return ajd.compareTo(jd);
	}

	/**
	 * ユリウス通日のハッシュコードを返します。
	 * @return ハッシュコード。
	 */
	public int hashCode() {
		return ajd.hashCode();
	}

	/**
	 * ユリウス通日の比較。
	 * @return true:ユリウス通日が一致、false:ユリウス通日が不一致。
	 */
	public boolean equals(Object o) {
		return ajd.equals(o);
	}

	/**
	 * 文字列化。
	 * @return yyyy/mm/dd hh:mm:ss形式のフォーマットで返します。
	 */
	public String toString() {
		return String.format("%d/%02d/%02d/ %02d:%02d:%02d", yy, mm, dd,
				getHour(), getMinute(), getSecond());
	}

	/**
	 * ユリウス通日の取得。
	 * @return ユリウス通日。
	 */
	public AJD toAJD() {
		return ajd;
	}

	/**
	 * ユリウス通日の取得。
	 * @return ユリウス通日。
	 */
	public BigDecimal getAJD() {
		return ajd.getAJD();
	}

	/**
	 * 年を返します。
	 * @return 年。
	 */
	public int getYear() {
		return yy;
	}

	/**
	 * 月を返します。
	 * @return 月。
	 */
	public int getMonth() {
		return mm;
	}

	/**
	 * 日を返します。
	 * @return 日。
	 */
	public int getDay() {
		return dd;
	}

	/**
	 * 時を返します。
	 * @return 時。
	 */
	public int getHour() {
		return ajd.getHour();
	}

	/**
	 * 分を返します。
	 * @return 分。
	 */
	public int getMinute() {
		return ajd.getMinute();
	}

	/**
	 * 秒を返します。
	 * @return 秒。
	 */
	public int getSecond() {
		return ajd.getSecond();
	}

	/**
	 * 九星。節切りで判断されます。<br>
	 * 日の九星は、以下の方法に拠ります。
	 * 陽遁は数字を増やしていくことで、陰遁は数字を減らしていくことです。
	 * <ul>
	 * <li>冬至に最も近い甲子を一白として陽遁。<br>
	 * ただし冬至の前後１日に甲午があれば、甲午を七赤として陽遁。
	 * <li>夏至に最も近い甲子を九紫として陰遁。<br>
	 * ただし夏至の前後１日に甲午があれば、甲午を三碧として陰遁。
	 * </ul>
	 */
	public enum Kyusei {
		/** 一白水星 */
		WHITE1("一白水星"),
		/** 二黒土星 */
		BLACK2("二黒土星"),
		/** 三碧木星 */
		BLUE3("三碧木星"),
		/** 四緑木星 */
		GREEN4("四緑木星"),
		/** 五黄土星 */
		YELLOW5("五黄土星"),
		/** 六白金星 */
		WHITE6("六白金星"),
		/** 七赤金星 */
		RED7("七赤金星"),
		/** 八白土星 */
		WHITE8("八白土星"),
		/** 九紫火星 */
		PURPLE9("九紫火星");

		private String name;

		private Kyusei(String n) {
			name = n;
		}

		/**
		 * 名称の取得。
		 * @return 一白水星～九紫火星。
		 */
		public String getName() {
			return name;
		}

		private static final Kyusei[] ky = values();

		private static Kyusei fix(int v) {
			v %= ky.length;
			if (v < 0)
				v += ky.length;
			return ky[v];
		}

		/**
		 * 年の九星の取得。
		 * @param date 年の取得先。立春を年の開始とします。
		 * @return その年を表す九星。
		 */
		public static Kyusei getYear(Day date) {
			date = AjdFactory.makeAJD(date.getAJD(), date.getZoneId()).trim();
			int yy = new STCD(date).getYear();
			if (yy < 0)
				yy++;
			return fix(10 - yy);
		}

		/**
		 * 月の九星の取得。
		 * @param date 月の取得先。節月を取ります。
		 * @return その月を表す九星。
		 */
		public static Kyusei getMonth(Day date) {
			date = AjdFactory.makeAJD(date.getAJD(), date.getZoneId()).trim();
			STCD stcd = new STCD(date);
			EarthlyBranch year = EarthlyBranch.getYear(stcd);
			int start = 8 - (year.getNo() % 3) * 3;
			return fix(start - stcd.getMonth());
		}

		private static class Start {
			int no;
			int r;
			AJD ajd;

			Start() {
			}
		}

		private static Start getStart(int r, AJD base) {
			HeavenlyStem h = HeavenlyStem.getDay(base);
			EarthlyBranch e = EarthlyBranch.getDay(base);
			int base_no = SexagenaryCycle.getNo(h, e);
			Start start = new Start();
			start.r = r;
			if (base_no < 29) {
				start.no = r == Equinox.SUMMER ? 8 : 0;
				start.ajd = base.addDay(-1 * base_no);
			} else if (base_no > 31) {
				start.no = r == Equinox.SUMMER ? 8 : 0;
				start.ajd = base.addDay(60 - base_no);
			} else {
				start.no = r == Equinox.SUMMER ? 2 : 6;
				start.ajd = base.addDay(30 - base_no);
			}
			return start;
		}

		private static Start getStart(Day stcd, ZoneId zone) {
			int r = Equinox.SUMMER, y = stcd.getYear() - 2;
			AJD last = null, next = null;
			for (;; r += 180) {
				if (r >= 360) {
					r -= 360;
					y++;
					if (y == 0) { y = 1; }
				}
				next = STCD.getStart(Year.fromAJD(y, zone), r);
				if (last != null) {
					if (next.compareTo(stcd) > 0)
						break;
				}
				last = next;
			}
			Start start1 = getStart((r + 180) % 360, last);
			Start start2 = getStart(r, next);
			if (start1.ajd.compareTo(stcd) > 0) {
				return getStart(AjdFactory.makeAJD(stcd.getAJD(), zone).addDay(-30), zone);
			}
			return (start2.ajd.compareTo(stcd) <= 0) ? start2 : start1;
		}

		/**
		 * 日の九星の取得。
		 * @param date 日の取得先。
		 * @return その日を表す九星。
		 */
		public static Kyusei getDay(Day date) {
			ZoneId zone = date.getZoneId();
			date = AjdFactory.makeAJD(date.getAJD(), zone).trim();
			Start start = getStart(date, zone);
			int cnt = new Span(date, start.ajd).getDayPart();
			if (start.r == Equinox.SUMMER) {
				return fix(start.no - cnt);
			}
			return fix(start.no + cnt);
		}

		/**
		 * 時間の九星の取得。
		 * @param date 時間の取得先。
		 * @return その時間を表す九星。
		 */
		public static Kyusei getTime(Day date) {
			Start start = getStart(date, date.getZoneId());
			int day = EarthlyBranch.getDay(date).getNo() % 3;
			int base = start.r == Equinox.SUMMER ? 8 - day * 3 : day * 3;
			int time = EarthlyBranch.getTime(date).getNo();
			if (start.r == Equinox.SUMMER) {
				return fix(base - time);
			}
			return fix(base + time);
		}
	}

}
