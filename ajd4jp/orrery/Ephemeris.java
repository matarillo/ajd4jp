/*
 * AJD4JP
 * Copyright (c) 2011-2017  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.orrery;

import ajd4jp.*;
import ajd4jp.util.*;
import ajd4jp.orrery.tool.*;
import java.io.*;
import java.util.*;
import java.math.*;


/**
 * 天体暦。
 */
public class Ephemeris {
	private Ephemeris(){}
	private static File	parent;
	private static MapXYZ	map = null;
	private static ETD start = null, end = null;
	private static HashMap<String, BigDecimal>	fix = new HashMap<String, BigDecimal>();
	static BigDecimal	EMB, AU;

	static long floor( BigDecimal b ) {
		return (long)Math.floor( b.doubleValue() );
	}

	static BigDecimal getParam( String key ) {
		return fix.get( key );
	}

	/**
	 * 初期化。*.deのパラメータファイルを読み込みます。
	 * @param dir ファイルの格納先ディレクトリ。
	 * @throws Exception 初期化失敗。
	 */
	public static void init( String dir ) throws Exception {
		parent = new File( dir );
		map = MapXYZ.load( parent );
		fix = new HashMap<String, BigDecimal>();
		for ( int i = 0; i < map.key.length; i++ ) {
			fix.put( map.key[i], map.val[i] );
		}
		start = new ETD( map.start_jed );
		end = new ETD( map.end_jed );
		EMB = Calc.div( BigDecimal.ONE, fix.get( "EMRAT" ).add(BigDecimal.ONE) );
		AU = fix.get( "AU" );
	}

	static RandomAccessFile open( JPLItem p ) throws IOException {
		return new RandomAccessFile( new File( parent, p.getFileName() ), "r" );
	}

	static void check() throws AJDException {
		if ( start == null || end == null )
			throw new AJDException( "Ephemeris.init()が呼び出されていません" );
	}

	/**
	 * 日付範囲判定。
	 * 天体暦が指定日で計算できるか判定します。
	 * @param jed 地球時。
	 * @return true:計算可、false:日付範囲外か init が呼ばれていない。
	 */
	public static boolean isRange( ETD jed ) {
		if ( start == null || end == null )	return false;
		if ( start.compareTo( jed ) > 0 || end.compareTo( jed ) <= 0 )	return false;
		return true;
	}

	static class DE {
		int	no;
		BigDecimal	pos;
		DE( BigDecimal sub, int days ) {
			no = (int)Ephemeris.floor( Calc.div( sub, days ) );
			sub = sub.subtract( new BigDecimal( no * days ) );
			pos = Calc.div( sub.multiply( new BigDecimal( 2 ) ), new BigDecimal( days ) ).subtract( BigDecimal.ONE );
		}
	}

	static DE getBlockNo( ETD jed, int days ) throws AJDException {
		if ( !isRange( jed ) )	throw new AJDException( "天体暦の日時範囲外" );
		return new DE( jed.getJED().subtract( start.getJED() ), days );
	}

	static int getDays( int no ) { return map.step_day[no]; }
	static int getCount( int no ) { return map.data_count[no]; }
	static byte[] getBuffer() { return new byte[map.size]; }
}

