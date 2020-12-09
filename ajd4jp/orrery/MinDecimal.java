/*
 * AJD4JP
 * Copyright (c) 2011-2012  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.orrery;
import java.math.*;


class MinDecimal extends BigDecimal {
	private boolean nega_zero = false;

	boolean getNZ() { return nega_zero; }

	private BigDecimal nega( BigDecimal b ) {
		if ( nega_zero ) {
			if ( b.compareTo( BigDecimal.ZERO ) == 0 ) {
				return new MinDecimal( BigDecimal.ZERO, true );
			}
			b = BigDecimal.ZERO.subtract( b );
		}
		return new MinDecimal( b, false );
	}

	MinDecimal( BigDecimal org, boolean min ) {
		super( org.toString() );
		if ( org.compareTo( BigDecimal.ZERO ) == 0 )	nega_zero = min;
	}

	public BigDecimal divide( BigDecimal n ) {
		return nega( super.divide( n ) );
	}
	public BigDecimal divide( BigDecimal n, int m ) {
		return nega( super.divide( n, m ) );
	}
	public BigDecimal divide( BigDecimal n, int s, int m ) {
		return nega( super.divide( n, s, m ) );
	}
	public BigDecimal divide( BigDecimal n, int s, RoundingMode m ) {
		return nega( super.divide( n, s, m ) );
	}
	public BigDecimal divide( BigDecimal n, MathContext m ) {
		return nega( super.divide( n, m ) );
	}
	public BigDecimal divide( BigDecimal n, RoundingMode m ) {
		return nega( super.divide( n, m ) );
	}

	public BigDecimal[] divideAndRemainder( BigDecimal n ) {
		BigDecimal[]	ret = super.divideAndRemainder( n );
		ret[0] = nega( ret[0] );
		return ret;
	}
	public BigDecimal[] divideAndRemainder( BigDecimal n, MathContext m ) {
		BigDecimal[]	ret = super.divideAndRemainder( n, m );
		ret[0] = nega( ret[0] );
		return ret;
	}

	public BigDecimal divideToIntegralValue( BigDecimal n ) {
		return nega( super.divideToIntegralValue( n ) );
	}
	public BigDecimal divideToIntegralValue( BigDecimal n, MathContext m ) {
		return nega( super.divideToIntegralValue( n, m ) );
	}

	public BigDecimal multiply( BigDecimal n ) {
		return nega( super.multiply( n ) );
	}
	public BigDecimal multiply( BigDecimal n, MathContext m ) {
		return nega( super.multiply( n, m ) );
	}

	public BigDecimal negate() {
		if ( compareTo( BigDecimal.ZERO ) == 0 ) {
			return new MinDecimal( BigDecimal.ZERO, !nega_zero );
		}
		return nega( super.negate() );
	}
	public BigDecimal negate( MathContext m ) {
		if ( compareTo( BigDecimal.ZERO ) == 0 ) {
			return new MinDecimal( BigDecimal.ZERO, !nega_zero );
		}
		return nega( super.negate( m ) );
	}

	public BigDecimal pow( int n ) {
		if ( nega_zero ) {
			return new MinDecimal( BigDecimal.ZERO, n % 2 == 1 );
		}
		return nega( super.pow( n ) );
	}
	public BigDecimal pow( int n, MathContext m ) {
		if ( nega_zero ) {
			return new MinDecimal( BigDecimal.ZERO, n % 2 == 1 );
		}
		return nega( super.pow( n, m ) );
	}

	public BigDecimal round( MathContext m ) {
		if ( nega_zero )	return this;
		return nega( super.round( m ) );
	}

	public BigDecimal scaleByPowerOfTen( int n ) {
		if ( nega_zero ) return this;
		return nega( super.scaleByPowerOfTen( n ) );
	}

	public BigDecimal setScale( int s ) {
		if ( nega_zero ) return this;
		return nega( super.setScale( s ) );
	}
	public BigDecimal setScale( int s, int m ) {
		if ( nega_zero ) return this;
		return nega( super.setScale( s, m ) );
	}
	public BigDecimal setScale( int s, RoundingMode m ) {
		if ( nega_zero ) return this;
		return nega( super.setScale( s, m ) );
	}
	
	public int signum() {
		if ( nega_zero )	return -1;
		return super.signum();
	}

	public BigDecimal stripTrailingZeros() {
		if ( nega_zero ) return this;
		return nega( super.stripTrailingZeros() );
	}

	public String toEngineeringString() {
		String	ret = super.toEngineeringString();
		if ( nega_zero )	return "-" + ret;
		return ret;
	}
	public String toPlainString() {
		String	ret = super.toPlainString();
		if ( nega_zero )	return "-" + ret;
		return ret;
	}
	public String toString() {
		String	ret = super.toString();
		if ( nega_zero )	return "-" + ret;
		return ret;
	}
}



