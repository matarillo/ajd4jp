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
import java.math.*;
import java.io.*;

/**
 * 極座標を表します。<br>
 * 赤道座標系(赤経、赤緯)と黄道座標系(黄経、黄緯)を扱えます。
 */
public class Pole implements Serializable {
	/** 座標系 */
	public enum Plane {
		/** 赤道 */
		EQUATOR,
		/** 黄道 */
		ECLIPTIC
	}
	private static BigDecimal sin( BigDecimal a ) {
		return new BigDecimal( Math.sin( a.doubleValue() ) );
	}
	private static BigDecimal cos( BigDecimal a ) {
		return new BigDecimal( Math.cos( a.doubleValue() ) );
	}
	private static BigDecimal atan2( BigDecimal a, BigDecimal b ) {
		return new BigDecimal( Math.atan2( a.doubleValue(), b.doubleValue() ) );
	}
	private static BigDecimal sqrt( BigDecimal a ) {
		return new BigDecimal( Math.sqrt( a.doubleValue() ) );
	}

	// 緯度(latitude),経度(Longitude)
	private BigDecimal	p, l;

	private Pole() {
		l = p = BigDecimal.ZERO;
	}
	private Pole( BigDecimal x, BigDecimal y, BigDecimal z ) {
		l = atan2( y, x );
		p = atan2( z, sqrt( x.pow(2).add( y.pow(2) ) ) );
	}

	private static final BigDecimal
		E84381 = new BigDecimal( 84381.406 ),
		E46 = new BigDecimal( 46.836769 ),
		E59 = new BigDecimal( 0.00059 ),
		E1819 = new BigDecimal( 0.001813 ),
		SEC = new BigDecimal( 648000 );

	private static final Angle[][]	ECS = new Angle[][] {
		new Angle[] {
			new Angle( 2306.2181, Angle.Unit.SECOND ),
			new Angle( 0.30188, Angle.Unit.SECOND ),
			new Angle( 0.017998, Angle.Unit.SECOND ),
		},
		new Angle[] {
			new Angle( 2004.3109, Angle.Unit.SECOND ),
			new Angle( -0.42665, Angle.Unit.SECOND ),
			new Angle( -0.041833, Angle.Unit.SECOND ),
		},
		new Angle[] {
			new Angle( 2306.2181, Angle.Unit.SECOND ),
			new Angle( 1.09468, Angle.Unit.SECOND ),
			new Angle( 0.018203, Angle.Unit.SECOND ),
		},
	};
	private static final Angle	A90 = new Angle( 90, Angle.Unit.DEGREE );
	private static final Angle	A_90 = new Angle( -90, Angle.Unit.DEGREE );

	private static Angle mean( Angle[] param, BigDecimal[] uc ) {
		BigDecimal	ret = BigDecimal.ZERO;
		for ( int i= 0; i < 3; i++ ) {
			ret = ret.add( param[i].h.multiply( uc[i] ) );
		}
		return new Angle( ret, Angle.Unit.SECOND );
	}
	// 赤道歳差
	private static Dim3 mean( Dim3 org, BigDecimal[] uc ) {
		Dim3	ret = org;
		ret = ret.rotateZ( A90.subtract( mean( ECS[0], uc ) ) );
		ret = ret.rotateX( mean( ECS[1], uc ) );
		ret = ret.rotateZ( A_90.subtract( mean( ECS[2], uc ) ) );
		return ret;
	}
	private static final Angle[] AX = new Angle[] {
		new Angle( 84381.406, Angle.Unit.SECOND ),
		new Angle( -46.836769, Angle.Unit.SECOND ),
		new Angle( -0.00059, Angle.Unit.SECOND ),
		new Angle( 0.001813, Angle.Unit.SECOND ),
	};
	// 黄道傾斜角
	private static Angle axial( BigDecimal[] uc ) {
		BigDecimal	ret = AX[0].h;
		ret = ret.add( AX[1].h.multiply( uc[0] ) );
		ret = ret.add( AX[2].h.multiply( uc[1] ) );
		ret = ret.add( AX[3].h.multiply( uc[2] ) );
		return new Angle( ret, Angle.Unit.SECOND );
	}
	// 黄道章動
	private static Dim3 ecriptic( Dim3 org, BigDecimal[] uc, Angle ax, Angle phi ) {
		Dim3	ret = mean( org, uc );
		ret = ret.rotateX( ax );
		return ret.rotateZ( phi );
	}
	// 赤道章動
	private static Dim3 equator( Dim3 org, BigDecimal[] uc, Angle ax, Angle phi, Angle epsilon ) {
		Dim3	ret = ecriptic( org, uc, ax, phi );
		Angle	a = new Angle( 0, Angle.Unit.SECOND ).subtract( ax.add( epsilon ) );
		return ret.rotateX( a );
	}
	private static BigDecimal[] get20C( ETD jed ) {
		BigDecimal	c = jed.get20C();
		return new BigDecimal[] { c, c.pow( 2 ), c.pow( 3 ) };
	}

	/**
	 * 惑星位置を算出します。
	 * @param jed 地球時。
	 * @param planet 座標を求める惑星。
	 * @param c 座標系。
	 * @return 座標。
	 * @throws AJDException 引数指定ミス。
	 * @throws IOException ファイルIOエラー。
	 */
	public static Pole plot( ETD jed, Planet planet, Plane c ) throws AJDException, IOException {
		if ( planet == Planet.EARTH ) {
			throw new AJDException( "Planet.EARTH は指定できません。" );
		}
		Dim3	p = Dim3.plot( jed, planet, Planet.EARTH );
		BigDecimal[]	uc = get20C( jed );
		Angle	ax = axial( uc );
		Raw	pe = Raw.getRaw( jed, JPLItem.NUTATIONS );
		Angle	phi = new Angle( pe.x.negate(), Angle.Unit.RADIAN );
		if ( c == Plane.ECLIPTIC ) {
			p = ecriptic( p, uc, ax, phi );
		}
		else {
			p = equator( p, uc, ax, phi, new Angle( pe.y, Angle.Unit.RADIAN ) );
		}
		Dim3.XYZ	d = p.get( Dim3.Unit.AU );
		return new Pole( d.x, d.y, d.z );
	}

	/** 座標値。 */
	public static class PL implements Serializable {
		private static final long serialVersionUID = 1;
		/** 緯度。 */
		public Angle	lat;
		/** 経度。 */
		public Angle	lon;

		/**
		 * コンストラクタ。
		 * @param latitude 緯度。
		 * @param longitude 経度。
		 */
		public PL( Angle latitude, Angle longitude ) {
			lat = latitude;
			lon = longitude;
		}
		/**
		 * 文字列化。
		 * @return 緯度と経度の文字列。
		 */
		public String toString() {
			StringBuilder	buf = new StringBuilder();
			buf.append( "Lat:" ).append( lat );
			buf.append( " / Lon:" ).append( lon );
			return buf.toString();
		}
	}
	/**
	 * 座標値の取得。
	 * @param latitude 緯度の単位。
	 * @param longitude 経度の単位。
	 * @return 座標値。
	 */
	public PL get( Angle.Unit latitude, Angle.Unit longitude ) {
		Angle	rp = new Angle( p, Angle.Unit.RADIAN ),
			rl = new Angle( l, Angle.Unit.RADIAN );
		return new PL( rp.convert( latitude ), rl.convert( longitude ) );
	}
}

