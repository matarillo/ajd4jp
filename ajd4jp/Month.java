/*
 * AJD4JP
 * Copyright (c) 2011-2021  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp;

import java.time.ZoneId;

import ajd4jp.iso.Year;
import ajd4jp.util.AjdFactory;

/**
 * 新暦での、月単位の処理を行うクラスです。<br>
 * カレンダー処理を行う上で起点となるクラスです。
 */
public class Month implements Comparable<Month>, java.io.Serializable {
	private static final long serialVersionUID = 1;
	private static final int[] days = new int[] {
			31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
	};
	private static final String[] eng3 = new String[] {
			"Jan", "Feb", "Mar", "Apr", "May", "Jun",
			"Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
	};
	private static final String[] eng = new String[] {
			"January", "February", "March", "April",
			"May", "June", "July", "August",
			"September", "October", "November", "December"
	};
	static final String[] jp = new String[] {
			"睦月", "如月", "弥生", "卯月", "皐月", "水無月",
			"文月", "葉月", "長月", "神無月", "霜月", "師走"
	};

	private short y, m;
	private ZoneId zone;

	private void init(int yyyy, int mm, ZoneId z) throws AJDException {
		AJD.check(yyyy, mm);
		y = (short) yyyy;
		m = (short) mm;
		zone = z;
	}

	/**
	 * コンストラクタ。今月の年月が設定されます。
	 * @param z タイムゾーン。
	 */
	public Month(ZoneId z) {
		this(AjdFactory.now(z));
	}

	/**
	 * コンストラクタ。今月の年月が設定されます。タイムゾーンはAJDと同じとなります。
	 */
	public Month() {
		this(AJD.OFFSET);
	}

	/**
	 * コンストラクタ。
	 * @param yyyy 年。0は指定できません。BC1年は-1です。
	 * @param mm 月。1～12。
	 * @param z タイムゾーン。
	 * @throws AJDException 不正な年月。
	 */
	public Month(int yyyy, int mm, ZoneId z) throws AJDException {
		init(yyyy, mm, z);
	}

	/**
	 * コンストラクタ。タイムゾーンはAJDと同じとなります。
	 * @param yyyy 年。0は指定できません。BC1年は-1です。
	 * @param mm 月。1～12。
	 * @throws AJDException 不正な年月。
	 */
	public Month(Year yyyy, int mm) throws AJDException {
		this(yyyy.getAjdYear(), mm, yyyy.getZoneId());
	}

	/**
	 * コンストラクタ。タイムゾーンはAJDと同じとなります。
	 * @param yyyy 年。0は指定できません。BC1年は-1です。
	 * @param mm 月。1～12。
	 * @throws AJDException 不正な年月。
	 */
	public Month(int yyyy, int mm) throws AJDException {
		init(yyyy, mm, AJD.OFFSET);
	}

	/**
	 * コンストラクタ。引数の年月と同じ月を設定します。
	 * @param ajd 年月。
	 */
	public Month(AJD ajd) {
		try {
			init(ajd.getYear(), ajd.getMonth(), ajd.getZoneId());
		} catch (AJDException e) {
		}
	}

	/**
	 * 年の取得。
	 * @return 年。負数は紀元前です。0は返しません。
	 */
	public int getYear() {
		return y;
	}

	/**
	 * 月の取得。
	 * @return 月。1～12。
	 */
	public int getMonth() {
		return m;
	}

	/**
	 * 月の加算。
	 * @param mm 加算する月数。負数も可。
	 * @return 加算後の月。
	 */
	public Month add(int mm) {
		if (mm == 0)
			return this;

		int yy = y;
		mm += m;
		while (true) {
			if (mm < 1) {
				mm += 12;
				yy--;
				if (yy == 0)
					yy--;
			} else if (mm > 12) {
				mm -= 12;
				yy++;
				if (yy == 0)
					yy++;
			} else
				break;
		}
		Month ret = null;
		try {
			ret = new Month(yy, mm);
		} catch (AJDException e) {
		}
		return ret;
	}

	/**
	 * 閏年であるか？1582年を境に閏年判定が変わります。
	 * @param yyyy 年。
	 * @return true:閏年、false:平年。
	 */
	public static boolean isLeapYear(int yyyy) {
		if ((yyyy % 4) == 0) {
			if ((yyyy % 100) == 0 && yyyy > 1582) {
				if ((yyyy % 400) == 0)
					return true;
			} else
				return true;
		}
		return false;
	}

	/**
	 * 閏年であるか？1582年を境に閏年判定が変わります。
	 * @return true:閏年、false:平年。
	 */
	public boolean isLeapYear() {
		return isLeapYear(y);
	}

	/**
	 * 月の初日取得。
	 * @return その月の1日を返します。
	 */
	public AJD getFirstAJD() {
		try {
			return AjdFactory.makeAJD(y, m, 1, zone);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 月の最終日取得。
	 * @return 1月なら31。2月は閏年判定を行います。
	 */
	public AJD getLastAJD() {
		try {
			return AjdFactory.makeAJD(y, m, getLastDay(), zone);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 月の最終日取得。
	 * @param yyyy 年。
	 * @param mm 日。
	 * @return 1月なら31。2月は閏年判定を行います。
	 */
	public static int getLastDay(int yyyy, int mm) {
		if (mm == 2) {
			if (isLeapYear(yyyy))
				return days[1] + 1;
			return days[1];
		}
		return days[mm - 1];
	}

	/**
	 * 月の最終日取得。
	 * @return 1月なら31。2月は閏年判定を行います。
	 */
	public int getLastDay() {
		return getLastDay(y, m);
	}

	private transient AJD[] ajd = null;

	/**
	 * 一ヶ月の一覧取得。
	 * @return 日の配列。[0]1日～[N-1]N日(月の最終日)。
	 * 全て 00:00:00 になります。
	 */
	public AJD[] getDays() {
		if (ajd != null)
			return ajd;
		AJD[] d = new AJD[getLastDay() - (y == 1582 && m == 10? 10: 0)];
		int cnt = 0;
		for (int i = 0; i < d.length; i++) {
			try {
				d[cnt] = AjdFactory.makeAJD(y, m, i + 1, zone);
				cnt++;
			} catch (AJDException e) {
			}
		}
		ajd = d;
		return d;
	}

	Holy[] getHolis() {
		Holy[] d = new Holy[getLastDay()];
		for (int i = 0; i < d.length; i++)
			d[i] = new Holy(y, m, i + 1);
		return d;
	}

	/**
	 * 曜日検索。
	 * @param seq 何番目か？1～。
	 * @param week 曜日。
	 * @return 第seq week曜日を返します。例)第2水曜日。
	 * 時間は 00:00:00 になります。月の範囲外の場合、nullを返します。
	 */
	public AJD getWeek(int seq, Week week) {
		AJD[] d = getDays();
		int hit = 0;
		for (int i = 0; i < d.length; i++) {
			if (d[i].getWeek() == week) {
				hit++;
				if (seq == hit)
					return d[i];
				i += 6;
				continue;
			}
		}
		return null;
	}

	/**
	 * 一週間の一覧取得。nullでない場合、常に7個の配列を返します。
	 * @param seq 何番目か？1～。
	 * @param supp_f true:先頭週・最終週で隣の月の日を含める。
	 * false:先頭週・最終週で当月以外はnullにする。
	 * @return 第seq週を返します。[0]日曜～[6]土曜。
	 * 全て 00:00:00 になります。月の範囲外の場合、nullを返します。
	 */
	public AJD[] getWeek(int seq, boolean supp_f) {
		if (seq < 1)
			return null;
		AJD[] ret = new AJD[7];
		AJD[] d = getDays();
		int start = (seq - 1) * 7;
		if (start == 0) {
			int n = 0;
			int left = d[0].getWeek().get();
			if (left > 0) {
				AJD[] pre = add(-1).getDays();
				for (int i = pre.length - left; i < pre.length; i++, n++)
					ret[n] = supp_f ? pre[i] : null;
			}
			for (int i = 0; n < ret.length; n++, i++)
				ret[n] = d[i];
			return ret;
		}
		if (start < d.length) {
			start = start - d[start].getWeek().get();
		} else {
			start -= ret.length;
			if (start >= d.length)
				return null;
			start = start + ret.length - d[start].getWeek().get();
			if (start >= d.length)
				return null;
		}
		int n = 0;
		for (; start < d.length && n < ret.length; start++, n++)
			ret[n] = d[start];
		if (n == ret.length || !supp_f)
			return ret;
		AJD[] after = add(1).getDays();
		for (int i = 0; n < ret.length; n++, i++)
			ret[n] = after[i];
		return ret;
	}

	/**
	 * 何週間存在するか取得します。
	 * getWeek で指定可能な週(seqパラメータ)は、この戻り値が最大値です。
	 * @return 週の数。
	 */
	public int getLastWeek() {
		int cnt = 6;
		for (; cnt > 4; cnt--) {
			if (getWeek(cnt, false) != null)
				break;
		}
		return cnt;
	}

	/**
	 * 月の英語名略称を返します。
	 * @param m 月。1～12。
	 * @return Jan～Dec。
	 */
	public static String getShortName(int m) {
		return eng3[m - 1];
	}

	/**
	 * 月の英語名略称を返します。
	 * @return Jan～Dec。
	 */
	public String getShortName() {
		return getShortName(m);
	}

	/**
	 * 月の英語名を返します。
	 * @param m 月。1～12。
	 * @return January～December。
	 */
	public static String getLongName(int m) {
		return eng[m - 1];
	}

	/**
	 * 月の英語名を返します。
	 * @return January～December。
	 */
	public String getLongName() {
		return getLongName(m);
	}

	/**
	 * 月の日本語名を返します。
	 * @param m 月。1～12。
	 * @return 睦月～師走。
	 */
	public static String getJpName(int m) {
		return jp[m - 1];
	}

	/**
	 * 月の日本語名を返します。
	 * @return 睦月～師走。
	 */
	public String getJpName() {
		return getJpName(m);
	}

	/**
	 * 月の比較。
	 * @param mon 比較対象。
	 * @return -1:this&lt;mon(thisが過去)、0:this==mon、1:this&gt;mon(thisが未来)。
	 */
	public int compareTo(Month mon) {
		int ret = this.y - mon.y;
		if (ret == 0) {
			ret = this.m - mon.m;
		}
		if (ret > 0)
			return 1;
		if (ret < 0)
			return -1;
		return 0;
	}

	/**
	 * ハッシュコードの取得。
	 * @return ハッシュコード。
	 */
	public int hashCode() {
		return (y << 4) | m;
	}

	/**
	 * 月の比較。
	 * @return true:一致、false:不一致。
	 */
	public boolean equals(Object o) {
		if (o instanceof Month)
			return compareTo((Month) o) == 0;
		return false;
	}

	/**
	 * 月を文字列にします。
	 * @return yyyy/mm のフォーマット。
	 */
	public String toString() {
		return String.format("%d/%02d", y, m);
	}
}
