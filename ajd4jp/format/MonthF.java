/*
 * AJD4JP
 * Copyright (c) 2011  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.format;

import ajd4jp.*;

/**
 * 月のフォーマット。{@link ajd4jp.LSCD}の場合、自動的に閏の文字が補われます。
 */
public class MonthF extends Format {
	static final ajd4jp.Month[]	mon = new ajd4jp.Month[12];
	static {
		try {
			for ( int i = 0; i < mon.length; i++ ) {
				mon[i] = new ajd4jp.Month( 2000, i + 1 );
			}
		}
		catch( AJDException e ){}
	}

	/** 名称種別 */
	public enum Name {
		/** 英語名略称 */
		SHORT( new Two(){
			char[] getChar(){return null;}
			public String toString( int n ) { return mon[n - 1].getShortName(); }
		}),
		/** 英語名 */
		LONG( new Two(){
			char[] getChar(){return null;}
			public String toString( int n ) { return mon[n - 1].getLongName(); }
		}),
		/** 日本語名 */
		JP( new Two(){
			char[] getChar(){return null;}
			public String toString( int n ) { return mon[n - 1].getJpName(); }
		});
		Two	two;
		private Name( Two t ) { two = t; }
	}

	/**
	 * コンストラクタ。
	 * @param num 数値表記。
	 */
	public MonthF( Two num ) {
		super( num );
	}

	/**
	 * コンストラクタ。new MonthF( new TwoHalfArabia( '0' ) ) と等価です。
	 */
	public MonthF() {
		this( new TwoHalfArabia( '0' ) );
	}

	private Name n = null;
	/**
	 * コンストラクタ。文字列名称を出力します。
	 * @param name 名称種別。
	 */
	public MonthF( Name name ) {
		this( name.two );
		n = name;
	}

	private boolean leap_f = false;
	int getNum( ajd4jp.Day date ) {
		if ( n == Name.SHORT || n == Name.LONG )	leap_f = false;
		else	leap_f = date instanceof LSCD?	((LSCD)date).isLeapMonth():	false;
		return date.getMonth();
	}

	String getHead() {
		return leap_f?	"閏":	"";
	}
}


