/*
 * AJD4JP
 * Copyright (c) 2011-2012  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.web;

import paraselene.*;
import paraselene.css.*;
import paraselene.supervisor.*;
import paraselene.tag.*;
import paraselene.tag.form.*;
import ajd4jp.*;
import ajd4jp.format.*;
import ajd4jp.web.logic.*;

/**
 * 日付選択ボタン。
 */
public class JpCalendarButton extends GPButton {
	private JpCalendarHtml.Param	param = null;
	private AJD	now = null;

	/**
	 * コンストラクタ。
	 */
	public JpCalendarButton() {
		super( GPButton.Type.SUBMIT );
		set( null );
	}

	/**
	 * 初期化。<br>
	 * ページに追加後、ボタンが表示される前に１回、必ず呼び出して下さい。<br>
	 * 表示済みボタンの選択可能日を変更する必要が出た場合は、
	 * 再度このメソッドを呼び出して下さい。
	 * @param from 選択可能開始日。null不可。
	 * @param cur 初期値。nullなら初期値なし。
	 * @param to 選択可能終了日。null不可。
	 * @param exclude 選択不可能日。
	 */
	public void init( AJD from, AJD cur, AJD to, AJD ... exclude ) {
		set( cur );
		if ( cur == null )	cur = to;
		param = new JpCalendarHtml.Param( from, cur, to, exclude );
		getAssignedPage().addOnLoadStyle(
			"button.ajd4jp\\\\.web\\\\.JpCalendarButton{",
			"border:2px outset;",
			"background-color:TRANSPARENT;",
			"font-size:75%;",
			"}"
		);
	}

	/**
	 * 値設定。
	 * @param val 設定値。nullなら未選択状態となる。
	 */
	public void set( AJD val ) {
		now = val;
		removeHTMLPart();
		if ( now == null ) {
			addHTMLPart( new Text( "----/--/--" ) );
			setValueString( null );
		}
		else {
			now = now.trim();
			Text	str = new Text( Formats.DATE.toString( now ) );
			addHTMLPart( str );
			setValueString( str.toString() );
		}
		if ( param != null && now != null )	param.setMine( now );
	}

	/**
	 * 値取得。
	 * @return 設定値。選ばれていない場合null。
	 */
	public AJD get() {
		return now;
	}

	public Forward beforeInput( Page page, RequestParameter req, Forward fw ) throws Exception {
		if ( param == null )	throw new Exception( "never called JpCalendarButton#init()" );
		fw = super.beforeInput( page, req, fw );
		if ( page.getClickedTag() != this )	return fw;
		PageID	id = JpCalendarHtml.migrate();
		Object	obj = SandBox.doModal( JpCalendarHtml.migrate(), EphemeralPosition.PULL_DOWN, this, param );
		if ( obj instanceof AJD )	set( (AJD)obj );
		return new Feedback();
	}
}

