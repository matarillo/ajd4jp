/*
 * AJD4JP
 * Copyright (c) 2011-2017  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.iso;
import ajd4jp.*;
import ajd4jp.util.*;
import java.util.*;
import java.math.*;
import java.time.*;
import java.time.temporal.*;


/**
 * java.time 変換。<br>
 * {@link java.time.ZonedDateTime}をラップする
 * {@link ajd4jp.AJD}派生クラスです。<br>
 * 内部では {@link java.util.Date} へ丸め込みを挟むため、マイクロ秒情報は
 * 損失しますので注意してください。<br><br>
 * Date and Time API（JSR-310）では、ISO-8601 に従い、
 * 先発グレゴリオ暦法を採用しています。<br>
 * AJD4JP では、ユリウス通日を骨子としているため、
 * 1582年より過去はユリウス暦法となります。このクラスも同様です。<br>
 * そのため1582年より過去では、同一の物理的な時間であっても
 * 年月日とした場合の値が双方で異なります。注意してください。
 */
public class AJD310 extends AJD {
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
		return String.format(std?
			"%04d%02d%02dT%02d%02d%02d":
			"%04d-%02d-%02dT%02d:%02d:%02d",
			getYear(),
			getMonth(),
			getDay(),
			getHour(),
			getMinute(),
			getSecond()
		) + getOffsetHM(iso, std);
	}

	private static BigDecimal getOffset(ZonedDateTime zdt) {
		if (zdt == null) { return Calc.JP_H; }
		return Calc.div(new BigDecimal(zdt.getOffset().getTotalSeconds()), Calc.J86400);
	}
	private AJD310(ZonedDateTime zdt) {
		super(Date.from(zdt.toInstant()), getOffset(zdt));
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
	 * インスタンス取得。
	 * @param date 変換元日時。
	 * @param zone タイムゾーン。
	 * @return インスタンス。
	 */
	public static AJD310 of(Date date, ZoneId zone) {
		return of(ZonedDateTime.ofInstant(date.toInstant(), zone));
	}

	@Override
	protected AJD from(Number num) {
		return of(super.from(num), iso.getZone());
	}
	@Override
	protected AJD from(int yyyy, int mm, int dd, int hh, int mi, int ss) throws AJDException {
		return new AJD310(yyyy, mm, dd, hh, mi, ss, getOffset(iso));
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

	private AJD310(int yyyy, int mm, int dd, int hh, int mi, int ss, BigDecimal offset) throws AJDException {
		super(yyyy, mm, dd, hh, mi, ss, offset);
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
		return of(ZonedDateTime.of(yyyy < 0? yyyy + 1: yyyy, mm, dd, hh, mi, ss, 0, zone));
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
		return of(yyyy, mm, dd, 0, 0, 0, zone);
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


