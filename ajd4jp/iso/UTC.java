/*
 * AJD4JP
 * Copyright (c) 2021  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.iso;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;

import ajd4jp.AJD;
import ajd4jp.AJDException;
import ajd4jp.Day;

/**
 * AJDの振る舞いをUTCにしたものです。
 */
public class UTC extends AJD {
	/** UTCタイムゾーン */
	public static final ZoneOffset OFFSET = ZoneOffset.UTC;

	/**
	 * タイムゾーン取得。
	 * @return タイムゾーン。
	 */
	@Override
	public ZoneId getZoneId() {
		return OFFSET;
	}

	@Override
	protected AJD from(Instant ins) {
		return new UTC(ins);
	}
	@Override
	protected AJD from(int yyyy, int mm, int dd, int hh, int mi, int ss, int ns) throws AJDException {
		return new UTC(yyyy, mm, dd, hh, mi, ss, ns);
	}

	/**
	 * コンストラクタ。
	 * @param ins インスタント
	 */
	public UTC(Instant ins) {
		super(ins, ()->OFFSET);
	}
	/**
	 * コンストラクタ。現在時刻が設定されます。
	 */
	public UTC() {
		this(Instant.now());
	}
	/**
	 * コンストラクタ。引数の日時を表すインスタンスを生成します。
	 * @param calendar 日時。
	 */
	public UTC(Calendar calendar) {
		this(calendar.toInstant());
	}
	/**
	 * コンストラクタ。引数の日時を表すインスタンスを生成します。
	 * @param date 日時。
	 */
	public UTC(java.util.Date date) {
		this(date2Instant(date, OFFSET));
	}
	/**
	 * コンストラクタ。引数の日時を表すインスタンスを生成します。
	 * @param date 日時。
	 */
	public UTC(Day date) {
		this(date.toAJD().toInstant());
	}
	/**
	 * コンストラクタ。負数は指定できません。入力値の絶対値を採用します。
	 * @param num ユリウス通日。
	 */
	public UTC(Number num) {
		super(num, ()->OFFSET);
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
	 * @throws AJDException 不正な日付。
	 */
	public UTC(int yyyy, int mm, int dd, int hh, int mi, int ss, int ns) throws AJDException {
		super(yyyy, mm, dd, hh, mi, ss, ns, OFFSET);
	}
	/**
	 * コンストラクタ。
	 * @param yyyy 年。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @param hh 時。0～23。
	 * @param mi 分。0～59。
	 * @param ss 秒。0～59。
	 * @param ns ナノ秒。0～999,999,999。
	 * @throws AJDException 不正な日付。
	 */
	public UTC(Year yyyy, int mm, int dd, int hh, int mi, int ss, int ns) throws AJDException {
		this(yyyy.getAjdYear(), mm, dd, hh, mi, ss, ns);
	}

	/**
	 * コンストラクタ。
	 * @param yyyy 年。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @param hh 時。0～23。
	 * @param mi 分。0～59。
	 * @param ss 秒。0～59。
	 * @throws AJDException 不正な日付。
	 */
	public UTC(Year yyyy, int mm, int dd, int hh, int mi, int ss) throws AJDException {
		this(yyyy.getAjdYear(), mm, dd, hh, mi, ss);
	}
	/**
	 * コンストラクタ。
	 * @param yyyy 年。0は指定できません。BC1年は-1です。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @param hh 時。0～23。
	 * @param mi 分。0～59。
	 * @param ss 秒。0～59。
	 * @throws AJDException 不正な日付。
	 */
	public UTC(int yyyy, int mm, int dd, int hh, int mi, int ss) throws AJDException {
		this(yyyy, mm, dd, hh, mi, ss, 0);
	}

	/**
	 * コンストラクタ。
	 * 時間は 00:00:00 となります。
	 * @param yyyy 年。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @throws AJDException 不正な日付。
	 */
	public UTC(Year yyyy, int mm, int dd) throws AJDException {
		this(yyyy.getAjdYear(), mm, dd);
	}

	/**
	 * コンストラクタ。
	 * 時間は 00:00:00 となります。
	 * @param yyyy 年。0は指定できません。BC1年は-1です。
	 * @param mm 月。1～12。
	 * @param dd 日。1～N。
	 * @throws AJDException 不正な日付。
	 */
	public UTC(int yyyy, int mm, int dd) throws AJDException {
		this(yyyy, mm, dd, 0, 0, 0);
	}
}
