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
 * 3次元直行座標を表します。<br>
 * ここで使用する座標系は、JPL(ジェット推進研究所)天体暦が使用する座標系ですが、
 * J2000.0の赤道座標系として扱います。<br>
 * JPL天体暦はICRS(国際天文基準座標系)ですので、厳密にはJ2000.0の赤道座標系とは
 * 0.1秒未満の誤差があります。
 */
public class Dim3 implements Serializable {
	private static final long serialVersionUID = 1;
	private BigDecimal x, y, z;

	private Dim3() {
		x = y = z = BigDecimal.ZERO;
	}
	private Dim3( BigDecimal a, BigDecimal b, BigDecimal c ) {
		x = a;
		y = b;
		z = c;
	}
	private Dim3( Raw r ) {
		x = r.x;
		y = r.y;
		z = r.z;
	}

	/**
	 * 惑星位置を算出します。
	 * @param jed 地球時。
	 * @param planet 座標を求める惑星。
	 * @param origin 座標の原点。
	 * @return 座標。
	 * @throws AJDException Ephemeris.init()の初期化漏れ。
	 * @throws IOException ファイルIOエラー。
	 */
	public static Dim3 plot( ETD jed, Planet planet, Planet origin ) throws AJDException, IOException {
		Ephemeris.check();
		if ( planet == origin ) {
			return new Dim3();
		}
		Dim3	ret = null;
		if ( planet.item != null ) {
			ret = new Dim3( Raw.getRaw( jed, planet.item ) );
		}
		else if ( planet == Planet.EARTH ) {
			Raw	emb = Raw.getRaw( jed, JPLItem.EARTH_MOON_BARYCENTER );
			Raw	moon = Raw.getRaw( jed, JPLItem.MOON );
			BigDecimal	r = Ephemeris.EMB;
			ret = new Dim3(
				emb.x.subtract( r.multiply( moon.x ) ),
				emb.y.subtract( r.multiply( moon.y ) ),
				emb.z.subtract( r.multiply( moon.z ) )
			);
		}
		else if ( planet == Planet.SOLAR_SYSTEM_BARYCENTER ) {
			ret = new Dim3();
		}

		if ( planet == Planet.MOON ) {
			if ( origin == Planet.EARTH )	return ret;
			Dim3	earth = plot( jed, Planet.EARTH, Planet.SOLAR_SYSTEM_BARYCENTER );
			ret = ret.add( earth );
		}
		if ( origin == Planet.SOLAR_SYSTEM_BARYCENTER )	return ret;
		return ret.subtract( plot( jed, origin, Planet.SOLAR_SYSTEM_BARYCENTER ) );
	}

	/**
	 * 座標の加算。
	 * @param a 加算値。
	 * @return this + a。
	 */
	public Dim3 add( Dim3 a ) {
		return new Dim3(
			this.x.add( a.x ),
			this.y.add( a.y ),
			this.z.add( a.z )
		);
	}
	private Dim3 add( Raw a ) {
		return new Dim3(
			this.x.add( a.x ),
			this.y.add( a.y ),
			this.z.add( a.z )
		);
	}

	/**
	 * 座標の減算。
	 * @param a 減算値。
	 * @return this - a。
	 */
	public Dim3 subtract( Dim3 a ) {
		return new Dim3(
			this.x.subtract( a.x ),
			this.y.subtract( a.y ),
			this.z.subtract( a.z )
		);
	}
	private Dim3 subtract( Raw a ) {
		return new Dim3(
			this.x.subtract( a.x ),
			this.y.subtract( a.y ),
			this.z.subtract( a.z )
		);
	}

	/**
	 * 符号の反転。3軸全ての符号を反転させます。
	 * @return this を符号反転したもの。
	 */
	public Dim3 negate() {
		return new Dim3(
			x.negate(),
			y.negate(),
			z.negate()
		);
	}

	/**
	 * X軸の回転。
	 * @param a 角度。
	 * @return 回転後の座標。
	 */
	public Dim3 rotateX( Angle a ) {
		double	p = a.convert( Angle.Unit.RADIAN ).h.doubleValue();
		BigDecimal	s = new BigDecimal( Math.sin( p ) );
		BigDecimal	c = new BigDecimal( Math.cos( p ) );
		BigDecimal	dy = c.multiply( y ).add( s.multiply( z ) );
		BigDecimal	dz = c.multiply( z ).subtract( s.multiply( y ) );
		return new Dim3( x, dy, dz );
	}

	/**
	 * Y軸の回転。
	 * @param a 角度。
	 * @return 回転後の座標。
	 */
	public Dim3 rotateY( Angle a ) {
		double	p = a.convert( Angle.Unit.RADIAN ).h.doubleValue();
		BigDecimal	s = new BigDecimal( Math.sin( p ) );
		BigDecimal	c = new BigDecimal( Math.cos( p ) );
		BigDecimal	dx = c.multiply( x ).subtract( s.multiply( z ) );
		BigDecimal	dz = s.multiply( x ).add( c.multiply( z ) );
		return new Dim3( dx, y, dz );
	}

	/**
	 * Z軸の回転。
	 * @param a 角度。
	 * @return 回転後の座標。
	 */
	public Dim3 rotateZ( Angle a ) {
		double	p = a.convert( Angle.Unit.RADIAN ).h.doubleValue();
		BigDecimal	s = new BigDecimal( Math.sin( p ) );
		BigDecimal	c = new BigDecimal( Math.cos( p ) );
		BigDecimal	dx = c.multiply( x ).add( s.multiply( y ) );
		BigDecimal	dy = c.multiply( y ).subtract( s.multiply( x ) );
		return new Dim3( dx, dy, z );
	}


	/** 座標値 */
	public static class XYZ implements Serializable {
		private static final long serialVersionUID = 1;
		/** X座標 */
		public BigDecimal	x;
		/** Y座標 */
		public BigDecimal	y;
		/** Z座標 */
		public BigDecimal	z;

		/**
		 * コンストラクタ。
		 * @param x X座標。
		 * @param y Y座標。
		 * @param z Z座標。
		 */
		public XYZ( Number x, Number y, Number z ) {
			this.x = new BigDecimal( x.toString() );
			this.y = new BigDecimal( y.toString() );
			this.z = new BigDecimal( z.toString() );
		}
		/**
		 * 文字列化。
		 * @return (x座標, y座標, z座標)形式の文字列を返します。
		 */
		public String toString() {
			StringBuilder	buf = new StringBuilder( "(" );
			buf.append( Calc.toString( x ) ).append( ", " );
			buf.append( Calc.toString( y ) ).append( ", " );
			buf.append( Calc.toString( z ) ).append( ")" );
			return buf.toString();
		}
	}
	/** 単位 */
	public enum Unit {
		/** キロメートル */
		KM,
		/** 天文単位 */
		AU
	}
	/**
	 * 座標値の取得。
	 * @param u 単位。
	 * @return 座標値。
	 */
	public XYZ get( Unit u ) {
		if ( u == Unit.KM )	return new XYZ( x, y, z );
		return new XYZ(
			Calc.div( x, Ephemeris.AU ),
			Calc.div( y, Ephemeris.AU ),
			Calc.div( z, Ephemeris.AU )
		);
	}
}

