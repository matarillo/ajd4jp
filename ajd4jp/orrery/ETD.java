/*
 * AJD4JP
 * Copyright (c) 2011-2020  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.orrery;

import java.math.BigDecimal;
import java.time.ZoneOffset;

import ajd4jp.AJD;
import ajd4jp.Day;
import ajd4jp.iso.AJD310;
import ajd4jp.util.Calc;

/**
 * TT(地球時)での日付。<br>
 * 天体位置計算で使用する時刻系は50年間で、ET(暦表時)・TDT(地球力学時)・TTと
 * 変化してきました。今後も変化する可能性があるため、このクラス名としています。<br>
 * 現在、閏秒等を挟むUTC(協定世界時)とTTとは1分少々の差があり、西暦年により
 * その差が異なります。<br>
 * そのため、UTCのまま計算するとTTを基にした天体位置と誤差が生じます。<br>
 * その誤差調整を行うための日付クラスです。<br>
 * 本来、太陽系を扱う場合は、TDB(太陽系力学時)またはTCB(太陽系座標時)等で
 * 計算すべきところですが、TTとTDBの差はミリ秒オーダーである上、
 * UTCからの正確な変換も難しいため、このクラスでは地球時として扱っています。
 */
public class ETD implements Day {
	private static final long serialVersionUID = 1;

	private AJD ajd;
	private AJD jed;

	/**
	 * コンストラクタ。現在時刻が設定されます。
	 */
	public ETD() {
		this(new AJD());
	}

	/**
	 * 地球時直接設定コンストラクタ。
	 * 地球時が明確に判明している場合に使用して下さい。
	 * @param jed 地球時のユリウス通日。
	 */
	public ETD(BigDecimal jed) {
		this.jed = new AJD(jed);
		this.ajd = new AJD(jed.subtract(makeT(this.jed.getYear(), this.jed.getMonth())));
	}

	/**
	 * 協定世界時変換コンストラクタ。
	 * 簡易的な計算で、地球時の近似値を求めます。<br>
	 * 日本国内であっても経度の指定により、細かい時差を割り当てます。<br>
	 * 例えば旭川市と石垣島では、実質１時間以上の時差があります。
	 * @param ajd 協定世界時のユリウス通日。
	 * @param longitude 経度補正。
	 */
	public ETD(AJD ajd, Angle longitude) {
		if (longitude != null) {
			longitude = longitude.convert(Angle.Unit.DEGREE);
			BigDecimal loc_sec = Calc.mul(longitude.h, 60 * 60 / 15);
			ajd = AJD310.of(ajd, ZoneOffset.UTC);
			ajd = AJD310.of(ajd, ZoneOffset.ofTotalSeconds(loc_sec.intValue()));
		}
		this.ajd = ajd;
		this.jed = new AJD(ajd.getAJD().add(makeT(ajd.getYear(), ajd.getMonth())));
	}

	/**
	 * 協定世界時変換コンストラクタ。
	 * 簡易的な計算で、地球時の近似値を求めます。<br>
	 * 日本時間をそのまま使用し、経度補正は行いません。
	 * @param ajd 協定世界時のユリウス通日。
	 */
	public ETD(AJD ajd) {
		this(ajd, null);
	}

	/**
	 * 地球時でのユリウス通日(近似値)の取得。
	 * @return 地球時でのユリウス通日。
	 */
	public BigDecimal getJED() {
		return jed.getAJD();
	}

	/**
	 * 協定世界時でのユリウス通日(近似値)の取得。
	 * @return 地球時でのユリウス通日。
	 */
	public BigDecimal getAJD() {
		return ajd.getAJD();
	}

	private static final BigDecimal T2000 = new BigDecimal(2451545),
			J100 = new BigDecimal(36525);

	/**
	 * J2000.0基準のユリウス世紀数の取得。
	 * @return 世紀数
	 */
	public BigDecimal get20C() {
		return Calc.div(getJED().subtract(T2000), J100);
	}

	/**
	 * 協定世界時のAJD(近似値)を返します。
	 * @return 協定世界時。
	 */
	public AJD toAJD() {
		return ajd;
	}

	public int getYear() {
		return jed.getYear();
	}

	public int getMonth() {
		return jed.getMonth();
	}

	public int getDay() {
		return jed.getDay();
	}

	public int getHour() {
		return jed.getHour();
	}

	public int getMinute() {
		return jed.getMinute();
	}

	public int getSecond() {
		return jed.getSecond();
	}

	/**
	 * ユリウス通日の比較。
	 * ユリウス通日を格納しているBigDecimalのcompareToを使用します。
	 * @param jd 比較対象。
	 * @return -1:this&lt;jd(thisが過去)、0:this==jd、1:this&gt;jd(thisが未来)。
	 */
	public int compareTo(Day jd) {
		if (jd instanceof ETD)
			return jed.compareTo(((ETD) jd).jed);
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
		if (o instanceof ETD)
			return jed.equals(((ETD) o).jed);
		return ajd.equals(o);
	}

	/**
	 * 文字列化。
	 * @return yyyy/mm/dd hh:mm:ss形式のフォーマットで返します。
	 */
	public String toString() {
		return jed.toString();
	}

	private static double pow(double org, int cnt) {
		double ret = 1;
		for (int i = 0; i < cnt; i++) {
			ret *= org;
		}
		return ret;
	}

	private static BigDecimal makeT(int year, int month) {
		double t = _makeT(year, month);
		return Calc.div(new BigDecimal(t), Calc.J86400);
	}

	private static double _makeT(int year, int month) {
		double u, t, y = year + (month - 0.5) / 12;

		if (year < -500) {
			u = (year - 1820) / 100;
			return -20 + 32 * pow(u, 2);
		}
		if (year < 500) {
			u = y / 100;
			return 10583.6 - 1014.41 * u + 33.78311 * pow(u, 2) - 5.952053 * pow(u, 3) - 0.1798452 * pow(u, 4)
					+ 0.022174192 * pow(u, 5) + 0.0090316521 * pow(u, 6);
		}
		if (year < 1600) {
			u = (y - 1000) / 100;
			return 1574.2 - 556.01 * u + 71.23472 * pow(u, 2) + 0.319781 * pow(u, 3) - 0.8503463 * pow(u, 4)
					- 0.005050998 * pow(u, 5) + 0.0083572073 * pow(u, 6);
		}
		if (year < 1700) {
			t = y - 1600;
			return 120 - 0.9808 * t - 0.01532 * pow(t, 2) + pow(t, 3) / 7129;
		}
		if (year < 1800) {
			t = y - 1700;
			return 8.83 + 0.1603 * t - 0.0059285 * pow(t, 2) + 0.00013336 * pow(t, 3) - pow(t, 4) / 1174000;
		}
		if (year < 1860) {
			t = y - 1800;
			return 13.72 - 0.332447 * t + 0.0068612 * pow(t, 2) + 0.0041116 * pow(t, 3) - 0.00037436 * pow(t, 4)
					+ 0.0000121272 * pow(t, 5) - 0.0000001699 * pow(t, 6) + 0.000000000875 * pow(t, 7);
		}
		if (year < 1900) {
			t = y - 1860;
			return 7.62 + 0.5737 * t - 0.251754 * pow(t, 2) + 0.01680668 * pow(t, 3) - 0.0004473624 * pow(t, 4)
					+ pow(t, 5) / 233174;
		}
		if (year < 1920) {
			t = y - 1900;
			return -2.79 + 1.494119 * t - 0.0598939 * pow(t, 2) + 0.0061966 * pow(t, 3) - 0.000197 * pow(t, 4);
		}
		if (year < 1941) {
			t = y - 1920;
			return 21.20 + 0.84493 * t - 0.076100 * pow(t, 2) + 0.0020936 * pow(t, 3);
		}
		if (year < 1961) {
			t = y - 1950;
			return 29.07 + 0.407 * t - pow(t, 2) / 233 + pow(t, 3) / 2547;
		}
		if (year < 1986) {
			t = y - 1975;
			return 45.45 + 1.067 * t - pow(t, 2) / 260 - pow(t, 3) / 718;
		}
		if (year < 2005) {
			t = y - 2000;
			return 63.86 + 0.3345 * t - 0.060374 * pow(t, 2) + 0.0017275 * pow(t, 3) + 0.000651814 * pow(t, 4)
					+ 0.00002373599 * pow(t, 5);
		}
		if (year < 2050) {
			t = y - 2000;
			return 62.92 + 0.32217 * t + 0.005589 * pow(t, 2);
		}
		if (year < 2150) {
			return -20 + 32 * pow((y - 1820) / 100, 2) - 0.5628 * (2150 - y);
		}
		u = (year - 1820) / 100;
		return -20 + 32 * pow(u, 2);
	}
}
