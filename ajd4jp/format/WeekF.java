/*
 * AJD4JP
 * Copyright (c) 2011  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.format;

import ajd4jp.*;

class Dummy extends Two {
	static final Dummy	dmy = new Dummy();
	char[] getChar(){return null;}
	public String toString( int n ){ return ""; }
}

/**
 * 曜日のフォーマット。
 */
public class WeekF extends Format {
	/** 名称種別 */
	public enum Name {
		/** 英語名略称 */
		SHORT,
		/** 英語名 */
		LONG,
		/** 日本語名 */
		JP
	}
	private Name n;
	/**
	 * コンストラクタ。
	 * @param name 名称種別。
	 */
	public WeekF( Name name ) {
		super( Dummy.dmy );
		n = name;
	}

	/**
	 * コンストラクタ。new WeekF( Name.JP ) と等価です。
	 */
	public WeekF() {
		this( Name.JP );
	}

	private ajd4jp.Week	w;
	int getNum( ajd4jp.Day date ) {
		w = ajd4jp.Week.get( date );
		return 0;
	}
	String getHead() {
		switch( n ) {
		case SHORT:
			return w.getShortName();
		case LONG:
			return w.getLongName();
		case JP:
			return w.getJpName();
		}
		return "";
	}
}

