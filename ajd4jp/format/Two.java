/*
 * AJD4JP
 * Copyright (c) 2011  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.format;

interface RuleWork {
	String toString( Numeral n, Integer t, Integer o );
}

/**
 * 2桁の数字。
 */
public abstract class Two extends Numeral {
	/**
	 * 特殊な数値の変換方法指定。
	 * 複数指定可能ですが、競合するルールは先に指定されたものが有効となります。
	 */
	public enum Rule {
		/**
		 * 0を零と表記する。
		 * <br>ex)零時。
		 */
		ZERO( new RuleWork(){
			public String toString( Numeral n, Integer t, Integer o ) {
				if ( t == null && o == 0 )	return "零";
				return null;
			}
		}),
		/**
		 * 1を元と表記する。
		 * <br>ex)元年。
		 */
		FIRST( new RuleWork(){
			public String toString( Numeral n, Integer t, Integer o ) {
				if ( t == null && o == 1 )	return "元";
				return null;
			}
		}),
		/**
		 * 10の場合、十で表記する。
		 * <br>ex)十日。
		 */
		TENTH( new RuleWork(){
			public String toString( Numeral n, Integer t, Integer o ) {
				if ( t == null )	return null;
				if ( t == 1 && o == 0 )	return "十";
				return null;
			}
		}),
		/**
		 * 20の場合、廿で表記する。
		 * <br>ex)廿日。
		 */
		TWENTIETH( new RuleWork(){
			public String toString( Numeral n, Integer t, Integer o ) {
				if ( t == null )	return null;
				if ( t == 2 && o == 0 )	return "廿";
				return null;
			}
		}),
		/**
		 * 30の場合、卅で表記する。
		 * <br>ex)卅日。
		 */
		THIRTIETH( new RuleWork(){
			public String toString( Numeral n, Integer t, Integer o ) {
				if ( t == null )	return null;
				if ( t == 3 && o == 0 )	return "卅";
				return null;
			}
		}),
		/**
		 * 10で割り切れる場合、0ではなく十で表記する。
		 * 10ならば、TENTH と同様。
		 * <br>ex)二十日。
		 */
		MULTIPLE_TEN( new RuleWork(){
			public String toString( Numeral n, Integer t, Integer o ) {
				if ( t == null || o != 0 )	return null;
				String	ret = TENTH.work( n, t, o );
				if ( ret != null )	return ret;
				StringBuilder	buf = new StringBuilder();
				return buf.append( n.getChar()[t] ).append( "十" ).toString();
			}
		}),
		/**
		 * 10～19の場合、1ではなく十で表記する。
		 * 10ならば、TENTH と同様。
		 * <br>ex)十一日。
		 */
		TENS( new RuleWork(){
			public String toString( Numeral n, Integer t, Integer o ) {
				if ( t == null )	return null;
				if ( t != 1 )	return null;
				String	ret = TENTH.work( n, t, o );
				if ( ret != null )	return ret;
				StringBuilder	buf = new StringBuilder( "十" );
				return buf.append( n.getChar()[o] ).toString();
			}
		}),
		/**
		 * 20～29の場合、2ではなく廿で表記する。
		 * 20ならば、TWENTIETH と同様。
		 * <br>ex)廿一日。
		 */
		TWENTIES( new RuleWork(){
			public String toString( Numeral n, Integer t, Integer o ) {
				if ( t == null )	return null;
				if ( t != 2 )	return null;
				String	ret = TWENTIETH.work( n, t, o );
				if ( ret != null )	return ret;
				StringBuilder	buf = new StringBuilder( "廿" );
				return buf.append( n.getChar()[o] ).toString();
			}
		}),
		/**
		 * 31～39の場合、3ではなく卅で表記する。
		 * 30ならば、THIRTIETH と同様。
		 * <br>ex)卅一日。
		 */
		THIRTIES( new RuleWork(){
			public String toString( Numeral n, Integer t, Integer o ) {
				if ( t == null )	return null;
				if ( t != 3 )	return null;
				String	ret = THIRTIETH.work( n, t, o );
				if ( ret != null )	return ret;
				StringBuilder	buf = new StringBuilder( "卅" );
				return buf.append( n.getChar()[o] ).toString();
			}
		}),
		/**
		 * 21以上の場合、三文字で表記する。
		 * 10で割り切れるならば、MULTIPLE_TEN と同様。
		 * 10～19の場合、TENS と同様。
		 * <br>ex)二十一日。
		 */
		TRIPLE( new RuleWork(){
			public String toString( Numeral n, Integer t, Integer o ) {
				if ( t == null )	return null;
				String	str = MULTIPLE_TEN.work( n, t, o );
				if ( str != null )	return str;
				str = TENS.work( n, t, o );
				if ( str != null )	return str;
				char[]	num = n.getChar();
				StringBuilder	buf = new StringBuilder();
				return buf.append( num[t] ).append( "十" ).append( num[o] ).toString();
			}
		});


		private RuleWork work;
		private Rule( RuleWork w ) { work = w; }
		String work( Numeral n, Integer t, Integer o ) {
			return work.toString( n, t, o );
		}
	}

	private String		pad = null;
	private Rule[]	spe = null;
	Two( String p, Rule ... s ) {
		super();
		pad = p;
		spe = s;
	}

	Two() { super(); }

	/**
	 * 文字列化。
	 * @param n 数値。この数値の絶対値の下二桁を使用します。
	 * @return 文字列。
	 */
	public String toString( int n ) {
		Integer[]	val = split( n );
		for ( Rule s:	spe ) {
			String	ret = s.work( this, val[0], val[1] );
			if ( ret != null ) {
				return ret;
			}
		}
		char[]	num = getChar();
		StringBuilder	buf = new StringBuilder();
		if ( val[0] != null )	buf = buf.append( num[val[0]] );
		else if ( pad != null )	buf = buf.append( pad );
		return buf.append( num[val[1]] ).toString();
	}

	private Integer[] split( int n ) {
		if ( n < 0 )	n *= -1;
		if ( n < 10 )	return new Integer[] { null, n };
		return new Integer[] {
			(n / 10) % 10, n % 10
		};
	}

}

