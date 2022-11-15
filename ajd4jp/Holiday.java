/*
 * AJD4JP
 * Copyright (c) 2011-2021  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp;

import java.util.ArrayList;
import java.util.HashMap;

import ajd4jp.format.C;
import ajd4jp.format.Formats;
import ajd4jp.format.JapaneseYearF;
import ajd4jp.format.TwoHalfArabia;

class Holy extends AJD {
	private static final long serialVersionUID = 1;
	private Holiday hd = null;

	private Holy() {
		super();
	}

	Holy(int y, int m, int d) {
		super(y, m, d, AJD.OFFSET);
	}

	void set(Holiday h) {
		hd = h;
	}

	Holiday getHoliday() {
		return hd;
	}
}

abstract class HolidayType implements java.io.Serializable {
	private static final long serialVersionUID = 1;
	private String name;
	private Holy start = null, end = null;

	HolidayType(String n, Holy s, Holy e) {
		name = n;
		start = s;
		end = e;
	}

	String getName(int yyyy) {
		if (isHit(yyyy))
			return name;
		return null;
	}

	private boolean isHit(int yyyy) {
		if (yyyy < 1868)
			return false;
		if (start != null) {
			int sy = start.getYear();
			if (sy > yyyy)
				return false;
		}
		if (end != null) {
			int ey = end.getYear();
			if (ey < yyyy)
				return false;
		}
		return true;
	}

	abstract Holy[] get(int year) throws AJDException;

	static Holy[] select(int yyyy, Holiday h, HolidayType[] hd) {
		for (int i = 0; i < hd.length; i++) {
			if (hd[i].isHit(yyyy))
				return hd[i].getHoliday(h, yyyy);
		}
		return null;
	}

	private Holy[] getHoliday(Holiday h, int year) {
		Holy[] ret = null;
		try {
			ret = get(year);
			for (int i = 0; i < ret.length; i++) {
				ret[i].set(h);
			}
		} catch (AJDException e) {
		}
		return ret;
	}
}

class FixHoliday extends HolidayType {
	private Holy ajd;

	FixHoliday(String n, Holy start) {
		this(n, start, start, start);
	}

	FixHoliday(String n, Holy start, Holy end) {
		this(n, start, end, start != null ? start : end);
	}

	FixHoliday(String n, Holy start, Holy end, Holy fix) {
		super(n, start, end);
		ajd = fix;
	}

	Holy[] get(int yyyy) throws AJDException {
		return new Holy[] { new Holy(yyyy, ajd.getMonth(), ajd.getDay()) };
	}
}

class MoveHoliday extends HolidayType {
	int mm, seq;
	Week week;

	MoveHoliday(String n, Holy start, Holy end, int m, int s, Week w) {
		super(n, start, end);
		mm = m;
		seq = s;
		week = w;
	}

	Holy[] get(int yyyy) throws AJDException {
		AJD jd = new Month(yyyy, mm).getWeek(seq, week);
		return new Holy[] {
				new Holy(jd.getYear(), jd.getMonth(), jd.getDay())
		};
	}
}

class EquinoxHoliday extends HolidayType {
	int r;

	EquinoxHoliday(String n, Holy start, Holy end, int a) {
		super(n, start, end);
		r = a;
	}

	Holy[] get(int yyyy) throws AJDException {
		AJD jd = Equinox.getAJD(yyyy, r);
		return new Holy[] {
				new Holy(jd.getYear(), jd.getMonth(), jd.getDay())
		};
	}
}

interface AfterHoliday {
	void set(Holy[] l);
}

class Sunday extends HolidayType implements AfterHoliday {
	Sunday() {
		super("振替休日", new Holy(1973, 1, 1), null);
	}

	private Holy[] list;

	public void set(Holy[] l) {
		list = l;
	}

	Holy[] get(int yyyy) throws AJDException {
		ArrayList<Holy> ret = new ArrayList<Holy>();
		for (int i = 0; i < list.length; i++) {
			if (list[i].getWeek() == Week.SUNDAY) {
				if (list[i].getHoliday() != null) {
					if (yyyy < 2007) {
						// 月曜のみ
						i++;
						if (list[i].getHoliday() == null)
							ret.add(list[i]);
					} else {
						// 月曜以外も適用
						for (i++; i < list.length; i++) {
							if (list[i].getHoliday() == null) {
								ret.add(list[i]);
								break;
							}
						}
					}
				} else {
					i += 6;
					continue;
				}
			}
		}
		return ret.toArray(new Holy[0]);
	}
}

class Mid extends HolidayType implements AfterHoliday {
	Mid() {
		super("国民の休日", new Holy(1988, 1, 1), null);
	}

	private Holy[] list;

	public void set(Holy[] l) {
		list = l;
	}

	Holy[] get(int yyyy) throws AJDException {
		ArrayList<Holy> ret = new ArrayList<Holy>();
		for (int i = 2; i < list.length; i++) {
			if (list[i].getHoliday() != null &&
					list[i - 1].getHoliday() == null &&
					list[i - 2].getHoliday() != null) {
				Week w = list[i - 1].getWeek();
				if (w == Week.SUNDAY || w == Week.MONDAY)
					continue;
				ret.add(list[i - 1]);
			}
		}
		return ret.toArray(new Holy[0]);
	}
}

/**
 * 日本の祝日。<br>
 * 列挙型説明のうち取消線のあるものは、現在は廃止された、
 * または当年１年限りの祝日で現在は祝日ではありません。
 */
public enum Holiday implements OffProvider.Off {
	/** 元日 */
	GANZITU(
			new FixHoliday("元日", null, null, new Holy(1, 1, 1))),
	/** <span style="text-decoration:line-through;">元始祭</span> */
	GENSHI(
			new FixHoliday("元始祭", null, new Holy(1948, 1, 3))),
	/** <span style="text-decoration:line-through;">新年宴会</span> */
	SHINNEN(
			new FixHoliday("新年宴会", null, new Holy(1948, 1, 5))),
	/** 成人の日 */
	SEIJIN(
			new FixHoliday("成人の日", new Holy(1949, 1, 15), new Holy(1999, 1, 15)),
			new MoveHoliday("成人の日", new Holy(2000, 1, 1), null, 1, 2, Week.MONDAY)),
	/** <span style="text-decoration:line-through;">孝明天皇祭</span> */
	KOMEI(
			new FixHoliday("孝明天皇祭", null, new Holy(1912, 1, 30))),
	/** 建国記念日 */
	KENKOKU(
			new FixHoliday("紀元節", null, new Holy(1948, 2, 11)),
			new FixHoliday("建国記念の日", new Holy(1967, 2, 11), null)),
	/** 天皇誕生日(令和) */
	REIWA(
			new FixHoliday("天皇誕生日", new Holy(2020, 2, 23), null)),
	/** 春分の日 */
	SHUNBUN(
			new EquinoxHoliday("春季皇霊祭", new Holy(1879, 3, 21), new Holy(1948, 3, 21), Equinox.VERNAL),
			new EquinoxHoliday("春分の日", new Holy(1949, 3, 21), null, Equinox.VERNAL)),
	/** <span style="text-decoration:line-through;">神武天皇祭</span> */
	JINMU(
			new FixHoliday("神武天皇祭", null, new Holy(1948, 4, 3))),
	/** 昭和の日 */
	SHOWA(
			new FixHoliday("天長節", new Holy(1927, 4, 29), new Holy(1948, 4, 29)),
			new FixHoliday("天皇誕生日", new Holy(1949, 4, 29), new Holy(1988, 4, 29)),
			new FixHoliday("昭和の日", new Holy(2007, 4, 29), null)),
	/** 憲法記念日 */
	KENPO(
			new FixHoliday("憲法記念日", new Holy(1949, 5, 3), null)),
	/** みどりの日 */
	MIDORI(
			new FixHoliday("みどりの日", new Holy(1989, 4, 29), new Holy(2006, 4, 29)),
			new FixHoliday("みどりの日", new Holy(2007, 5, 4), null)),
	/** こどもの日 */
	KODOMO(
			new FixHoliday("こどもの日", new Holy(1949, 5, 5), null)),
	/** 海の日 */
	UMI(
			new FixHoliday("海の日", new Holy(1996, 7, 20), new Holy(2002, 7, 20)),
			new MoveHoliday("海の日", new Holy(2003, 1, 1), new Holy(2019, 1, 1), 7, 3, Week.MONDAY),
			new FixHoliday("海の日", new Holy(2020, 7, 23)),
			new FixHoliday("海の日", new Holy(2021, 7, 22)),
			new MoveHoliday("海の日", new Holy(2022, 1, 1), null, 7, 3, Week.MONDAY)),
	/** <span style="text-decoration:line-through;">明治天皇祭</span> */
	MEIJI(
			new FixHoliday("明治天皇祭", new Holy(1913, 7, 30), new Holy(1926, 7, 30))),
	/** 山の日 */
	YAMA(
			new FixHoliday("山の日", new Holy(2016, 8, 11), new Holy(2019, 8, 11)),
			new FixHoliday("山の日", new Holy(2020, 8, 10)),
			new FixHoliday("山の日", new Holy(2021, 8, 8)),
			new FixHoliday("山の日", new Holy(2022, 8, 11), null)),
	/** <span style="text-decoration:line-through;">天長節(大正天皇)</span> */
	TAISHO(
			new FixHoliday("天長節", new Holy(1913, 8, 31), new Holy(1926, 8, 31))),
	/** 敬老の日 */
	KEIRO(
			new FixHoliday("敬老の日", new Holy(1966, 9, 15), new Holy(2002, 9, 15)),
			new MoveHoliday("敬老の日", new Holy(2003, 9, 15), null, 9, 3, Week.MONDAY)),
	/** <span style="text-decoration:line-through;">神嘗祭</span> */
	KANNAME(
			new FixHoliday("神嘗祭", null, new Holy(1878, 9, 17)),
			new FixHoliday("神嘗祭", new Holy(1879, 10, 17), new Holy(1947, 10, 17))),
	/** 秋分の日 */
	SHUBUN(
			new EquinoxHoliday("秋季皇霊祭", new Holy(1878, 9, 23), new Holy(1947, 9, 23), Equinox.AUTUMNAL),
			new EquinoxHoliday("秋分の日", new Holy(1948, 9, 23), null, Equinox.AUTUMNAL)),
	/** スポーツの日 */
	TAIIKU(
			new FixHoliday("体育の日", new Holy(1966, 10, 10), new Holy(1999, 10, 10)),
			new MoveHoliday("体育の日", new Holy(2000, 1, 1), new Holy(2019, 12, 31), 10, 2, Week.MONDAY),
			new FixHoliday("スポーツの日", new Holy(2020, 7, 24)),
			new FixHoliday("スポーツの日", new Holy(2021, 7, 23)),
			new MoveHoliday("スポーツの日", new Holy(2022, 1, 1), null, 10, 2, Week.MONDAY)),
	/** <span style="text-decoration:line-through;">天長節祝日</span> */
	TAISHO2(
			new FixHoliday("天長節祝日", new Holy(1913, 10, 31), new Holy(1926, 10, 31))),
	/** 文化の日 */
	BUNKA(
			new FixHoliday("天長節", null, new Holy(1911, 11, 3)),
			new FixHoliday("明治節", new Holy(1927, 11, 3), new Holy(1947, 11, 3)),
			new FixHoliday("文化の日", new Holy(1948, 11, 3), null)),
	/** 勤労感謝の日 */
	KINRO(
			new FixHoliday("新嘗祭", null, new Holy(1947, 11, 23)),
			new FixHoliday("勤労感謝の日", new Holy(1948, 11, 23), null)),
	/** <span style="text-decoration:line-through;">天皇誕生日(平成)</span> */
	HEISEI(
			new FixHoliday("天皇誕生日", new Holy(1989, 12, 23), new Holy(2018, 12, 23))),
	/** <span style="text-decoration:line-through;">大正天皇祭</span> */
	TAISHO3(
			new FixHoliday("大正天皇祭", new Holy(1927, 12, 25), new Holy(1947, 12, 25))),

	/** 即位の礼(1915, 1928, 2019) */
	SOKUI(
			new FixHoliday("即位の礼", new Holy(1915, 11, 10)),
			new FixHoliday("即位の礼", new Holy(1928, 11, 10)),
			new FixHoliday("天皇の即位の日", new Holy(2019, 5, 1))),
	/** <span style="text-decoration:line-through;">大嘗祭(1915, 1928)</span> */
	ONIE(
			new FixHoliday("大嘗祭", new Holy(1915, 11, 14)),
			new FixHoliday("大嘗祭", new Holy(1928, 11, 14))),
	/** <span style="text-decoration:line-through;">大饗第1日(1915, 1928)</span> */
	DAIKYO1(
			new FixHoliday("大饗第一日", new Holy(1915, 11, 16)),
			new FixHoliday("大饗第一日", new Holy(1928, 11, 16))),
	/** <span style="text-decoration:line-through;">明仁親王の結婚の儀(1959)</span> */
	EMP1959(new FixHoliday("明仁親王の結婚の儀", new Holy(1959, 4, 10))),
	/** <span style="text-decoration:line-through;">昭和天皇の大喪の礼(1989)</span> */
	EMP1989(new FixHoliday("昭和天皇の大喪の礼", new Holy(1989, 2, 24))),
	/** <span style="text-decoration:line-through;">即位の礼正殿の儀(1990)</span> */
	EMP1990(new FixHoliday("即位の礼正殿の儀", new Holy(1990, 11, 12))),
	/** <span style="text-decoration:line-through;">徳仁親王の結婚の儀(1993)</span> */
	EMP1993(new FixHoliday("徳仁親王の結婚の儀", new Holy(1993, 6, 9))),
	/** 即位の礼正殿の儀(2019) */
	EMP2019(new FixHoliday("即位礼正殿の儀の行われる日", new Holy(2019, 10, 22))),

	/** 振替休日 */
	FURIKAE(new Sunday()),
	/** 国民の休日 */
	KOKUMIN(new Mid());

	private HolidayType[] type;

	private Holiday(HolidayType... hd) {
		type = hd;
	}

	private static HashMap<Integer, Holy[]> list = new HashMap<Integer, Holy[]>();

	static Holy[] build(int yyyy) throws AJDException {
		final Holiday[] hd = values();
		synchronized (list) {
			Holy[] dim = list.get(yyyy);
			if (dim != null)
				return dim;
			ArrayList<Holy> tmp = new ArrayList<Holy>();
			for (int i = 0; i < hd.length; i++) {
				if (hd[i].type[0] instanceof AfterHoliday)
					continue;
				Holy[] ret = HolidayType.select(yyyy, hd[i], hd[i].type);
				if (ret != null) {
					for (int j = 0; j < ret.length; j++)
						tmp.add(ret[j]);
				}
			}
			if (tmp.size() == 0) {
				dim = new Holy[0];
				list.put(yyyy, dim);
				return dim;
			}
			Holy[][] mon = new Holy[12][];
			for (int i = 0; i < mon.length; i++)
				mon[i] = new Month(yyyy, i + 1).getHolis();
			for (Holy h : tmp)
				mon[h.getMonth() - 1][h.getDay() - 1].set(h.getHoliday());
			tmp = new ArrayList<Holy>();
			for (int i = 0; i < mon.length; i++) {
				for (Holy h : mon[i])
					tmp.add(h);
			}
			AfterHoliday[] aft = new AfterHoliday[] {
					(AfterHoliday) FURIKAE.type[0],
					(AfterHoliday) KOKUMIN.type[0]
			};
			dim = tmp.toArray(new Holy[0]);
			for (int i = 0; i < aft.length; i++) {
				aft[i].set(dim);
				Holy[] ret = HolidayType.select(yyyy, i == 0 ? FURIKAE : KOKUMIN, i == 0 ? FURIKAE.type : KOKUMIN.type);
				if (ret != null) {
					for (int j = 0; j < ret.length; j++) {
						mon[ret[j].getMonth() - 1][ret[j].getDay() - 1].set(ret[j].getHoliday());
					}
				}
			}
			tmp = new ArrayList<Holy>();
			for (Holy h : dim) {
				if (h.getHoliday() != null)
					tmp.add(h);
			}
			dim = tmp.toArray(new Holy[0]);
			list.put(yyyy, dim);
			return dim;
		}
	}

	/**
	 * 祝日の取得。指定日が祝日ならば、null以外を返します。
	 * @param ajd 祝日判定対象日。
	 * @return 祝日。祝日でなければnull。
	 */
	public static Holiday getHoliday(AJD ajd) {
		Holy[] ret = null;
		try {
			ret = build(ajd.getYear());
		} catch (AJDException e) {
		}
		for (Holy h : ret) {
			if (h.getMonth() == ajd.getMonth() && h.getDay() == ajd.getDay()) {
				return h.getHoliday();
			}
		}
		return null;
	}

	/**
	 * 祝日名称の取得。
	 * 例えば 4/29(昭和天皇の誕生日) は、参照する年によって祝日名称が変化します。
	 * @param ajd 年月日。
	 * @return 祝日名称。該当日が祝日でなければnull。
	 */
	public String getName(AJD ajd) {
		String name = null;
		if (this != getHoliday(ajd))
			return null;
		for (HolidayType t : type) {
			name = t.getName(ajd.getYear());
			if (name != null)
				break;
		}
		return name;
	}

	/**
	 * 指定年の全ての祝日を取得。
	 * @param yyyy 年。
	 * @return 祝日の配列。
	 * @exception AJDException 不正な年。
	 */
	public static AJD[] getHoliday(int yyyy) throws AJDException {
		ArrayList<AJD> list = new ArrayList<AJD>();
		AJD day = new AJD(yyyy, 1, 1);
		for (; day.getYear() == yyyy; day = day.addDay(1)) {
			if (getHoliday(day) != null) {
				list.add(day);
			}
		}
		return list.toArray(new AJD[0]);
	}

	/**
	 * {@link #getHoliday(int)}を呼び出し、結果を標準出力します。
	 * @param argv 年を指定
	 * @exception Exception 不正な年。
	 */
	public static void main(String ... argv) throws Exception {
		Formats jy = new Formats(Formats.DATE, new C("("), new JapaneseYearF(new TwoHalfArabia('0')), new C("年)"));
		for (AJD ajd : getHoliday(Integer.parseInt(argv[0]))) {
			System.out.print(jy.toString(ajd));
			System.out.print("\t");
			System.out.println(getHoliday(ajd).getName(ajd));
		}
	}
}
