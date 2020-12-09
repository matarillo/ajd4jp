/*
 * AJD4JP
 * Copyright (c) 2011-2015  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.format;

import java.util.*;

/**
 * 文字列定数。
 */
public class C extends Format {
	/**
	 * 文字列が指定文字種別のみか判定します。<br>
	 * allow 指定文字種のみの文字列であれば true と判定します。<br>
	 * deny系の指定文字が存在すれば、即 false となります。
	 * allow する文字セットの特定文字のみを不可としたい場合などで使用して下さい。<br>
	 * @param s 判定文字列。
	 * @param allow 許可する文字。指定しない場合はnull。
	 * @param deny 許可しない文字。指定しない場合はnull。
	 * @param allow_type 許可する文字種別。指定しない場合はnull。
	 * @param deny_type 許可しない文字種別。指定しない場合はnull。
	 * @return true:許可する文字のみの文字列、false:許可されない文字が存在。<br>
	 * allow系の指定があれば、allow 以外が登場すれば false となります。<br>
	 * deny系のみ指定した場合、sにdenyが登場しなければ true となります。<br>
	 * sのみを指定すると、常に true です。<br>
	 * sがnullまたは空文字列であれば、常に true です。
	 */
	public static boolean checkAllow( String s, char[] allow, char[] deny, Type[] allow_type, Type[] deny_type ) {
		if ( s == null || s.length() == 0 )	return true;
		if ( deny != null ) {
			for ( char x: s.toCharArray() ) {
				for ( char y: deny ) {
					if ( x == y )	return false;
				}
			}
		}
		if ( deny_type != null ) {
			for ( Type t: deny_type ) {
				if ( s.length() != t.delete( s ).length() )	return false;
			}
		}
		if ( (allow == null || allow.length == 0) && (allow_type == null || allow_type.length == 0) )	return true;
		for ( Type t: allow_type ) {
			s = t.delete( s );
			if ( s.length() == 0 )	return true;
		}
		if ( allow == null || allow.length == 0 )	return false;
		for ( char x: s.toCharArray() ) {
			boolean	flag = false;
			for ( char y: allow ) {
				flag = x == y;
				if ( flag )	break;
			}
			if ( !flag )	return false;
		}
		return true;
	}
	/**
	 * 文字列が指定文字種別のみか判定します。
	 * @param s 判定文字列。
	 * @param allow 許可する文字。指定しない場合はnull。
	 * @param allow_type 許可する文字種別。
	 * @return true:許可する文字のみ、false:許可されない文字が存在。
	 */
	public static boolean checkAllow( String s, char[] allow, Type ... allow_type ) {
		return checkAllow( s, allow, null, allow_type, null );
	}
	/**
	 * 文字列が指定文字種別のみか判定します。
	 * @param s 判定文字列。
	 * @param allow_type 許可する文字種別。
	 * @return true:許可する文字のみ、false:許可されない文字が存在。
	 */
	public static boolean checkAllow( String s, Type ... allow_type ) {
		return checkAllow( s, null, null, allow_type, null );
	}

	/** 文字種別 */
	public interface Type{
		/**
		 * 入力文字列から該当文字種別を削除した文字列を返します。
		 * @param org 判定文字列。
		 * @return 削除後の文字列。
		 */
		public String delete( String org );
	}
	/**
	 * ホワイトスペース。改行やタブなどの制御コードも含まれます。
	 */
	public enum Space implements Type {
		/** 全角空白 */
		FULL( '　' ),
		/** 半角空白 */
		HALF( ' ' );
		private char sp;
		private Space( char s ) {
			sp = s;
		}
		/**
		 * 入力文字列から該当文字種別を削除した文字列を返します。
		 * @param org 判定文字列。
		 * @return 削除後の文字列。
		 */
		public String delete( String org ) {
			if ( org == null )	return null;
			StringBuilder	buf = new StringBuilder();
			for ( char ch : org.toCharArray() ) {
				if ( ch == sp )	continue;
				if ( ch == 0x7f )	continue;
				if ( ch < ' ' )	continue;
				buf = buf.append( ch );
			}
			return buf.toString();
		}
		/**
		 * 全角半角変換。このインスタンスが表す文字へ変換します。
		 * @param org 変換元。
		 * @return 変換後文字列。
		 */
		public String convert( String org ) {
			if ( org == null )	return null;
			Space	anti = this == FULL?	HALF:	FULL;
			char[]	ch = org.toCharArray();
			boolean	flag = false;
			for ( int i = 0; i < ch.length; i++ ) {
				if ( ch[i] != anti.sp )	continue;
				ch[i] = sp;
				flag = true;
			}
			if ( flag )	return new String( ch );
			return org;
		}
	}
	/**
	 * 記号。
	 * !&quot;#$%&amp;'()*+,-./:;&lt;=&gt;?@[\]^_`{|}~｡｢｣､･ﾞﾟ
	 */
	public enum Symbol implements Type {
		/** 全角記号 */
		FULL( "！”＃＄％＆’（）＊＋，－．／：；＜＝＞？＠［￥］＾＿‘｛｜｝～｡「」､・゛゜".toCharArray() ),
		/** 半角記号 */
		HALF( "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~｡｢｣､･ﾞﾟ".toCharArray() );
		private char[]	data;
		private HashMap<Character, Integer>	map = new HashMap<Character, Integer>();
		private Symbol( char[] ch ) {
			data = ch;
			for ( int i = 0; i < ch.length; i++ )	map.put( ch[i], i );
		}
		/**
		 * 入力文字列から該当文字種別を削除した文字列を返します。
		 * @param org 判定文字列。
		 * @return 削除後の文字列。
		 */
		public String delete( String org ) {
			if ( org == null )	return null;
			StringBuilder	buf = new StringBuilder();
			for ( char ch : org.toCharArray() ) {
				if ( map.get( ch ) == null )	buf = buf.append( ch );
			}
			return buf.toString();
		}
		/**
		 * 全角半角変換。このインスタンスが表す文字へ変換します。
		 * @param org 変換元。
		 * @return 変換後文字列。
		 */
		public String convert( String org ) {
			if ( org == null )	return null;
			Symbol	anti = this == FULL?	HALF:	FULL;
			char[]	ch = org.toCharArray();
			boolean	flag = false;
			for ( int i = 0; i < ch.length; i++ ) {
				Integer	no = anti.map.get( ch[i] );
				if ( no == null )	continue;
				ch[i] = data[no];
				flag = true;
			}
			if ( flag )	return new String( ch );
			return org;
		}
	}
	/**
	 * 数字。
	 * 0123456789
	 */
	public enum Numeral implements Type {
		/** 全角アラビア数字 */
		FULL( FullArabia.num ),
		/** 半角アラビア数字 */
		HALF( HalfArabia.num ),
		/** 漢数字 */
		JAPAN( Japan.num );
		private char[]	data;
		private HashMap<Character, Integer>	map = new HashMap<Character, Integer>();
		private Numeral( char[] ch ) {
			data = ch;
			for ( int i = 0; i < ch.length; i++ )	map.put( ch[i], i );
		}
		/**
		 * 入力文字列から該当文字種別を削除した文字列を返します。
		 * @param org 判定文字列。
		 * @return 削除後の文字列。
		 */
		public String delete( String org ) {
			if ( org == null )	return null;
			StringBuilder	buf = new StringBuilder();
			for ( char ch : org.toCharArray() ) {
				if ( map.get( ch ) == null )	buf = buf.append( ch );
			}
			return buf.toString();
		}
		/**
		 * 文字変換。このインスタンスが表す文字へ変換します。
		 * @param org 変換元。
		 * @param target 変換対象の文字。
		 * これを指定しなかった場合、全指定とみなされます。
		 * @return 変換後文字列。
		 */
		public String convert( String org, Numeral ... target ) {
			if ( org == null )	return null;
			if ( target == null || target.length == 0 )	return convert( org, values() );
			char[]	ch = org.toCharArray();
			boolean	flag = false;
			for ( int i = 0; i < ch.length; i++ ) {
				Integer	no = null;
				for ( Numeral t: target ) {
					if ( t == this )	continue;
					no = t.map.get( ch[i] );
					if ( no != null )	break;
				}
				if ( no == null )	continue;
				ch[i] = data[no];
				flag = true;
			}
			if ( flag )	return new String( ch );
			return org;
		}
	}
	/**
	 * 英字。A-Z
	 */
	public enum Alphabet implements Type {
		/** 全角英大文字 */
		FULL_UP( 'Ａ', 'Ｚ' ),
		/** 半角英大文字 */
		HALF_UP( 'A', 'Z' ),
		/** 全角英小文字 */
		FULL_LOW( 'ａ', 'ｚ' ),
		/** 半角英小文字 */
		HALF_LOW( 'a', 'z' );
		private static final int MAX = 'z' - 'a';
		private char start, end;
		private Alphabet( char s, char e ) {
			start = s;
			end = e;
		}
		/**
		 * 入力文字列から該当文字種別を削除した文字列を返します。
		 * @param org 判定文字列。
		 * @return 削除後の文字列。
		 */
		public String delete( String org ) {
			if ( org == null )	return null;
			StringBuilder	buf = new StringBuilder();
			for ( char ch : org.toCharArray() ) {
				if ( ch < start || ch > end )	buf = buf.append( ch );
			}
			return buf.toString();
		}
		/**
		 * 文字変換。このインスタンスが表す文字へ変換します。
		 * @param org 変換元。
		 * @param target 変換対象の文字。
		 * これを指定しなかった場合、全指定とみなされます。
		 * @return 変換後文字列。
		 */
		public String convert( String org, Alphabet ... target ) {
			if ( org == null )	return null;
			if ( target == null || target.length == 0 )	return convert( org, values() );
			char[]	ch = org.toCharArray();
			boolean	flag = false;
			for ( int i = 0; i < ch.length; i++ ) {
				Integer	no = null;
				for ( Alphabet t: target ) {
					if ( t == this )	continue;
					int sub = ch[i] - t.start;
					if ( sub < 0 || sub > MAX )	continue;
					no = sub;
					break;
				}
				if ( no == null )	continue;
				ch[i] = (char)((int)start + no);
				flag = true;
			}
			if ( flag )	return new String( ch );
			return org;
		}
	}
	/**
	 * 日本語かな。あ-ん
	 */
	public enum Kana implements Type {
		/** 全角カタカナ */
		FULL(
"ア","イ","ウ","エ","オ","カ","キ","ク","ケ","コ",
"サ","シ","ス","セ","ソ","タ","チ","ツ","テ","ト",
"ナ","ニ","ヌ","ネ","ノ","ハ","ヒ","フ","ヘ","ホ",
"マ","ミ","ム","メ","モ","ヤ","ユ","ヨ",
"ラ","リ","ル","レ","ロ","ワ","ヰ","ヱ","ヲ","ン",
"ガ","ギ","グ","ゲ","ゴ","ザ","ジ","ズ","ゼ","ゾ",
"ダ","ヂ","ヅ","デ","ド","バ","ビ","ブ","ベ","ボ",
"パ","ピ","プ","ペ","ポ","ヴ","ー",
"ァ","ィ","ゥ","ェ","ォ","ャ","ュ","ョ","ッ"
		),
		/** 半角カタカナ */
		HALF(
"ｱ","ｲ","ｳ","ｴ","ｵ","ｶ","ｷ","ｸ","ｹ","ｺ",
"ｻ","ｼ","ｽ","ｾ","ｿ","ﾀ","ﾁ","ﾂ","ﾃ","ﾄ",
"ﾅ","ﾆ","ﾇ","ﾈ","ﾉ","ﾊ","ﾋ","ﾌ","ﾍ","ﾎ",
"ﾏ","ﾐ","ﾑ","ﾒ","ﾓ","ﾔ","ﾕ","ﾖ",
"ﾗ","ﾘ","ﾙ","ﾚ","ﾛ","ﾜ","ｨﾟ","ｪﾟ","ｦ","ﾝ",
"ｶﾞ","ｷﾞ","ｸﾞ","ｹﾞ","ｺﾞ","ｻﾞ","ｼﾞ","ｽﾞ","ｾﾞ","ｿﾞ",
"ﾀﾞ","ﾁﾞ","ﾂﾞ","ﾃﾞ","ﾄﾞ","ﾊﾞ","ﾋﾞ","ﾌﾞ","ﾍﾞ","ﾎﾞ",
"ﾊﾟ","ﾋﾟ","ﾌﾟ","ﾍﾟ","ﾎﾟ","ｳﾞ","ｰ",
"ｧ","ｨ","ｩ","ｪ","ｫ","ｬ","ｭ","ｮ","ｯ"
		),
		/** 全角ひらがな */
		HIRA(
"あ","い","う","え","お","か","き","く","け","こ",
"さ","し","す","せ","そ","た","ち","つ","て","と",
"な","に","ぬ","ね","の","は","ひ","ふ","へ","ほ",
"ま","み","む","め","も","や","ゆ","よ",
"ら","り","る","れ","ろ","わ","ゐ","ゑ","を","ん",
"が","ぎ","ぐ","げ","ご","ざ","じ","ず","ぜ","ぞ",
"だ","ぢ","づ","で","ど","ば","び","ぶ","べ","ぼ",
"ぱ","ぴ","ぷ","ぺ","ぽ","う゛","ー",
"ぁ","ぃ","ぅ","ぇ","ぉ","ゃ","ゅ","ょ","っ"
		);
		private String[]	data;
		private HashMap<String, Integer>	map = new HashMap<String, Integer>();
		private Kana( String ... s ) {
			data = s;
			for ( int i = 0; i < s.length; i++ )	map.put( s[i], i );
		}
		/**
		 * 入力文字列から該当文字種別を削除した文字列を返します。
		 * @param org 判定文字列。
		 * @return 削除後の文字列。
		 */
		public String delete( String org ) {
			if ( org == null )	return null;
			StringBuilder	buf = new StringBuilder();
			int	len = org.length();
			String	test;
			for ( int i = 0; i < len; i++ ) {
				if ( i < (len - 1) ) {
					test = org.substring( i , i + 2 );
					if ( map.get( test ) != null ) {
						i++;
						continue;
					}
				}
				test = org.substring( i, i + 1 );
				if ( map.get( test ) != null )	continue;
				buf = buf.append( test );
			}
			return buf.toString();
		}
		private int getNo( String s, Kana ... target ) {
			for ( Kana k: target ) {
				if ( k == this )	continue;
				Integer	no = k.map.get( s );
				if ( no != null )	return no;
			}
			return -1;
		}
		/**
		 * 文字変換。このインスタンスが表す文字へ変換します。
		 * @param org 変換元。
		 * @param target 変換対象の文字。
		 * これを指定しなかった場合、全指定とみなされます。
		 * @return 変換後文字列。
		 */
		public String convert( String org, Kana ... target ) {
			if ( org == null )	return null;
			if ( target == null || target.length == 0 )	return convert( org, values() );
			StringBuilder	buf = new StringBuilder();
			int	len = org.length();
			boolean	flag = false;
			for ( int i = 0; i < len; i++ ) {
				int	no = -1;
				if ( i < (len - 1) ) {
					no = getNo( org.substring( i, i + 2 ), target );
					if ( no >= 0 ) {
						buf = buf.append( data[no] );
						i++;
						flag = true;
						continue;
					}
				}
				String	test = org.substring( i, i + 1 );
				no = getNo( test, target );
				if ( no >= 0 ) {
					buf = buf.append( data[no] );
					flag = true;
				}
				else	buf = buf.append( test );
			}
			if ( flag )	return buf.toString();
			return org;
		}
	}

	private String str;
	/**
	 * コンストラクタ。
	 * @param s 文字列定数。
	 */
	public C( String s ) {
		super( Dummy.dmy );
		str = s;
		if ( str == null )	str = "";
	}

	int getNum( ajd4jp.Day date ) {
		return 0;
	}
	String getHead() {
		return str;
	}
}
