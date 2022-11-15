/*
 * AJD4JP
 * Copyright (c) 2011-2019  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp;

/**
 * 年齢算出クラス。<br>
 * 数え年と満年齢を算出します。
 */
public class Age {
	private AJD start;
	/**
	 * コンストラクタ。
	 * @param birthday 誕生日。
	 */
	public Age(Day birthday) {
		start = new AJD(birthday.getAJD()).trim();
	}

	private static int get(AJD birthday, AJD target, int sub) {
		AJD s = new AJD(birthday).trim();
		AJD last = new AJD(s).addDay(sub);
		AJD e = new AJD(target).trim();
		int sub_year = 0;
		while(true) {
			sub_year = s.getYear() - last.getYear();
			try {
				last = new AJD(e.getYear(), last.getMonth(), last.getDay());
			}
			catch(Exception ex) {
				last = last.addDay(1);
				continue;
			}
			break;
		}
		int ret = e.getYear() - s.getYear() - 1 + sub_year;
		if (last.compareTo(e) <= 0) { ret++; }
		if (ret < 0) { ret = 0; }
		return ret;
	}

	private static int calcAsia(int s, int e) {
		int ret = e - s + 1;
		if (ret < 1) { ret = 1; }
		return ret;
	}
	private int getS(AJD end) {
		return calcAsia(start.getYear(), end.getYear());
	}
	private int getLS(AJD end) {
		return calcAsia(
			LunisolarYear.getLunisolarYear(start).getYear(),
			LunisolarYear.getLunisolarYear(end).getYear()
		);
	}
	private int getST(AJD end) {
		return calcAsia(
			new STCD(start).getYear(),
			new STCD(end).getYear()
		);
	}

	/**
	 * 計算方法。
	 */
	public enum Reckoning {
		/**
		 * 日付起算の満年齢(誕生日前日に加齢)。<br>
		 * 主な日本の法律(就学、選挙権、年金等)ではこの方式に依ります。
		 */
		DAY,
		/**
		 * 時間起算の満年齢(誕生日当日に加齢)。<br>
		 * 一般的な方式です。
		 */
		TIME,
		/**
		 * 数え年(西暦起算)。<br>
		 * 還暦や厄年の年齢に使用します。
		 */
		ASIA_SOLAR,
		/** 数え年(旧暦起算)。<br>
		 * 現在では西暦起算が一般的です。
		 */
		ASIA_LUNISOLAR,
		/** 数え年(節月起算)。 <br>
		 * 現在では西暦起算が一般的です。
		 */
		ASIA_SOLAR_TERM;
	}

	/**
	 * 年齢を算出します。
	 * @param type 計算方法。
	 * @param target 年齢を取得したい日。
	 * @return 年齢。
	 */
	public int getAge(Reckoning type, Day target) {
		AJD end = new AJD(target.getAJD()).trim();
		switch(type) {
		case DAY:
			return get(start, end, -1);
		case TIME:
			return get(start, end, 0);
		case ASIA_SOLAR:
			return getS(end);
		case ASIA_LUNISOLAR:
			return getLS(end);
		case ASIA_SOLAR_TERM:
			return getST(end);
		}
		return -1;
	}
	/**
	 * 年齢に達する日付を返します。
	 * @param type 計算方法。
	 * @param age 年齢。
	 * @return 日付。
	 */
	public AJD getDate(Reckoning type, int age) {
		switch(type) {
		case DAY:
		case TIME:
			if (age == 0) { return start; }
			if (age > 0) {
				int y = start.getYear() + age;
				AJD ret = start;
				while(true) {
					try {
						ret = new AJD(y, ret.getMonth(), ret.getDay());
						break;
					}
					catch(Exception e) {}
					ret = ret.addDay(1);
				}
				if (type == Reckoning.TIME) { return ret; }
				return ret.addDay(-1);
			}
			break;
		case ASIA_SOLAR:
			if (age == 1) { return start; }
			if (age > 1) {
				try {
					return new AJD(start.getYear() + age - 1, 1, 1);
				}catch(Exception e) {}
			}
			break;
		case ASIA_LUNISOLAR:
			if (age == 1) { return start; }
			if (age > 1) {
				int y = LunisolarYear.getLunisolarYear(start).getYear() + age - 1;
				if (y == 0) { y = 1; }
				try {
					return LunisolarYear.getLunisolarYear(y).getLSCD(1, false, 1).toAJD();
				}
				catch(Exception e) {}
			}
			break;
		case ASIA_SOLAR_TERM:
			if (age == 1) { return start; }
			if (age > 1) {
				for (int sy = start.getYear() + age - 2; ; sy++) {
					if (sy == 0) { continue; }
					try {
						AJD[] dy = STCD.Doyo.SPRING.getDoyo(sy);
						AJD fst = dy[dy.length - 1].addDay(1);
						if (getAge(type, fst) == age) { return fst; }
					}
					catch(Exception e) { break; }
				}
			}
			break;
		}
		return null;
	}
}


