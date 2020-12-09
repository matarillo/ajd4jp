/*
 * AJD4JP
 * Copyright (c) 2011-2012  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.orrery.tool;
import java.math.*;
import java.io.*;
import java.util.*;
import ajd4jp.*;
import ajd4jp.orrery.*;


public class MapXYZ implements Serializable {
	private static final long serialVersionUID = 1;
	private static final String	FILE_NAME = "index.de";

	public String	TITLE = Version.getTitle();
	public String	COPY = Version.getCopyRight();
	public int[]	version = Version.getVersion();
	public int size = 0;
	public BigDecimal	start_jed, end_jed;
	public int[] step_day;
	public int[] data_count;

	public String[]	key;
	public BigDecimal[]	val;

	public void printDate() {
		System.out.println( "START:  " + new AJD( start_jed ).toString() );
		System.out.println( "END  :  " + new AJD( end_jed ).toString() );
	}

	public MapXYZ(){}
	MapXYZ( String s, String e ) {
		start_jed = new BigDecimal( Conv( s ) );
		end_jed = new BigDecimal( Conv( e ) );
		StringBuilder	buf = new StringBuilder( "START = " );
		buf.append( new ETD( start_jed ) );
		buf.append( "  /  END = " );
		buf.append( new ETD( end_jed ) );
		System.out.println( buf.toString() );
	}
	void setCount( int[] cnt ) {
		data_count = cnt;
		System.out.print( "Count :" );
		for ( int i = 0; i < cnt.length; i++ ) {
			System.out.print( " " );
			System.out.print( data_count[i] );
		}
		System.out.println();
	}
	void setStep( int all, int[] sub ) {
		step_day = new int[sub.length];
		System.out.print( "Days :" );
		for ( int i = 0; i < sub.length; i++ ) {
			step_day[i] = all / sub[i];
			System.out.print( " " );
			System.out.print( step_day[i] );
		}
		System.out.println();
	}
	void add( String[] k, String[] v ) throws Exception {
		key = k;
		val = new BigDecimal[v.length];
		for( int i = 0; i < v.length; i++ ) {
			String	tmp = Conv( v[i] );
			val[i] = new BigDecimal( tmp );
			int	len = tmp.length();
			if ( size < len )	size = len;
		}
		System.out.print( "String Length: " );
		System.out.println( size );
		if ( key.length != val.length ) {
			throw new Exception( "GROUP 1040 / 1041 Error" );
		}
		System.out.print( "GROUP 1040 / 1041 count >> " );
		System.out.println( key.length );
	}
	static String Conv( String data ) {
		data = data.trim();
		String[]	tmp = data.split( "D" );
		if ( tmp.length == 2 ) {
			data = tmp[0] + "E" + tmp[1];
		}
		return data;
	}

	void save( File dir ) throws Exception {
		ObjectOutputStream	oos = null;
		try {
			File	file = new File( dir, FILE_NAME );
			oos = new ObjectOutputStream( new FileOutputStream( file ) );
			oos.writeObject( this );
		}
		finally {
			if ( oos != null )	oos.close();
		}
	}

	public static MapXYZ load( File dir ) throws Exception {
		ObjectInputStream	ois = null;
		try {
			File	file = new File( dir, FILE_NAME );
			ois = new ObjectInputStream( new FileInputStream( file ) );
			return (MapXYZ)ois.readObject();
		}
		finally {
			if ( ois != null )	ois.close();
		}
	}

	public static void main( String[] argv ) throws Exception {
		MapXYZ	map = load( new File( argv[0] ) );
		map.printDate();
	}
}


