/*
 * AJD4JP
 * Copyright (c) 2011-2012  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.orrery;

import ajd4jp.*;
import ajd4jp.orrery.tool.*;
import java.math.*;
import java.io.*;

class Raw {
	static JPLItem[]	list = JPLItem.values();
	static int getNo( JPLItem p ) {
		int	no = 0;
		for ( ; no < list.length; no++ )	if ( list[no] == p )	break;
		return no;
	}

	BigDecimal	x;
	BigDecimal	y;
	BigDecimal	z;
	JPLItem.Centric	ce;

	static Raw getRaw( ETD jed, JPLItem p ) throws AJDException, IOException {
		return new Raw( jed, p );
	}

	private Raw( ETD jed, JPLItem p ) throws AJDException, IOException {
		ce = p.GetType();
		int	no = getNo( p );
		Ephemeris.DE de = Ephemeris.getBlockNo( jed, Ephemeris.getDays( no ) );
		RandomAccessFile	r = null;
		int	data_count = p.GetDim();
		int	size = Ephemeris.getCount( no );
		BigDecimal	file[] = new BigDecimal[data_count * size];
		synchronized( p ) {
			try {
				byte[]	buf = Ephemeris.getBuffer();
				r = Ephemeris.open( p );
				r.seek( (long)data_count * size * de.no * buf.length );
				for ( int i = 0; i < file.length; i++ ) {
					r.readFully( buf );
					file[i] = new BigDecimal( new String( buf ).trim() );
				}
			}
			catch( EOFException e ) {
				throw new AJDException( "ファイルサイズ不正" );
			}
			finally {
				if ( r != null )	r.close();
			}
		}

		BigDecimal	cp[] = new BigDecimal[size];
		cp[0] = BigDecimal.ONE;
		cp[1] = de.pos;
		de.pos = de.pos.multiply( new BigDecimal( 2 ) );
		for ( int i = 2; i < size; i++ ) {
			cp[i] = de.pos.multiply( cp[i - 1] ).subtract( cp[i - 2] );
		}
		no = 0;
		for ( int k = 0; k < data_count; k++ ) {
			BigDecimal	tmp = BigDecimal.ZERO;
			for ( int i = 0; i < size; i++, no++ ) {
				tmp = tmp.add( file[no].multiply( cp[i] ) );
			}
			if ( k == 0 )	x = tmp;
			else if ( k == 1 )	y = tmp;
			else	z = tmp;
		}
	}
}


