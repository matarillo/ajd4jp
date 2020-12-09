/*
 * AJD4JP
 * Copyright (c) 2011-2012  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp;

/**
 * 年度。会計年度のような、年の開始が1/1でない期間を扱います。
 */
public class FiscalYear implements java.io.Serializable {
	private static final long serialVersionUID = 1;
	private static AJD	START4 = null;
	static {
		try{
			START4 = new AJD( 2000, 4, 1 );
		}
		catch( AJDException e ){}
	}

	private int start_m, start_d, start_h;
	private Boolean mon_f;
	private Boolean day_f;
	private Boolean hour_f;

	/**
	 * コンストラクタ。<br>
	 * new FiscalYear(new AJD(2000, 4, 1), true) と等価です。<br>
	 * 会計年度のような4/1を開始日とする一般的な年度を構築します。
	 */
	public FiscalYear() {
		this( START4, true );
	}

	/**
	 * コンストラクタ。<br>
	 * 開始日と、年を開始日で取るか終了日で取るかを指定します。<br><br>例
	 * <ul>
	 * <li>醸造・酒造年度(7/1～) 年は開始日<br>
	 * new FiscalYear(new AJD(2000, 7, 1), true)<br>
	 * 2000/6/30は、1999年度6/30です。<br>
	 * 2000/7/1 は、2000年度7/1 です。
	 * <li>羊毛年度(7/1～) 年は終了日<br>
	 * new FiscalYear(new AJD(2000, 7, 1), false)<br>
	 * 2000/6/30は、2000年度6/30です。<br>
	 * 2000/7/1 は、2001年度7/1 です。
	 * </ul>
	 * new FiscalYear(start_ajd, true, null, null) と等価です。<br>
	 * @param start_day 年度開始日。指定日の月と日を判定条件に使用します。
	 * @param year_of_start_day true:年は開始日と同じ、false:年は終了日と同じ。
	 */
	public FiscalYear( AJD start_day, boolean year_of_start_day ) {
		this( start_day, year_of_start_day, null, null );
	}

	/**
	 * コンストラクタ。<br>
	 * このコンストラクタは年度に加え、月を締める日と、日を締める時間を
	 * 設定できます。<br>
	 * 各フラグのtrue/falseは{@link FiscalYear#FiscalYear(AJD,boolean)}と
	 * 同じ考え方で、
	 * <ul>
	 * <li>year_of_start_dayがnullでなければ、start_dayの月と日を、年の切り替え
	 * 日として使用する。
	 * <br>nullなら、年は暦と同じ。
	 * <li>month_of_start_dayがnullでなければ、start_dayの日を、月の切り替え日
	 * として使用する。
	 * <br>nullなら、月は暦と同じ。
	 * <li>day_of_start_hourがnullでなければ、start_dayの時を、日の切り替え時
	 * として使用する。
	 * <br>nullなら、日は暦と同じ。
	 * </ul>
	 * のように扱います。必要な判定箇所にnull以外を与えて下さい。
	 * @param start_day 年度開始日。
	 * 各フラグがnullでなければ、指定日の月と日と時を判定条件に使用します。
	 * @param year_of_start_day true:年は開始日と同じ、false:年は終了日と同じ。
	 * @param month_of_start_day true:月は開始日と同じ、false:月は終了日と同じ。
	 * @param day_of_start_hour true:日は開始時と同じ、false:日は終了時と同じ。
	 */
	public FiscalYear( AJD start_day, Boolean year_of_start_day, Boolean month_of_start_day, Boolean day_of_start_hour ) {
		start_m = start_day.getMonth();
		start_d = start_day.getDay();
		start_h = start_day.getHour();
		mon_f = year_of_start_day;
		day_f = month_of_start_day;
		hour_f = day_of_start_hour;
	}


	private AJD getStart( int y, int m, int d, int h ) {
		try {
			return new AJD( y, m, d, h, 0, 0 );
		}
		catch( AJDException e ) {}
		return getStart( y, m, d - 1, h );
	}

	int[] getDate( AJD ajd ) {
		ajd = getDay( ajd );
		Month	m = getMonth( ajd );
		ajd = getStart( m.getYear(), m.getMonth(), ajd.getDay(), ajd.getHour() );
		return new int[] { getYear( ajd ), m.getMonth(), ajd.getDay() };
	}

	private int getYear( AJD ajd ) {
		int	y = ajd.getYear();
		if ( mon_f == null )	return y;

		AJD	st = getStart( y, start_m, start_d, ajd.getHour() );
		if ( st.compareTo( ajd ) > 0 && mon_f )	{
			y--;
			if ( y == 0 )	y--;
		}
		else if ( st.compareTo( ajd ) <= 0 && !mon_f ) {
			y++;
			if ( y == 0 )	y++;
		}
		return y;
	}

	private Month getMonth( AJD ajd ) {
		int	y = ajd.getYear();
		int m = ajd.getMonth();
		Month	ret = new Month( ajd );
		if ( day_f == null )	return ret;

		AJD	st = getStart( y, m, start_d, ajd.getHour() );
		if ( st.compareTo( ajd ) > 0 && day_f )	return ret.add( -1 );
		else if ( st.compareTo( ajd ) <= 0 && !day_f )	return ret.add( 1 );
		return ret;
	}

	private AJD getDay( AJD ajd ) {
		if ( hour_f == null )	return ajd;
		int	y = ajd.getYear();
		int m = ajd.getMonth();
		int d = ajd.getDay();

		AJD	st = getStart( y, m, d, start_h );
		if ( st.compareTo( ajd ) > 0 && hour_f )	return ajd.addDay( -1 );
		else if ( st.compareTo( ajd ) <= 0 && !hour_f )	return ajd.addDay( 1 );
		return ajd;
	}

	/**
	 * 年度換算日の取得。
	 * @param day 換算元。
	 * @return 換算日。
	 */
	public FYD getFYD( AJD day ) {
		return new FYD( this, day );
	}

	/**
	 * 指定日の取得。時間は00:00:00になります。
	 * @param yyyy 年度。
	 * @param mm 締め月。
	 * @param dd 締め日。
	 * @return 年度換算日。
	 * @throws AJDException 不正な日付。
	 */
	public FYD getFYD( int yyyy, int mm, int dd ) throws AJDException {
		return getFYD( yyyy, mm, dd, 0, 0, 0 );
	}

	private AJD getAJD( int yyyy, int mm, int dd, int hh, int mi, int ss ) {
		try {
			if ( dd < 1 ) {
				mm--;
				if ( mm < 1 ) {
					mm = 12;
					yyyy--;
					if ( yyyy == 0 )	yyyy = -1;
				}
				dd = Month.getLastDay( yyyy, mm );
			}
			else if ( dd > Month.getLastDay( yyyy, mm ) ) {
				mm++;
				if ( mm > 12 ) {
					mm = 1;
					yyyy++;
					if ( yyyy == 0 )	yyyy = 1;
				}
				dd = 1;
			}
			if ( mm < 1 ) {
				mm = 12;
				yyyy--;
				if ( yyyy == 0 )	yyyy = -1;
				return getAJD( yyyy, mm, dd, hh, mi, ss );
			}
			else if ( mm > 12 ) {
				mm = 1;
				yyyy++;
				if ( yyyy == 0 )	yyyy = 1;
				return getAJD( yyyy, mm, dd, hh, mi, ss );
			}
			return new AJD( yyyy, mm, dd, hh, mi, ss );
		}
		catch( AJDException e ){}
		return null;
	}

	/**
	 * 指定日の取得。
	 * @param yyyy 年度。
	 * @param mm 締め月。
	 * @param dd 締め日。
	 * @param hh 時。
	 * @param mi 分。
	 * @param ss 秒。
	 * @return 年度換算日。
	 * @throws AJDException 不正な日付。
	 */
	public FYD getFYD( int yyyy, int mm, int dd, int hh, int mi, int ss ) throws AJDException {
		FYD[]	fyd = new FYD[] {
			new FYD( this, getAJD( yyyy == 1?	-1: yyyy - 1, mm, dd, hh, mi, ss ) ),
			new FYD( this, getAJD( yyyy, mm - 1, dd, hh, mi, ss ) ),
			new FYD( this, getAJD( yyyy, mm, dd - 1, hh, mi, ss ) ),
			new FYD( this, getAJD( yyyy, mm, dd, hh, mi, ss ) ),
			new FYD( this, getAJD( yyyy, mm, dd + 1, hh, mi, ss ) ),
			new FYD( this, getAJD( yyyy, mm + 1, dd, hh, mi, ss ) ),
			new FYD( this, getAJD( yyyy == -1?	1: yyyy + 1, mm, dd, hh, mi, ss ) )
		};
		for ( int i = 0; i < fyd.length; i++ ) {
			if ( fyd[i].ajd == null )	continue;
			if ( fyd[i].getYear() != yyyy )	continue;
			if ( fyd[i].getMonth() != mm )	continue;
			if ( fyd[i].getDay() != dd )	continue;
			return fyd[i];
		}
		throw new AJDException( "指定日が存在しません。" );
	}
}

