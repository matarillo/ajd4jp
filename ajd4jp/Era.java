/*
 * AJD4JP
 * Copyright (c) 2011-2021  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp;

import java.util.HashMap;

import ajd4jp.format.JapaneseYearF;


class EraWork {
	static AJD[][] st_ed;
	static {
		st_ed = new AJD[5][];
		try {
			st_ed[0] = new AJD[] {
				new AJD( 1868, 9, 8 ), new AJD( 1912, 7, 30 )
			};
			st_ed[1] = new AJD[] {
				new AJD( 1912, 7, 30 ), new AJD( 1926, 12, 25 )
			};
			st_ed[2] = new AJD[] {
				new AJD( 1926, 12, 25 ), new AJD( 1989, 1, 7 )
			};
			st_ed[3] = new AJD[] {
				new AJD( 1989, 1, 8 ), new AJD( 2019, 4, 30 )
			};
			st_ed[4] = new AJD[] {
				new AJD( 2019, 5, 1 ), null
			};
		}
		catch( AJDException e ) {}
	}
}

/**
 * 和暦の元号。明治以降の元号を持ちます。<br>
 * 明治6年より過去は太陽暦ではないため、正しい対応とはなりません。<br>
 * 昭和以前では、前元号の最終日と次元号の開始日が同一日です。このクラスで該当日を判定する場合は、未来の元号(大正と昭和なら昭和)を採用します。
 */
public enum Era {
	/** 明治 */
	MEIJI("明治", EraWork.st_ed[0][0], EraWork.st_ed[0][1], "M", "Ｍ"),
	/** 大正 */
	TAISHO("大正", EraWork.st_ed[1][0], EraWork.st_ed[1][1], "T", "Ｔ"),
	/** 昭和 */
	SHOWA("昭和", EraWork.st_ed[2][0], EraWork.st_ed[2][1], "S", "Ｓ"),
	/** 平成 */
	HEISEI("平成", EraWork.st_ed[3][0], EraWork.st_ed[3][1], "H", "Ｈ"),
	/** 令和 */
	REIWA("令和", EraWork.st_ed[4][0], EraWork.st_ed[4][1], "R", "Ｒ"),
	;

	private HashMap<JapaneseYearF.Era, String> name = new HashMap<JapaneseYearF.Era, String>();
	private AJD	start, end = null;
	private Era(String n, AJD s, AJD e, String h, String f) {
		start = s;
		end = e;
		name.put(JapaneseYearF.Era.KANJI, n);
		name.put(JapaneseYearF.Era.HALF, h);
		name.put(JapaneseYearF.Era.FULL, f);
	}

	static Era get( Day ajd ) {
		final Era[]	era = values();
		for ( int i = era.length - 1; i >= 0; i-- ) {
			if ( era[i].start.compareTo( ajd ) <= 0 )	return era[i];
		}
		return null;
	}

	/**
	 * 和暦年の取得。
	 * @param yyyy 西暦年。
	 * @param mm 月。
	 * @param dd 日。
	 * @return 和暦年。
	 * @throws AJDException 不正な日付。
	 */
	public static Era.Year getEra( int yyyy, int mm, int dd ) throws AJDException {
		return new Era.Year( new AJD( yyyy, mm, dd ) );
	}

	/**
	 * 元号名の取得。getName(JapaneseYearF.Era.KANJI) を戻します。
	 * @return 明治など。
	 */
	public String getName() { return getName(JapaneseYearF.Era.KANJI); }
	/**
	 * 元号名の取得。
	 * @param e 取得するパターン。
	 * @return 元号名。
	 */
	public String getName(JapaneseYearF.Era e) { return name.get(e); }
	/**
	 * 開始日の取得。
	 * @return 開始日。
	 */
	public AJD getStart() { return start; }
	/**
	 * 終了日の取得。
	 * @return 終了日。終了未定の元号はnullを返します。
	 */
	public AJD getEnd() { return end; }

	/**
	 * 日付範囲判定。指定日付が元号の期間中のものか判定します。
	 * @param date 判定日付。
	 * @return true:その元号範囲、false:その元号の範囲外。
	 */
	public boolean isValid( AJD date ) {
		if ( date == null )	return false;
		date = date.trim();
		if ( start.compareTo( date ) > 0 )	return false;
		if ( end == null )	return true;
		if ( end.compareTo( date ) < 0 )	return false;
		return true;
	}

	/**
	 * 西暦での年の取得。
	 * @param yy 元号での年。
	 * @return yyに対応する西暦年。元号の範囲を超過する年でも値を返します。
	 */
	public int getAD( int yy ) {
		return start.getYear() + yy - 1;
	}

	/**
	 * 和暦での年の取得。
	 * @param yyyy 西暦での年。
	 * @return yyyyに対応する年。元号の範囲を超過する年でも値を返します。
	 */
	public int getJP( int yyyy ) {
		return yyyy - start.getYear() + 1;
	}

	/**
	 * 和暦の年を表します。
	 */
	public static class Year implements java.io.Serializable {
		private static final long serialVersionUID = 1;
		private Era era;
		private int yy;

		private Year() {}
		/**
		 * コンストラクタ。
		 * @param ajd 生成対象日。
		 */
		public Year( Day ajd ) {
			era = Era.get( ajd );
			int y4 = ajd.getYear();
			yy = ( era == null )?	y4:	era.getJP( y4 );
		}
		/**
		 * 元号の取得。
		 * @return 元号。明治より前はnullです。
		 */
		public Era getEra() { return era; }
		/**
		 * 年の取得。
		 * @return 年。明治より前は西暦で返します。
		 */
		public int getYear() { return yy; }
		/**
		 * 文字列取得。
		 * @return 元号yy年のフォーマット。
		 */
		public String toString() {
			return String.format( "%s%d年", ( era == null )? "": era.getName(), yy );
		}
	}
}

