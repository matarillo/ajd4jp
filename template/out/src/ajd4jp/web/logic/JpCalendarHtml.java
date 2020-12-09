// Paraselene2 2.3.0 (http://paraselene.sourceforge.jp/) generated this source file.
/*
 * AJD4JP
 * Copyright (c) 2011-2012  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.web.logic;

import ajd4jp.*;
import ajd4jp.web.*;
import paraselene.*;
import paraselene.supervisor.*;
import paraselene.tag.*;
import paraselene.tag.form.*;
import paraselene.tag.list.*;
import paraselene.tag.table.*;
import paraselene.tag.ruby.*;
import paraselene.css.*;
import paraselene.ui.*;

/**
 * jp_calendar.html
 */
public class JpCalendarHtml extends ajd4jp.web.view.JpCalendarHtml implements EphemeralPage {
	private static final long serialVersionUID = 1L;

	/**
	 * コンストラクタ。
	 */
	public JpCalendarHtml() {
		super();
		init();
	}

	/**
	 * 初期化。
	 */
	public void init(){
		super.init();
		// モックアップHTMLの内容と異なる内容で初期化したい場合は
		// super.init()の後に変更用の処理を記述して下さい。
		// このメソッドは上記コンストラクタの中から呼ばれます。
		// インスタンス発生はバックグラウンドで非同期に処理していますので
		// HTTPリクエスト発生よりずっと過去に初期化されます。
		// このため、ユーザーのトランザクションに応じた条件分岐を設けても
		// 意図した振る舞いになりません。
		replaceFeedbackURI( "jp_calendar.html" );
		getYearSelect().setSubmitEvent( Control.SubmitEvent.CHANGE );
		getMonthSelect().setSubmitEvent( Control.SubmitEvent.CHANGE );
	}

	/**
	 * 別名URI設定。nullを返すと別名は設定しません。
	 * 最低４文字指定して下さい。
	 * ".na"で終えると完全一致となりますが、".na"で無ければ先頭一致となります。
	 * @return URI。
	 */
	public String getAliasURI() {
		return null;
	}

	/**
	 * 入力値の検証を行う。
	 * このメソッドが呼ばれる際には必ずセッションが発生しています。
	 * 入力値のエラーチェックや入力値に即した動作を記述します。
	 * @param req リクエスト内容。
	 * @param fw デフォルト遷移先。
	 * @exception PageException 処理の継続が不可能(ブラウザには500を返す)。
	 */
	public Forward inputMain( RequestParameter req, Forward fw ) throws PageException {
		// fwにはモックアップと同じ遷移先が設定されています。
		// 別の遷移先としたい場合は、新しくForwardインスタンスを生成して
		// リターンして下さい。
		try {
			if ( req.getItem( "close" ) != null )	return new Closure();
			RequestItem	day = req.getItem( "day" );
			int	year = Integer.parseInt( getYearSelect().getValueString() );
			int	month = Integer.parseInt( getMonthSelect().getValueString() );
			if ( day != null ) {
				return new EphemeralClosure( new AJD( year, month,
					Integer.parseInt( day.getValue() )
				) );
			}
			if ( req.getItem( "prev" ) != null )	mon = mon.add( -1 );
			else if ( req.getItem( "next" ) != null )	mon = mon.add( 1 );
			else	mon = new Month( year, month );
			return new Feedback();
		}
		catch( AJDException e ) {
			throw new PageException( e );
		}
	}

	public static class Param implements java.io.Serializable {
		private static final long serialVersionUID = 1L;
		AJD	from, mine, to;
		AJD[]	ex;
		public Param( AJD f, AJD m, AJD t, AJD ... e ) {
			from = f.trim();
			if ( m != null ) {
				mine = m.trim();
			}
			to = t.trim();
			if ( e != null ) {
				ex = new AJD[e.length];
				for ( int i = 0; i < ex.length; i++ ) {
					ex[i] = e[i].trim();
				}
			}
			else	e = new AJD[0];
		}
		public void setMine( AJD m ) {
			mine = m;
		}
	}
	private Param param;
	private Month mon;
	public void firstOutput( RequestParameter req, Object p ) throws PageException {
		AJD	today = new AJD();
		param = (Param)p;
		Select	year = getYearSelect();
		Select	month = getMonthSelect();
		year.makeSequenceNo( param.from.getYear(), param.to.getYear() );
		month.makeSequenceNo( 1, 12 );
		if ( param.mine.compareTo( param.from ) < 0 )	param.mine = param.from;
		else if ( param.mine.compareTo( param.to ) > 0 )	param.mine = param.to;
		try {
			mon = new Month( param.mine.getYear(), param.mine.getMonth() );
		}
		catch( AJDException e ) {
			throw new PageException( e );
		}
		addOnLoadStyle( getFirstTagByType( "style" ).getValueString() );
	}

	/**
	 * 初回outputの呼び出しメイン処理。
	 * @param req リクエスト内容。
	 * @exception PageException 処理の継続が不可能(ブラウザには500を返す)。
	 */
	public void firstOutputMain( RequestParameter req ) throws PageException {
		// 初期化処理があれば記述して下さい。
		firstOutput( req, null );
	}

	/**
	 * 出力情報の設定を行う。
	 * @param from 遷移元ページ。直接呼ばれている場合はnullです。
	 * @param req リクエスト内容。
	 * @return 出力ページ。
	 * nullを返すとthisをリターンしたのと同じ扱いにされます。
	 * @exception PageException 処理の継続が不可能(ブラウザには500を返す)。
	 */
	public Page outputMain( Page from, RequestParameter req ) throws PageException {
		// 出力内容を設定します。
		// 初期化したい場合は、init()をコールして下さい。
		Select	year = getYearSelect();
		Select	month = getMonthSelect();
		year.setValueString( Integer.toString( mon.getYear() ) );
		month.setValueString( Integer.toString( mon.getMonth() ) );
		AJD[]	all = mon.getDays();
		Table	table = getMainTable();
		table.removeLine();
		for ( int i = 1; ; i++ ) {
			AJD[]	w = mon.getWeek( i, false );
			if ( w == null )	break;
			Line	line = new Line();
			for ( int j = 0; j < w.length; j++ ) {
				Column	column = new Column( Column.Type.DATA );
				line.addColumn( column );
				if ( w[j] == null )	continue;
				int	day = w[j].getDay();
				String	val = Integer.toString( day );
				String	str = null;
				if ( day < 10 ) {
					StringBuilder	buf = new StringBuilder( "　" );
					str = buf.append( val ).toString();
				}
				else	str = val;
				boolean	flag = false;
				for ( AJD	ex: param.ex ) {
					if ( ex.equals( w[j] ) ) {
						flag = true;
						break;
					}
				}
				if ( flag || w[j].compareTo( param.from ) < 0 || w[j].compareTo( param.to ) > 0 ) {
					column.setValueString( str );
					continue;
				}

				Holiday	h = Holiday.getHoliday( w[j] );
				if ( j == 0 || h != null )	column.setAttribute( "class", "sun" );
				else if ( j == 6 )	column.setAttribute( "class", "sat" );
				Anchor	a = new Anchor();
				a.setAttribute( createPageToURI( "href", getID(), null, new QueryItem( "day", val ) ) );
				if ( h != null )	a.setAttribute( "title", h.getName( w[j] ) );
				a.setValueString( str );
				column.addHTMLPart( a );
			}
			table.addLine( line );
		}
		AJD	prev = all[0].addDay( -1 );
		AJD	next = all[all.length - 1].addDay( 1 );
		getPrevA().setVisible( all[0].addDay( -1 ).compareTo( param.from ) >= 0 );
		getNextA().setVisible( all[all.length - 1].addDay( 1 ).compareTo( param.to ) <= 0 );
		getChangeInput().setVisible( !req.wasUsedAjax() );
		return this;
	}

	public CSSValuable[] getPopupBackGround() {
		return new CSSValuable[]{ new Color( WebColor.WHITE ) };
	}
}

