/*
 * AJD4JP
 * Copyright (c) 2011-2021  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.iso;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

import ajd4jp.AJD;
import ajd4jp.AJDException;
import ajd4jp.Day;


/**
 * {@link java.time.ZonedDateTime}をラップする
 * {@link ajd4jp.AJD}派生クラスです。<br>
 * Date and Time API（JSR-310）では、ISO-8601 に従い、
 * 先発グレゴリオ暦法を採用しています。<br>
 * AJD4JP では、ユリウス通日を骨子としているため、
 * 1582年より過去はユリウス暦法となります。このクラスも同様です。<br>
 * そのため1582年より過去では、同一の物理的な時間であっても
 * 年月日とした場合の値が双方で異なります。注意してください。
 */
public class AJD310 extends AJD {
	/** Asia/Tokyoタイムゾーン */
	public static final ZoneId JP_ZONE = ZoneId.of("Asia/Tokyo");

	private ZonedDateTime iso;
	private static String getOffsetHM(ZonedDateTime zdt, boolean std) {
		int min = zdt.getOffset().getTotalSeconds() / 60;
		if (min == 0) { return "Z"; }
		int hour = min / 60;
		boolean sub = hour < 0;
		if (sub) { hour *= -1; }
		min = min % 60;
		if (min < 0) { min *= -1; }
		String str = String.format(std? "%02d%02d": "%02d:%02d", hour, min);
		return (sub? "-": "+") + str;
	}
	/**
	 * ISO 8601形式文字列。
	 * @param std true(区切り記号なし),false(ハイフンあり)。
	 * @return 基本形式(YYYYMMDDThhmmss+nn:nn)
	 * または拡張形式(YYYY-MM-DDThh:mm:ss+nn:nn)。
	 */
	public String toIsoString(boolean std) {
		int yyyy = Math.abs(getYear());
		return (getYear() < 0? "-": "") + String.format(std?
			"%04d%02d%02dT%02d%02d%02d":
			"%04d-%02d-%02dT%02d:%02d:%02d",
			yyyy,
			getMonth(),
			getDay(),
			getHour(),
			getMinute(),
			getSecond()
		) + getOffsetHM(iso, std);
	}

	/**
	 * コンストラクタ。
	 * @param zdt 設定値
	 * @throws AJDException 例外
	 */
	protected AJD310(ZonedDateTime zdt) throws AJDException {
		super(zdt.toInstant(), ()->zdt.getOffset());
		iso = zdt;
	}

	/**
	 * インスタンス取得。
	 * @param zdt 変換元日時。
	 * @return インスタンス。
	 */
	public static AJD310 of(ZonedDateTime zdt) {
		return new AJD310(zdt);
	}

	/**
	 * {@link java.time.ZonedDateTime#withZoneSameLocal(ZoneId)}にて、ローカルタイムを維持してタイムゾーンを変更します。
	 * @param zone 変更先タイムゾーン。
	 * @return 変更後インスタンス。
	 */
	public AJD310 withZoneSameLocal(ZoneId zone) {
		return AJD310.of(toZonedDateTime().withZoneSameLocal(zone));
	}
	/**
	 * {@link java.time.ZonedDateTime#withZoneSameInstant(ZoneId)}にて、インスタントを維持してタイムゾーンを変更します。
	 * @param zone 変更先タイムゾーン。
	 * @return 変更後インスタンス。
	 */
	public AJD310 withZoneSameInstant(ZoneId zone) {
		return AJD310.of(toZonedDateTime().withZoneSameInstant(zone));
	}

	/**
	 * インスタンス取得。
	 * @param date 変換元日時。
	 * @param zone タイムゾーン。
	 * @return インスタンス。
	 */
	public static AJD310 of(java.util.Date date, ZoneId zone) {
		return of(ZonedDateTime.ofInstant(date2Instant(date, zone), zone));
	}

	/**
	 * インスタンス取得。
	 * @param calendar カレンダー。タイムゾーンはこのインスタンスのものを引き継ぎます。
	 * @return インスタンス。
	 */
	public static AJD310 of(Calendar calendar) {
		return of(ZonedDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId()));
	}

	/**
	 * タイムゾーン取得。
	 * @return タイムゾーン。
	 */
	@Override
	public ZoneId getZoneId() {
		return iso.getZone();
	}

	@Override
	protected AJD from(Instant ins) {
		return of(ZonedDateTime.ofInstant(ins, iso.getZone()));
	}
	@Override
	protected AJD from(int yyyy, int mm, int dd, int hh, int mi, int ss, int ns) throws AJDException {
		return of(ZonedDateTime.of(yyyy, mm, dd, hh, mi, ss, ns, iso.getZone()));
	}

	/**
	 * インスタンス取得。
	 * @param day 変換元日時。
	 * @param zone タイムゾーン。
	 * @return インスタンス。
	 */
	public static AJD310 of(Day day, ZoneId zone) {
		return of(ZonedDateTime.ofInstant(Instant.ofEpochMilli(day.toAJD().getTime()), zone));
	}

	/**
	 * 現在時刻のインスタンス取得。
	 * @param zone タイムゾーン。
	 * @return インスタンス。
	 */
	public static AJD310 now(ZoneId zone) {
		return of(ZonedDateTime.now(zone));
	}

	/**
	 * インスタンス取得。
	 * @param yyyy 年。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @param hh 時。0～23。
	 * @param mi 分。0～59。
	 * @param ss 秒。0～59。
	 * @param ns ナノ秒。0～999,999,999。
	 * @return インスタンス。
	 * @throws DateTimeException 不正な日付。
	 */
	public static AJD310 of(Year yyyy, int mm, int dd, int hh, int mi, int ss, int ns) throws DateTimeException {
		return of(ZonedDateTime.of(yyyy.getAjdYear(), mm, dd, hh, mi, ss, ns, yyyy.getZoneId()));
	}
	/**
	 * インスタンス取得。
	 * @param yyyy 年。0は指定できません。BC1年は-1です。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @param hh 時。0～23。
	 * @param mi 分。0～59。
	 * @param ss 秒。0～59。
	 * @param ns ナノ秒。0～999,999,999。
	 * @param zone タイムゾーン。
	 * @return インスタンス。
	 * @throws DateTimeException 不正な日付。
	 */
	public static AJD310 of(int yyyy, int mm, int dd, int hh, int mi, int ss, int ns, ZoneId zone) throws DateTimeException {
		if (yyyy == 0) { throw new AJDException("年が0です。"); }
		return of(ZonedDateTime.of(yyyy < 0? yyyy + 1: yyyy, mm, dd, hh, mi, ss, ns, zone));
	}

	/**
	 * インスタンス取得。
	 * @param yyyy 年。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @param hh 時。0～23。
	 * @param mi 分。0～59。
	 * @param ss 秒。0～59。
	 * @return インスタンス。
	 * @throws DateTimeException 不正な日付。
	 */
	public static AJD310 of(Year yyyy, int mm, int dd, int hh, int mi, int ss) throws DateTimeException {
		return of(yyyy.getAjdYear(), mm, dd, hh, mi, ss, yyyy.getZoneId());
	}
	/**
	 * インスタンス取得。
	 * @param yyyy 年。0は指定できません。BC1年は-1です。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @param hh 時。0～23。
	 * @param mi 分。0～59。
	 * @param ss 秒。0～59。
	 * @param zone タイムゾーン。
	 * @return インスタンス。
	 * @throws DateTimeException 不正な日付。
	 */
	public static AJD310 of(int yyyy, int mm, int dd, int hh, int mi, int ss, ZoneId zone) throws DateTimeException {
		return of(yyyy, mm, dd, hh, mi, ss, 0, zone);
	}

	/**
	 * インスタンス取得。
	 * 時間は 00:00:00 となります。
	 * @param yyyy 年。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @return インスタンス。
	 * @throws AJDException 不正な日付。
	 */
	public static AJD310 of(Year yyyy, int mm, int dd) throws AJDException {
		return of(yyyy.getAjdYear(), mm, dd, yyyy.getZoneId());
	}
	/**
	 * インスタンス取得。
	 * 時間は 00:00:00 となります。
	 * @param yyyy 年。0は指定できません。BC1年は-1です。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @param zone タイムゾーン。
	 * @return インスタンス。
	 * @throws AJDException 不正な日付。
	 */
	public static AJD310 of(int yyyy, int mm, int dd, ZoneId zone) throws AJDException {
		try {
			return of(yyyy, mm, dd, 0, 0, 0, zone);
		}
		catch(AJDException ae) { throw ae; }
		catch(Exception etc) { throw new AJDException(etc); }
	}

	/**
	 * このAJDを、ZonedDateTimeに変換する。
	 * @return ZonedDateTime。
	 */
	public ZonedDateTime toZonedDateTime() {
		return iso;
	}

	/**
	 * 配列のタイムゾーン変換。
	 * @param src 変換元。
	 * @param zone タイムゾーン。
	 * @return 変換後配列。
	 */
	public static AJD310[] to(AJD[] src, ZoneId zone) {
		AJD310[] ret = new AJD310[src.length];
		for (int i = 0; i < src.length; i++) {
			ret[i] = AJD310.of(src[i], zone);
		}
		return ret;
	}
}


