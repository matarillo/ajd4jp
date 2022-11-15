/*
 * AJD4JP
 * Copyright (c) 2011-2021  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.function.Supplier;

import ajd4jp.iso.Year;
import ajd4jp.util.Calc;

/**
 * ユリウス通日での１日を表します。<br>
 * +9時間固定の日本時間となります。
 */
public class AJD implements Day {
	private static final long serialVersionUID = 1;

	static final BigDecimal FIX05 = new BigDecimal("0.50000000");
	static final BigDecimal FIX_05 = new BigDecimal("-0.50000000");
	private BigDecimal ajd;
	private Instant instant;

	// エポック(1970/01/01 00:00:00)
	private static final BigDecimal EPOCH = new BigDecimal("2440587.50000000000000000000");

	private short year, mon, day, hour, min, sec;

	/**
	 * +9時間を表す。
	 */
	public static final ZoneOffset OFFSET = ZoneOffset.of("+09:00");

	/**
	 * タイムゾーン取得。
	 * @return 常に+9時間を返します。
	 */
	public ZoneId getZoneId() {
		return OFFSET;
	}

	/**
	 * インスタントへ変換。
	 * @return 保持しているインスタントを返す。
	 */
	public Instant toInstant() {
		return instant;
	}

	/**
	 * エポック(1970/01/01 00:00:00 GMT)からのミリ秒を返す。
	 * Java標準の日時系クラスへの設定値に使用できます。
	 * @return ミリ秒。
	 */
	public long getTime() {
		return instant.toEpochMilli();
	}

	/**
	 * 指定ナノ秒を加算したインスタンスを返す。
	 * @param nanos ナノ秒。負数も可。
	 * @return 新しいインスタンス。
	 */
	public AJD addNano(long nanos) {
		if (nanos == 0) {
			return this;
		}
		return from(instant.plusNanos(nanos));
	}

	/**
	 * 指定秒を加算したインスタンスを返す。
	 * @param ss 秒。負数も可。
	 * @return 新しいインスタンス。
	 */
	public AJD addSecond(long ss) {
		if (ss == 0) {
			return this;
		}
		return from(instant.plusSeconds(ss));
	}

	/**
	 * 指定分を加算したインスタンスを返す。
	 * @param mm 分。負数も可。
	 * @return 新しいインスタンス。
	 */
	public AJD addMinute(long mm) {
		return addSecond(mm * 60);
	}

	/**
	 * 指定時間を加算したインスタンスを返す。
	 * @param hh 時間。負数も可。
	 * @return 新しいインスタンス。
	 */
	public AJD addHour(long hh) {
		return addMinute(hh * 60);
	}

	/**
	 * 指定日数を加算したインスタンスを返す。
	 * @param dd 日数。負数も可。日未満の小数表現も可。
	 * @return 新しいインスタンス。
	 */
	public AJD addDay(Number dd) {
		BigDecimal d = new BigDecimal(dd.toString());
		if (d.compareTo(BigDecimal.ZERO) == 0) {
			return this;
		}
		return addNano(d.multiply(DAY_NANO_SEC).longValue());
	}

	/**
	 * 保持している時刻を文字列にします。
	 * @return yyyy/mm/dd hh:mm:ss.nnnnnnnnn(タイムゾーン)[ユリウス通日]のフォーマット。
	 */
	public String toString() {
		return String.format("%d/%02d/%02d %02d:%02d:%02d.%09d(%s)[%s]", year, mon, day, hour, min, sec, getNano(),
				getZoneId(), Calc.toString(ajd));
	}
	/**
	 * タイムスタンプをJDBCタイムスタンプ・エスケープ形式にフォーマットします。yyyy-mm-dd hh:mm:ss.fffffffff。ffffffffffはナノ秒を示します。
	 * @return yyyy-mm-dd hh:mm:ss.fffffffff形式のStringオブジェクト
	 */
	public String toSqlString() {
		return String.format("%04d-%02d-%02d %02d:%02d:%02d.%09d", year, mon, day, hour, min, sec, getNano());
	}

	/**
	 * 日本時間の西暦年の取得。
	 * @return 西暦年。負数なら紀元前。0は返りません。
	 */
	public int getYear() {
		return year;
	}

	private Era.Year era = null;

	/**
	 * 日本時間の和暦の取得。
	 * @return 和暦。明治より前は西暦のまま返します。
	 */
	public Era.Year getEra() {
		if (era == null)
			era = new Era.Year(this);
		return era;
	}

	/**
	 * 日本時間の月の取得。
	 * @return 月。1～12。
	 */
	public int getMonth() {
		return mon;
	}

	/**
	 * この日付が示すMonthを返す。
	 * @return 月。
	 */
	public Month toMonth() {
		return new Month(this);
	}

	/**
	 * 日本時間の日の取得。
	 * @return 日。1～N。
	 */
	public int getDay() {
		return day;
	}

	private Week week = null;

	/**
	 * 日本時間の曜日の取得。
	 * @return 曜日。
	 */
	public Week getWeek() {
		if (week == null)
			week = Week.get(this);
		return week;
	}

	/**
	 * 日本時間の時の取得。
	 * @return 時。0～23。
	 */
	public int getHour() {
		return hour;
	}

	/**
	 * 日本時間の分の取得。
	 * @return 分。0～59。
	 */
	public int getMinute() {
		return min;
	}

	/**
	 * 日本時間の秒の取得。
	 * @return 秒。0～59。
	 */
	public int getSecond() {
		return sec;
	}

	/**
	 * ナノ秒の取得。
	 * @return ナノ秒。0～999,999,999。
	 */
	public int getNano() {
		return instant.getNano();
	}

	/**
	 * コンストラクタ。現在時刻が設定されます。
	 */
	public AJD() {
		this(Instant.now());
	}

	/**
	 * インスタントからのインスタンス生成。
	 * @param ins インスタント
	 * @return インスタンス。
	 */
	protected AJD from(Instant ins) {
		return new AJD(ins);
	}

	private static final BigDecimal DAY_SEC = new BigDecimal(60 * 60 * 24);
	private static final BigDecimal NANO_SEC = new BigDecimal(1_000_000_000);
	private static final BigDecimal DAY_NANO_SEC = DAY_SEC.multiply(NANO_SEC);

	private static BigDecimal addNanoSec(BigDecimal org, int ns) {
		return org.add(Calc.div(new BigDecimal(ns), DAY_NANO_SEC));
	}

	protected AJD(Instant ins, Supplier<ZoneOffset> offsetter) {
		instant = ins;
		ajd = Calc.div(new BigDecimal(ins.getEpochSecond()), DAY_SEC).add(EPOCH);
		ajd = addNanoSec(ajd, ins.getNano());
		set(offsetter);
	}

	/**
	 * コンストラクタ。
	 * @param ins インスタント
	 */
	public AJD(Instant ins) {
		this(ins, () -> OFFSET);
	}

	private void ajd2Instant(Integer ns) {
		if (ns == null) {
			ns = ajd.abs().multiply(DAY_NANO_SEC).remainder(NANO_SEC).intValue();
		}
		long time = ajd.subtract(EPOCH).multiply(DAY_SEC).longValue();
		int sub = (int) (time % 60);
		if (sub < 0) {
			sub += 60;
		}
		sub -= sec;
		instant = Instant.ofEpochSecond(time - sub, ns);
	}

	protected AJD(Number num, Supplier<ZoneOffset> offsetter) {
		ajd = new BigDecimal(num.toString()).abs();
		set(offsetter);
		ajd2Instant(null);
	}

	/**
	 * コンストラクタ。負数は指定できません。入力値の絶対値を採用します。
	 * @param num ユリウス通日。
	 */
	public AJD(Number num) {
		this(num, () -> OFFSET);
	}

	private static Instant localDateTime2Instant(LocalDateTime ldt, ZoneId zone) {
		return ldt.atZone(zone).toInstant();
	}

	/**
	 * java.sql.* 対策
	 * @param date java.util.Date派生クラス
	 * @param zone タイムゾーン
	 * @return インスタント
	 */
	protected static Instant date2Instant(java.util.Date date, ZoneId zone) {
		if (date instanceof java.sql.Date) {
			return localDateTime2Instant(((java.sql.Date)date).toLocalDate().atStartOfDay(), zone);
		}
		else if (date instanceof java.sql.Time) {
			return localDateTime2Instant(((java.sql.Time)date).toLocalTime().atDate(LocalDate.of(1970, 1, 1)), zone);
		}
		else if (date instanceof java.sql.Timestamp) {
			return localDateTime2Instant(((java.sql.Timestamp)date).toLocalDateTime(), zone);
		}
		else { return date.toInstant(); }
	}
	/**
	 * コンストラクタ。引数の日時を表すインスタンスを生成します。
	 * @param date 日時。
	 */
	public AJD(java.util.Date date) {
		this(date2Instant(date, OFFSET));
	}

	/**
	 * コンストラクタ。引数の日時を表すインスタンスを生成します。
	 * @param date 日時。
	 */
	public AJD(Day date) {
		this(date.toAJD().instant);
	}

	/**
	 * コンストラクタ。引数の日時を表すインスタンスを生成します。
	 * @param calendar 日時。
	 */
	public AJD(Calendar calendar) {
		this(calendar.toInstant());
	}

	private static final BigDecimal J122_1 = new BigDecimal("122.1"),
			J365_25 = new BigDecimal("365.25"),
			J30_6001 = new BigDecimal("30.6001");

	private void set(Supplier<ZoneOffset> offsetter) {
		int offset_min = offsetter.get().getTotalSeconds() / 60;
		BigDecimal tmp = ajd;
		double org = ajd.doubleValue();
		long jd = Calc.cut(tmp);
		tmp = tmp.subtract(new BigDecimal(jd));
		if (tmp.compareTo(FIX05) >= 0) {
			jd++;
			tmp = tmp.subtract(FIX05);
		} else
			tmp = tmp.add(FIX05);

		if (org >= 2299160.5)
			jd = jd + 1 + (jd - 1867216) / 36524 - (jd - 1867216) / 146096;
		jd += 1524;
		BigDecimal bjd = new BigDecimal(jd);

		long c = Calc.cut(Calc.div(bjd.subtract(J122_1), J365_25));
		long k = c * 365 + c / 4;
		long e = Calc.cut(Calc.div(bjd.subtract(new BigDecimal(k)), J30_6001));

		year = (short) (c - 4716);
		mon = (short) (e - 1);
		if (mon > 12) {
			mon -= 12;
			year++;
		}
		if (year <= 0)
			year--;
		day = (short) (jd - k - Calc.cut(new BigDecimal("30.6").multiply(new BigDecimal(e))));

		long s = Calc.cut(tmp.multiply(Calc.J86400).add(FIX05));
		hour = (short) (s / 3600);
		min = (short) ((s % 3600) / 60 + offset_min);
		sec = (short) (s % 60);

		while (min < 0) {
			min += 60;
			hour--;
		}
		while (min > 59) {
			min -= 60;
			hour++;
		}
		if (hour >= 0 && hour < 24) {
			return;
		}
		try {
			Month m = new Month(year, mon);
			if (hour < 0) {
				hour += 24;
				day--;
				if (day < 1) {
					m = m.add(-1);
					day = (short) m.getLastDay();
				}
			} else {
				hour -= 24;
				day++;
				if (day > m.getLastDay()) {
					m = m.add(1);
					day = 1;
				}
			}
			year = (short) m.getYear();
			mon = (short) m.getMonth();
		} catch (AJDException e1) {
		}
		try {
			isGregorian(year, mon, day);
		} catch (AJDException e1) {
			day = (short) (offset_min < 0 ? 4 : 15);
		}
	}

	/**
	 * 年月日時分秒からのインスタンス生成。
	 * @param yyyy 年
	 * @param mm 月
	 * @param dd 日
	 * @param hh 時
	 * @param mi 分
	 * @param ss 秒
	 * @param ns ナノ秒
	 * @return インスタンス。
	 * @throws AJDException 日時不正。
	 */
	protected AJD from(int yyyy, int mm, int dd, int hh, int mi, int ss, int ns) throws AJDException {
		return new AJD(yyyy, mm, dd, hh, mi, ss, ns);
	}

	/**
	 * コンストラクタ。日時は日本時間とみなされます。
	 * @param yyyy 年。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @param hh 時。0～23。
	 * @param mi 分。0～59。
	 * @param ss 秒。0～59。
	 * @throws AJDException 不正な日付。
	 */
	public AJD(Year yyyy, int mm, int dd, int hh, int mi, int ss) throws AJDException {
		this(yyyy.getAjdYear(), mm, dd, hh, mi, ss);
	}

	/**
	 * コンストラクタ。日時は日本時間とみなされます。
	 * @param yyyy 年。0は指定できません。BC1年は-1です。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @param hh 時。0～23。
	 * @param mi 分。0～59。
	 * @param ss 秒。0～59。
	 * @throws AJDException 不正な日付。
	 */
	public AJD(int yyyy, int mm, int dd, int hh, int mi, int ss) throws AJDException {
		this(yyyy, mm, dd, hh, mi, ss, 0);
	}

	/**
	 * コンストラクタ。日時は日本時間とみなされます。
	 * @param yyyy 年。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @param hh 時。0～23。
	 * @param mi 分。0～59。
	 * @param ss 秒。0～59。
	 * @param ns ナノ秒。0～999,999,999。
	 * @throws AJDException 不正な日付。
	 */
	public AJD(Year yyyy, int mm, int dd, int hh, int mi, int ss, int ns) throws AJDException {
		this(yyyy.getAjdYear(), mm, dd, hh, mi, ss, ns);
	}

	/**
	 * コンストラクタ。日時は日本時間とみなされます。
	 * @param yyyy 年。0は指定できません。BC1年は-1です。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @param hh 時。0～23。
	 * @param mi 分。0～59。
	 * @param ss 秒。0～59。
	 * @param ns ナノ秒。0～999,999,999。
	 * @throws AJDException 不正な日付。
	 */
	public AJD(int yyyy, int mm, int dd, int hh, int mi, int ss, int ns) throws AJDException {
		this(yyyy, mm, dd, hh, mi, ss, ns, OFFSET);
	}

	/**
	 * コンストラクタ。
	 * @param yyyy 年。0は指定できません。BC1年は-1です。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @param hh 時。0～23。
	 * @param mi 分。0～59。
	 * @param ss 秒。0～59。
	 * @param ns ナノ秒。0～999,999,999。
	 * @param offset UTCからの時差。
	 * @throws AJDException 不正な日付。
	 */
	protected AJD(int yyyy, int mm, int dd, int hh, int mi, int ss, int ns, ZoneOffset offset) throws AJDException {
		initAJD(yyyy, mm, dd, hh, mi, ss, ns, () -> offset);
	}

	AJD(int yyyy, int mm, int dd, ZoneOffset z) {
		try {
			initAJD(yyyy, mm, dd, 0, 0, 0, 0, () -> z);
		} catch (AJDException e) {
		}
	}

	/**
	 * コンストラクタ。日時は日本時間とみなされます。
	 * 時間は 00:00:00 となります。
	 * @param yyyy 年。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @throws AJDException 不正な日付。
	 */
	public AJD(Year yyyy, int mm, int dd) throws AJDException {
		this(yyyy.getAjdYear(), mm, dd);
	}

	/**
	 * コンストラクタ。日時は日本時間とみなされます。
	 * 時間は 00:00:00 となります。
	 * @param yyyy 年。0は指定できません。BC1年は-1です。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @throws AJDException 不正な日付。
	 */
	public AJD(int yyyy, int mm, int dd) throws AJDException {
		this(yyyy, mm, dd, 0, 0, 0);
	}

	/**
	 * コンストラクタ。日時は日本時間とみなされます。
	 * @param yyyy 年。0は指定できません。BC1年は-1です。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @param hh 時。0～23。
	 * @param mi 分。0～59。
	 * @param ss 秒。0～59。
	 * @throws AJDException 不正な日付。
	 */
	public AJD(String yyyy, String mm, String dd, String hh, String mi, String ss) throws AJDException {
		this(
				Integer.parseInt(yyyy),
				Integer.parseInt(mm),
				Integer.parseInt(dd),
				Integer.parseInt(hh),
				Integer.parseInt(mi),
				Integer.parseInt(ss));
	}

	/**
	 * コンストラクタ。日時は日本時間とみなされます。
	 * 時間は 00:00:00 となります。
	 * @param yyyy 年。0は指定できません。BC1年は-1です。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @throws AJDException 不正な日付。
	 */
	public AJD(String yyyy, String mm, String dd) throws AJDException {
		this(yyyy, mm, dd, "0", "0", "0");
	}

	static int check(int yy, int mm) throws AJDException {
		if (yy == 0)
			throw new AJDException("年が0です。");
		if (mm < 1 || mm > 12)
			throw new AJDException("月が範囲外です。");
		if (yy < 0)
			yy++;
		return yy;
	}

	private static boolean isGregorian(int yy, int mm, int dd) throws AJDException {
		if (dd < 1 || dd > new Month(yy, mm).getLastDay())
			throw new AJDException("日が範囲外です。");
		if (yy == 1582 && mm == 10 && dd > 4 && dd < 15) {
			throw new AJDException("日が範囲外です。");
		}
		return yy > 1582 || (yy == 1582 && mm > 10) || (yy == 1582 && mm == 10 && dd >= 15);
	}

	void initAJD(int yy, int mm, int dd, int hh, int mi, int ss, int ns, Supplier<ZoneOffset> offsetter)
			throws AJDException {
		if (hh < 0 || hh > 23)
			throw new AJDException("時が範囲外です。");
		if (mi < 0 || mi > 59)
			throw new AJDException("分が範囲外です。");
		if (ss < 0 || ss > 59)
			throw new AJDException("秒が範囲外です。");
		if (ns < 0 || ns > 999_999_999)
			throw new AJDException("ナノ秒が範囲外です。");

		year = (short) yy;
		boolean bc_f = yy <= 0;
		boolean gre_f = isGregorian(yy, mm, dd);
		yy = check(yy, mm);
		mon = (short) mm;
		day = (short) dd;
		hour = (short) hh;
		min = (short) mi;
		sec = (short) ss;

		if (mm <= 2) {
			yy--;
			mm += 12;
		}

		long jd;
		BigDecimal ret;
		if (hh < 12) {
			jd = 0;
			ret = FIX05;
		} else {
			jd = 1;
			ret = FIX_05;
		}
		ret = ret.add(
				Calc.div(new BigDecimal(hh * 3600 + mi * 60 + ss - offsetter.get().getTotalSeconds()), Calc.J86400));
		ret = addNanoSec(ret, ns);

		jd += bc_f ? (yy - 3) / 4 : yy / 4;
		if (gre_f)
			jd += (2 - yy / 100 + yy / 400);
		jd += (1720994 + yy * 365 + (mm + 1) * 30 + (mm + 1) * 3 / 5 + dd);
		ajd = ret.add(new BigDecimal(jd));
		if (ajd.signum() == -1)
			throw new AJDException("ユリウス通日基準日より過去の日付になりました。");
		ajd2Instant(ns);
	}

	/**
	 * ユリウス通日の取得。
	 * @return ユリウス通日。
	 */
	public BigDecimal getAJD() {
		return ajd;
	}

	/**
	 * thisを返します。
	 * @return this。
	 */
	public AJD toAJD() {
		return this;
	}

	/**
	 * instantの比較。
	 * 格納しているinstantのcompareToを使用します。
	 * @param jd 比較対象。
	 * @return -1:this&lt;jd(thisが過去)、0:this==jd、1:this&gt;jd(thisが未来)。
	 */
	public int compareTo(Day jd) {
		return instant.compareTo(jd.toAJD().instant);
	}

	/**
	 * 格納しているinstantのハッシュコードを返します。
	 * @return ハッシュコード。
	 */
	public int hashCode() {
		return instant.hashCode();
	}

	/**
	 * ユリウス通日の比較。
	 * {@link AJD#compareTo(Day)}が0か否かで判定します。
	 * BigDecimalのequalsは使用しません。
	 * @return true:ユリウス通日が一致、false:ユリウス通日が不一致。
	 */
	public boolean equals(Object o) {
		if (o instanceof Day)
			return compareTo((Day) o) == 0;
		return false;
	}

	/** trim用パラメータ。 */
	public enum TrimParameter {
		/** 日に丸め込む。時間以下を0に切り捨てます。 */
		DAY(true, true, true),
		/** 時間に丸め込む。分以下を0に切り捨てます。 */
		HOUR(false, true, true),
		/** 分に丸め込む。秒以下を0に切り捨てます。 */
		MINUTE(false, false, true),
		/** 秒に丸め込む。ナノ秒を0に切り捨てます。 */
		SECOND(false, false, false);

		private boolean[] flag;

		private TrimParameter(boolean... f) {
			flag = f;
		}

		private AJD trim(AJD org) {
			AJD ret = org.from(org.year, org.mon, org.day,
					flag[0] ? 0 : org.hour,
					flag[1] ? 0 : org.min,
					flag[2] ? 0 : org.sec,
					0);
			if (ret.equals(org)) {
				return org;
			}
			return ret;
		}
	}

	/**
	 * 指定精度への丸め込み。
	 * @param param 丸め込み精度。
	 * @return 丸め込み後のインスタンス。
	 */
	public AJD trim(TrimParameter param) {
		return param.trim(this);
	}

	/**
	 * 日に丸めこみ、時間を 00:00:00 にしたインスタンスを返します。
	 * {@link AJD#trim(TrimParameter)}にAJD.TrimParameter.DAYを引数にしたものと等価です。
	 * @return 当日0時のインスタンス。
	 */
	public AJD trim() {
		return trim(TrimParameter.DAY);
	}

	/**
	 * SQL用ラッパー。
	 * @return Date型。
	 */
	public java.sql.Date toDate() {
		return java.sql.Date.valueOf(toSqlString().split(" ")[0]);
	}

	/**
	 * SQL用ラッパー。
	 * @return Time型。
	 */
	public java.sql.Time toTime() {
		return java.sql.Time.valueOf(toSqlString().split(" ")[1].split("\\.")[0]);
	}

	/**
	 * SQL用ラッパー。
	 * @return Timestamp型。
	 */
	public java.sql.Timestamp toTimestamp() {
		return java.sql.Timestamp.valueOf(toSqlString());
	}
}
