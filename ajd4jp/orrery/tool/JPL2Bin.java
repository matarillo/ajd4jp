/*
 * AJD4JP
 * Copyright (c) 2011-2012  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.orrery.tool;
import java.io.*;
import java.util.*;
import java.math.*;
import ajd4jp.*;

class FileList implements Comparator<FileList> {
	int year;
	String name;
	File	file;

	static FileList make( File filename, String ext ) {
		FileList	fl = new FileList();
		fl.file = filename;
		fl.name = filename.getName();
		if ( fl.name.length() != 12 )	return null;
		if ( !fl.name.substring( 8 ).equals( ext ) )	return null;
		String[]	part = new String[] {
			fl.name.substring( 0, 4 ), fl.name.substring( 4, 8 )
		};
		if ( "ascm".equals( part[0] ) )	fl.year = -1;
		else if ( "ascp".equals( part[0] ) )	fl.year = 1;
		else	return null;
		fl.year *= Integer.parseInt( part[1] );
		return fl;
	}
	public int compare( FileList a, FileList b ) {
		return a.year - b.year;
	}
}

public class JPL2Bin {
	private static int all_days = 0;
	private static JPLItem[] planet = JPLItem.values();
	private static int offset[] = new int[planet.length];
	private static int count[] = new int[planet.length];
	private static int sub[] = new int[planet.length];

	private static MapXYZ map = null;
	private static File	output;
	private static RandomAccessFile[] raf = new RandomAccessFile[planet.length];

	private static String getExt( String name ) {
		String[]	part = name.split( "\\." );
		String	no = part[part.length - 1];
		System.out.println( "DE" + no );
		return "." + no;
	}

	private static void readHeader( String name ) throws Exception {
		ArrayList<String>	key1040 = new ArrayList<String>();
		ArrayList<String>	data1041 = new ArrayList<String>();
		System.out.println( "Reading " + name );
		BufferedReader	br = null;
		try {
			br = new BufferedReader( new FileReader( name ) );
			int	gno = 0;
			String line;
			boolean started = false;
			int	cnt1050 = 0;
			while( ( line = br.readLine() ) != null ) {
				line = line.trim();
				if ( line.isEmpty() ) continue;
				String[]	part = line.split( "\\s+" );
				if ( "GROUP".equals( part[0] ) ) {
					gno = Integer.parseInt( part[1] );
					started = false;
					continue;
				}
				switch( gno ) {
				case 1030:
					map = new MapXYZ( part[0], part[1] );
					all_days = (int)Double.parseDouble( part[2] );
					break;
				case 1040:
					if ( !started ) {
						started = true;
						break;
					}
					for ( String s: part ) { key1040.add( s ); }
					break;
				case 1041:
					if ( !started ) {
						started = true;
						break;
					}
					for ( String s: part ) { data1041.add( s ); }
					break;
				case 1050:
					{
						int[] data = null;
						switch( cnt1050 ) {
						case 0:
							data = offset;
							break;
						case 1:
							data = count;
							break;
						case 2:
							data = sub;
							break;
						}
						for( int i = 0; i < part.length; i++ ) {
							data[i] = Integer.parseInt( part[i] );
						}
						cnt1050++;
					}
					break;
				}
			}
		}
		finally {
			if ( br != null ) {
				br.close();
			}
		}
		map.setCount( count );
		map.setStep( all_days, sub );
		map.add( key1040.toArray( new String[0] ), data1041.toArray( new String[0] ) );
		System.out.println();
	}

	private static File[] makeList( String header, String ext ) throws Exception {
		File	dir = new File( header ).getParentFile();
		ArrayList<FileList>	list = new ArrayList<FileList>();
		for ( File f: dir.listFiles() ) {
			FileList	fl = FileList.make( f, ext );
			if ( fl == null )	continue;
			list.add( fl );
		}
		if ( list.size() == 0 ) throw new Exception( "No Data(GROUP 1070)" );
		FileList[]	arr = list.toArray( new FileList[0] );
		Arrays.sort( arr, arr[0] );
		File[]	ret = new File[arr.length];
		for ( int i = 0; i < arr.length; i++ ) {
			ret[i] = arr[i].file;
		}
		return ret;
	}

	private static void write( int p_no, String data ) throws Exception {
		byte[]	org = MapXYZ.Conv( data ).getBytes();
		if ( map.size < org.length ) throw new Exception( String.format( "Size over %d < %d", map.size, org.length ) );
		if ( map.size > org.length ) {
			byte[]	b = new byte[map.size];
			int i = 0;
			for ( ; i < org.length; i++ )	b[i] = org[i];
			for ( ; i < b.length; i++ )	b[i] = (byte)' ';
			org = b;
		}
		raf[p_no].write( org );
	}

	private static BigDecimal	last_dt = null;
	private static void set(String[] data) throws Exception {
		if ( data == null )	return;
		BigDecimal	st = new BigDecimal( MapXYZ.Conv( data[0] ) );
		if ( last_dt == null ) {
			map.start_jed = st;
		}
		if (last_dt != null ) {
			if ( st.compareTo( last_dt ) < 0 ) {
				System.out.println( "SKIP: " + st.toString() );
				return;
			}
			if ( st.compareTo( last_dt ) > 0 ) {
				throw new Exception( "Next date mismatch" );
			}
		}
		last_dt = st.add( new BigDecimal( all_days ) );
		int	start = offset[0] - 1;
		for ( int p = 0; p < planet.length; p++ ) {
			int	skip = planet[p].GetDim();
			skip *= count[p];
			for ( int loop = 0; loop < sub[p]; loop++ ) {
				for ( int i = 0; i < skip; i++ ) {
					write( p, data[start] );
					start++;
				}
			}
		}
	}

	private static void readData( String file ) throws Exception {
		System.out.println( "Reading " + file );
		BufferedReader	br = null;
		try {
			br = new BufferedReader( new FileReader( file ) );
			String[]	data = null;
			int	pno = 0;
			int	cnt = 0;
			boolean	start = false;
			int	line_cnt = 0;
			String	line;
			while( ( line = br.readLine() ) != null ) {
				line_cnt++;
				line = line.trim();
				if ( line.isEmpty() )	continue;
				String[]	part = line.split( "\\s+" );
				if ( !start ) {
					if ( part.length != 2 )	throw new Exception( "Error line:" + Integer.toString( line_cnt ) + "  " + line );
					set( data );
					pno = 0;
					cnt = 0;
					data = new String[Integer.parseInt( part[1] )];
					start = true;
					continue;
				}
				for ( int i = 0; i < part.length; i++ ) {
					data[cnt] = part[i];
					cnt++;
					if ( cnt >= data.length )	break;
				}
				if ( cnt >= data.length )	start = false;
			}
			set( data );
		}
		finally {
			System.out.println();
			if ( br != null ) {
				br.close();
			}
		}
	}

	public static void main( String[] argv ) throws Exception {
		System.out.println( Version.getTitle() );
		System.out.println( Version.getCopyRight() );
		System.out.println();
		if ( argv.length != 2 ) {
			System.out.println( "param>> input_header_file output_dir" );
			return;
		}
		output = new File( argv[1] );
		output.mkdirs();
		String ext = getExt( argv[0] );
		for( int i = 0; i < planet.length; i++ ) {
			raf[i] = new RandomAccessFile( new File( output, planet[i].getFileName() ), "rw" );
			raf[i].setLength( 0 );
		}
		readHeader( argv[0] );
		for ( File f: makeList( argv[0], ext ) ) {
			readData( f.toString() );
		}
		for( int i = 0; i < planet.length; i++ ) {
			raf[i].close();
		}
		map.end_jed = new BigDecimal( all_days ).add( last_dt );
		map.save( output );
		map.printDate();
		System.out.println( "Completed." );
	}
}

