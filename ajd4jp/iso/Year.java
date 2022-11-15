/*
 * AJD4JP
 * Copyright (c) 2021  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.iso;

import java.io.Serializable;
import java.time.ZoneId;

import ajd4jp.AJD;
import ajd4jp.AJDException;
import ajd4jp.Month;
import ajd4jp.Span;
import ajd4jp.util.AjdFactory;

/**
 * 年表現。紀元前後を跨ぐ演算を安全に行えます。
 */
public class Year implements Serializable, Comparable<Year> {
	private static final long serialVersionUID = 1;
	private short val;
	private ZoneId zone;
	private Year() {}
	private Year(int yyyy, ZoneId z) {
		val = (short) yyyy;
		zone = z;
	}

	/**
	 * AJD4JP仕様で年の生成。0年は指定できません。紀元前1年は -1 を指定します。
	 * @param yyyy 年
	 * @param zone タイムゾーン。
	 * @return 生成インスタンス
	 * @throws AJDException 0年を指定した場合。
	 */
	public static Year fromAJD(int yyyy, ZoneId zone) throws AJDException {
		if (yyyy == 0) { throw new AJDException("年が0です。"); }
		if (yyyy < 0) { yyyy++; }
		return new Year(yyyy, zone);
	}

	/**
	 * ISO-8601仕様で年の生成。0は紀元前1年を表します。-1は紀元前2年を表します。
	 * @param yyyy 年
	 * @param zone タイムゾーン。
	 * @return 生成インスタンス
	 */
	public static Year fromISO(int yyyy, ZoneId zone) {
		return new Year(yyyy, zone);
	}

	/**
	 * AJD4JP仕様での年。0は戻りません。紀元前1年は-1です。
	 * @return 年。
	 */
	public int getAjdYear() {
		if (val < 1) { return val - 1; }
		return val;
	}

	/**
	 * ISO-8601仕様での年。紀元前1年は0です。紀元前2年は-1です。
	 * @return 年。
	 */
	public int getIsoYear() { return val; }

	/**
	 * タイムゾーン取得。
	 * @return タイムゾーン。
	 */
	public ZoneId getZoneId() { return zone; }

	/**
	 * 指定月の取得
	 * @param mm 月。1～12。
	 * @return 月。
	 * @throws AJDException 無効な月。
	 */
	public Month getMonth(int mm) throws AJDException {
		return new Month(this, mm);
	}
	/**
	 * 12ヶ月の取得。
	 * @return 月。
	 */
	public Month[] getMonth() {
		Month[] mon = new Month[12];
		for (int i = 0; i < mon.length; i++) { mon[i] = getMonth(i + 1); }
		return mon;
	}

	/**
	 * 1月1日を返す。
	 * @return 日。
	 */
	public AJD getFirstDay() { return AjdFactory.makeAJD(getAjdYear(), 1, 1, zone); }
	/**
	 * その年の第N日目を返します。
	 * @param no 1以上。
	 * @return 日。その年から外れるとnull。
	 */
	public AJD getDay(int no) {
		AJD ret = getFirstDay().addDay(no - 1);
		if (ret.getYear() != getAjdYear()) { return null; }
		return ret;
	}
	/**
	 * その年の第N日目であるかを返します。タイムゾーンは無視します。
	 * @param ajd 日
	 * @return 第N日。1以上が正常値。年が異なる場合0を返す。
	 */
	public int dayOfYear(AJD ajd) {
		if (ajd.getYear() != getAjdYear()) { return 0; }
		return new Span(ajd.trim(), getFirstDay()).getDayPart() + 1;
	}
	/**
	 * 12月31日を返す。
	 * @return 日。
	 */
	public AJD getLastDay() { return add(1).getFirstDay().addDay(-1); }
	/**
	 * 12月31日が第N日目であるかを返します。
	 * @return 365か366。
	 */
	public int lastDayOfYear() { return dayOfYear(getLastDay()); }

	/**
	 * 指定年数を加算した年を返す。
	 * @param year 加算年。負数可。
	 * @return 年。
	 */
	public Year add(int year) { return new Year(val + year, zone); }

	@Override
	public int compareTo(Year o) {
		return this.val - o.val;
	}

	@Override
	public int hashCode() { return val; }

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Year)) { return false; }
		Year anti = (Year)o;
		return anti.compareTo(this) == 0 && anti.zone.equals(this.zone);
	}

	@Override
	public String toString() {
		int year = getAjdYear();
		return (year < 0? "紀元前": "西暦") + Math.abs(year) + "年";
	}
}
