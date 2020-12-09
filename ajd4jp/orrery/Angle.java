/*
 * AJD4JP
 * Copyright (c) 2011-2012  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.orrery;

import ajd4jp.util.*;
import java.math.*;
import java.io.*;


/** 角度を表します。 */
public class Angle implements Serializable, Comparable<Angle>{
	private static final long serialVersionUID = 1;
	/**
	 * -0(負数としてのゼロ)。<br>
	 * SHIFT_DEGREE60に、-0.1のような値を与えたい場合、
	 * 全体では負数の小数ですが、コンストラクタ第一引数にはゼロを
	 * 指定することになり、マイナス符号の情報が失われます。<br>
	 * このインスタンスはそのような場合のために準備しており、
	 * ゼロの整数且つ負数が続く事を Angle クラスに知らせる事ができます。
	 */
	public static final BigDecimal	MZ = new MinDecimal( BigDecimal.ZERO, true );
	/** 単位 */
	public enum Unit {
		/** ラジアン */
		RADIAN( false ),
		/** 度(実数表記) */
		DEGREE( false ),
		/** 度分秒(ただし度は0から359の範囲) */
		DEGREE60( true ),
		/** 度分秒(ただし度は-180から179の範囲) */
		SHIFT_DEGREE60( true ),
		/** 時分秒(ただし時は0から23の範囲) */
		HOUR( true ),
		/** 秒(実数表記) */
		SECOND( false );
		boolean ms_f;
		private Unit( boolean f ) { ms_f = f; }
	}
	private Unit	unit;
	MinDecimal	h = null;
	BigDecimal	m = null;
	BigDecimal	s = null;

	/**
	 * 指定単位による文字列化。
	 * @return 角度を表す文字列。
	 */
	public String toString() {
		StringBuilder	buf = new StringBuilder();
		buf.append( Calc.toString( h ) );
		switch( unit ) {
		case RADIAN:
			buf.append( "(" ).append( Calc.toString( Calc.div( h, PI ) ) ).append( "π)rad" );
			break;
		case SECOND:
			buf.append( "″" );
			break;
		case HOUR:
			buf.append( ":" );
			break;
		default:
			buf.append( "°" );
			break;
		}
		if ( unit.ms_f ) {
			if ( unit == Unit.HOUR ) {
				buf.append( Calc.toString( m ) ).append( ":" );
				buf.append( Calc.toString( s ) );
			}
			else {
				buf.append( Calc.toString( m ) ).append( "′" );
				String[]	sec = Calc.toString( s ).split( "\\." );
				buf.append( sec[0] ).append( "″" );
				if ( sec.length > 1 ) {
					buf.append( "." ).append( sec[1] );
				}
			}
		}
		return buf.toString();
	}

	/**
	 * 角度の先頭要素を返します。単位により、以下のような値になります。
	 * <dl>
	 * <DT>RADIAN
	 * <DD>ラジアン。
	 * <DT>DEGREE
	 * <DD>度。小数点以下も含まれます。
	 * <DT>DEGREE60 または SHIFT_DEGREE60
	 * <DD>度の整数部。
	 * <DT>HOUR
	 * <DD>時。
	 * <DT>SECOND
	 * <DD>秒。小数点以下も含まれます。
	 * </dl>
	 * @return 先頭要素。上記参照。
	 */
	public BigDecimal getTop() {return h;}
	/**
	 * 角度の分要素を返します。<br>
	 * RADIAN、DEGREE、SECOND の場合、null を返します。
	 * @return 分。
	 */
	public BigDecimal getMinute() {return m;}
	/**
	 * 角度の秒要素を返します。<br>
	 * RADIAN、DEGREE、SECOND の場合、null を返します。
	 * @return 秒。
	 */
	public BigDecimal getSecond() {return s;}
	/**
	 * 単位を返します。
	 * @return 単位。
	 */
	public Unit getUnit(){return unit;}

	/**
	 * コンストラクタ。
	 * <br>topの設定値は{@link Angle#getTop()}を参照して下さい。
	 * @param top 度または時または秒(Unit.SECOND指定)。
	 * @param min 分。
	 * @param sec 秒。
	 * @param u 単位。
	 */
	public Angle( Number top, Number min, Number sec, Unit u ) {
		boolean	nz_f = false;
		unit = u;
		if ( top instanceof MinDecimal ) {
			h = (MinDecimal)top;
			nz_f = h.getNZ();
		}
		else	h = new MinDecimal( new BigDecimal( top.toString() ), false );
		if ( !u.ms_f )	return;
		m = new BigDecimal( min.toString() );
		s = new BigDecimal( sec.toString() );
		int	hh = h.intValue();
		h = new MinDecimal( h.subtract( new BigDecimal( hh ) ), false );
		m = m.add( h.multiply( A60 ) );
		int	mm = m.intValue();
		m = m.subtract( new BigDecimal( mm ) );
		s = s.add( m.multiply( A60 ) );
		while(true){
			if ( s.compareTo( BigDecimal.ZERO ) < 0 ) {
				s = s.add( A60 );
				mm--;
				continue;
			}
			if ( s.compareTo( A60 ) >= 0 ) {
				s = s.subtract( A60 );
				mm++;
				continue;
			}
			break;
		}
		while(true){
			if ( mm < 0 ) {
				mm += 60;
				hh--;
				continue;
			}
			if ( mm >= 60 ) {
				mm -= 60;
				hh++;
				continue;
			}
			break;
		}
		m = new BigDecimal( mm );
		if ( unit == Unit.HOUR ) {
			hh = hh % 24;
			if ( hh < 0 )	hh += 24;
		}
		else {
			hh = hh % 360;
			if ( unit == Unit.DEGREE60 ) {
				if ( hh < 0 )	hh += 360;
			}
			else {
				if ( hh < -180 )	hh = 360 + hh;
				if ( hh >= 180 )	hh = hh - 360;
			}
		}
		h = new MinDecimal( new BigDecimal( hh ), nz_f );
	}
	/**
	 * コンストラクタ。分と秒は、null または 0 になります。
	 * <br>topの設定値は{@link Angle#getTop()}を参照して下さい。
	 * @param top ラジアンまたは度または秒。
	 * @param u 単位。
	 */
	public Angle( Number top, Unit u ) {
		this( top,
			u.ms_f?	0: null,
			u.ms_f?	0: null,
			u );
	}

	static final BigDecimal
		PI = new BigDecimal( Math.PI ),
		A180 = new BigDecimal( 180 ),
		A360 = new BigDecimal( 360 ),
		A24 = new BigDecimal( 24 ),
		A12 = new BigDecimal( 12 ),
		A_12 = new BigDecimal( -12 ),
		A15 = new BigDecimal( 15 ),
		A60 = new BigDecimal( 60 ),
		A6060 = A60.multiply( A60 ),
		SEC = A6060.multiply( A24 ),
		DperS = SEC.divide( A360 );

	/**
	 * 単位変換。
	 * @param to 変換する単位。
	 * @return 変換後の角度。
	 */
	public Angle convert( Unit to ) {
		if ( unit == to )	return this;
		BigDecimal	dh = h, dm = m, ds = s;
		if ( unit != Unit.DEGREE ) {
			switch( unit ) {
			case HOUR:
				dh = dh.multiply( A6060 ).add( dm.multiply( A60 ) ).add( ds );
				dh = Calc.div( dh, DperS );
				break;
			case SECOND:
				dh = Calc.div( dh, A6060 );
				break;
			case SHIFT_DEGREE60:
				if ( dh.compareTo( BigDecimal.ZERO ) < 0 ) {
					dh = dh.subtract( Calc.div( dm, A60 ) ).subtract( Calc.div( ds, A6060 ) );
					break;
				}
			case DEGREE60:
				dh = dh.add( Calc.div( dm, A60 ) ).add( Calc.div( ds, A6060 ) );
				break;
			case RADIAN:
				dh = Calc.div( dh.multiply( A180 ), PI );
				break;
			}
		}

		dm = ds = null;
		if ( to == Unit.RADIAN ) {
			dh = Calc.div( dh.multiply( PI ), A180 );
		}
		else if ( to == Unit.SECOND ) {
			dh = dh.multiply( A6060 );
		}
		else if ( to.ms_f ) {
			boolean nega_f = false;
			if ( dh.compareTo( BigDecimal.ZERO ) < 0 && to == Unit.SHIFT_DEGREE60 ) {
				nega_f = true;
				dh = dh.negate();
			}
			while ( true ) {
				if ( dh.compareTo( BigDecimal.ZERO ) < 0 ) {
					dh = dh.add( A360 );
					continue;
				}
				if ( dh.compareTo( A360 ) >= 0 ) {
					dh = dh.subtract( A360 );
					continue;
				}
				break;
			}
			if ( to == Unit.DEGREE60 || to == Unit.SHIFT_DEGREE60 ) {
				dm = dh;
				dh = new BigDecimal( dm.intValue() );
				ds = dm.subtract( dh ).multiply( A60 );
				dm = new BigDecimal( ds.intValue() );
				ds = ds.subtract( dm ).multiply( A60 );
				if ( to == Unit.SHIFT_DEGREE60 && nega_f ) dh = new MinDecimal( dh.negate(), true );
			}
			else if ( to != Unit.DEGREE ) {
				ds = dh.multiply( DperS );
				dm = new BigDecimal( Calc.div( ds, A60 ).intValue() );
				ds = ds.subtract( dm.multiply( A60 ) );
				dh = new BigDecimal( Calc.div( dm, A60 ).intValue() );
				dm = new BigDecimal( dm.subtract( dh.multiply( A60 ) ).intValue() );
			}
		}
		return new Angle( dh, dm, ds, to );
	}

	/**
	 * 角度の加算。
	 * @param a 加算値。
	 * @return 加算値。thisと同じ単位です。
	 */
	public Angle add( Angle a ) {
		BigDecimal	ret = convert( Unit.DEGREE ).h;
		ret = ret.add( a.convert( Unit.DEGREE ).h );
		return new Angle( ret, Unit.DEGREE ).convert( unit );
	}
	/**
	 * 角度の減算。this - a を返します。
	 * @param a 減算値。
	 * @return 減算値。thisと同じ単位です。
	 */
	public Angle subtract( Angle a ) {
		BigDecimal	ret = convert( Unit.DEGREE ).h;
		ret = ret.subtract( a.convert( Unit.DEGREE ).h );
		return new Angle( ret, Unit.DEGREE ).convert( unit );
	}

	/**
	 * 角度の比較。単位が異なる場合も変換して比較します。
	 * @param a 比較対象。
	 * @return this&lt;a なら負数、this==a なら0、this&gt;a なら正数。
	 */
	public int compareTo( Angle a ) {
		Angle[]	c = new Angle[] {
			this.convert( Unit.DEGREE ),
			a.convert( Unit.DEGREE )
		};
		return c[0].h.compareTo( c[1].h );
	}

	/**
	 * 角度の比較。単位が異なる場合も変換して比較します。
	 * @param o 比較対象。
	 * @return true:一致、false:不一致。
	 */
	public boolean equals( Object o ) {
		if ( !(o instanceof Angle) )	return false;
		return compareTo( (Angle)o ) == 0;
	}

	/**
	 * ハッシュ値の取得。
	 * @return ハッシュ値。
	 */
	public int hashCode() {
		return convert( Unit.DEGREE ).h.hashCode();
	}
}

