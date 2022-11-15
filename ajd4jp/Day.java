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


/**
 * 一日を表します。
 */
public interface Day extends Comparable<Day>, java.io.Serializable {
	/**
	 * ユリウス通日の取得。
	 * @return ユリウス通日。
	 */
	public BigDecimal getAJD();
	/**
	 * AJDの取得。
	 * @return AJD。
	 */
	public AJD toAJD();

	/**
	 * 比較。
	 * @param jd 比較対象。
	 * @return 比較結果。
	 */
	public int compareTo( Day jd );

	/**
	 * タイムゾーン取得。
	 * @return 常に+9時間を返します。
	 */
	default public ZoneId getZoneId() {
		return AJD.OFFSET;
	}
	/**
	 * 年の取得。
	 * @return 年。
	 */
	default public Year toYear() { return Year.fromAJD(getYear(), getZoneId()); }
	/**
	 * 年の取得。
	 * @return 年。0は返りません。
	 */
	public int getYear();
	/**
	 * 月の取得。
	 * @return 月。1～12。
	 */
	public int getMonth();
	/**
	 * 日の取得。
	 * @return 日。1～N。
	 */
	public int getDay();
	/**
	 * 時の取得。
	 * @return 時。0～23。
	 */
	public int getHour();
	/**
	 * 分の取得。
	 * @return 分。0～59。
	 */
	public int getMinute();
	/**
	 * 秒の取得。
	 * @return 秒。0～59。
	 */
	public int getSecond();
}

