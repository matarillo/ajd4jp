/*
 * AJD4JP
 * Copyright (c) 2011-2017  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp;



/**
 * AJD4JPのバージョン情報。
 */
public class Version {
	private Version(){}
	private static final String VERSION = "@@@ver";
	private static final String	SITE = "(http://ajd4jp.osdn.jp/)";
	private static final String TITLE = "AJD4JP " + VERSION + SITE;
	private static final String COPY = "Copyright 2011-2021 @@@auth";

	/**
	 * バージョン番号の取得。
	 * @return バージョンが 1.3.0.2012 であれば、
	 * new int[]{ 1, 3, 0, 2012 }
	 * のような配列を返します。
	 */
	public static int[] getVersion() {
		String[]	ver = VERSION.split( "\\." );
		int[]	ret = new int[ver.length];
		for ( int i = 0; i < ver.length; i++ ) {
			ret[i] = Integer.parseInt( ver[i] );
		}
		return ret;
	}

	/**
	 * AJD4JPのアプリタイトル文字列。
	 * @return アプリタイトル。
	 */
	public static String getTitle() {
		return TITLE;
	}

	/**
	 * AJD4JPの著作権表示。
	 * @return 著作権表示。
	 */
	public static String getCopyRight() {
		return COPY;
	}
}


