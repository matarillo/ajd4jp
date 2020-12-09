/*
 * AJD4JP
 * Copyright (c) 2011  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp;


/**
 * 干支の組み合わせ。甲子から癸亥まで、60通り存在します。
 * 還暦は干支が一巡したことを表しています。
 */
public class SexagenaryCycle implements java.io.Serializable {
	private static final long serialVersionUID = 1;
	private static final HeavenlyStem[]		hs = HeavenlyStem.values();
	private static final EarthlyBranch[]	eb = EarthlyBranch.values();

	private HeavenlyStem	stem;
	private EarthlyBranch	branch;
	/**
	 * 組み合わせ総数。
	 */
	public static final int MAX = 60;

	/**
	 * コンストラクタ。
	 * @param h 十干。
	 * @param e 十二支。
	 */
	public SexagenaryCycle( HeavenlyStem h, EarthlyBranch e ) {
		stem = h;
		branch = e;
	}

	/**
	 * 十干の取得。
	 * @return 十干。
	 */
	public HeavenlyStem getHeavenlyStem() { return stem; }
	/**
	 * 十二支の取得。
	 * @return 十二支。
	 */
	public EarthlyBranch getEarthlyBranch() { return branch; }

	/**
	 * 文字列化。
	 * @return 甲子～癸亥。
	 */
	public String toString() {
		if ( stem == null || branch == null )	return null;
		StringBuilder	buf = new StringBuilder( stem.getName() );
		return buf.append( branch.getName() ).toString();
	}

	/**
	 * 干支の比較。
	 * @param o 比較対象。
	 * @return true:一致、false:不一致。
	 */
	public boolean equals( Object o ) {
		if ( o instanceof SexagenaryCycle ) {
			SexagenaryCycle	s = (SexagenaryCycle)o;
			return stem == s.stem && branch == s.branch;
		}
		return false;
	}

	/**
	 * {@link SexagenaryCycle#getNo()}を返します。
	 * @return ハッシュコード。
	 */
	public int hashCode() {
		return getNo();
	}

	/**
	 * 干支の組み合わせ番号を返します。
	 * @param h 十干。
	 * @param b 十二支。
	 * @return 0(甲子)～59(癸亥)。存在しない組み合わせは-1。
	 */
	public static int getNo( HeavenlyStem h, EarthlyBranch b ) {
		if ( h == null || b == null )	return -1;
		for ( int no = 0, h_no = 0, b_no = 0; no < MAX;
		no++, h_no = (h_no + 1) % hs.length, b_no = (b_no + 1) % eb.length ) {
			if ( h_no == h.getNo() && b_no == b.getNo() )	return no;
		}
		return -1;
	}

	/**
	 * 干支の組み合わせ番号を返します。
	 * @return 0(甲子)～59(癸亥)。存在しない組み合わせは-1。
	 */
	public int getNo() {
		return getNo( stem, branch );
	}

	/**
	 * 干支の組み合わせを返します。
	 * @param no 0(甲子)～59(癸亥)。
	 * @return 甲子～癸亥。
	 * 範囲外の引数を渡すと null を返します。
	 */
	public static SexagenaryCycle getSexagenaryCycle( int no ) {
		if ( no < 0 || no >= MAX )	return null;
		return new SexagenaryCycle( hs[no % hs.length], eb[no % eb.length] );
	}

	/**
	 * 年の干支の取得。
	 * @param date 年の取得先。
	 * @return その年を表す干支。
	 */
	public static SexagenaryCycle getYear( Day date ) {
		return new SexagenaryCycle(
			HeavenlyStem.getYear( date ),
			EarthlyBranch.getYear( date )
		);
	}
	/**
	 * 月の干支の取得。
	 * @param date 月の取得先。
	 * @return その月を表す干支。
	 */
	public static SexagenaryCycle getMonth( Day date ) {
		return new SexagenaryCycle(
			HeavenlyStem.getMonth( date ),
			EarthlyBranch.getMonth( date )
		);
	}
	/**
	 * 日の干支の取得。
	 * @param date 日の取得先。
	 * @return その日を表す干支。
	 */
	public static SexagenaryCycle getDay( Day date ) {
		return new SexagenaryCycle(
			HeavenlyStem.getDay( date ),
			EarthlyBranch.getDay( date )
		);
	}
	/**
	 * 時間の干支の取得。
	 * @param date 時間の取得先。
	 * @return その時間を表す干支。
	 */
	public static SexagenaryCycle getTime( Day date ) {
		return new SexagenaryCycle(
			HeavenlyStem.getTime( date ),
			EarthlyBranch.getTime( date )
		);
	}
}

