// Paraselene2 2.3.0 (http://paraselene.sourceforge.jp/) generated this source file.
/*
 * AJD4JP
 * Copyright (c) 2011-2012  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.web.view;

import paraselene.*;
import paraselene.tag.*;
import paraselene.tag.form.*;
import paraselene.tag.list.*;
import paraselene.tag.table.*;
import paraselene.tag.ruby.*;
import paraselene.css.*;
import paraselene.dyna.*;
import paraselene.supervisor.*;

/**
 * jp_calendar.html
 */
public abstract class JpCalendarHtml extends XenoPage {
private static final long serialVersionUID = 1L;
/**
 * コンストラクタ。
 */
public JpCalendarHtml() {
super();
}
/**
 * 初期化。
 */
public void init() {
clear();
try{
paraselene.tag.Tag tag45=new Tag("html",false);
paraselene.tag.Tag tag46=new Tag("head",false);
paraselene.tag.Tag tag48=new Tag("meta",true);
tag48.setAttribute(new Attribute("content",new Text("text/css")),new Attribute("http-equiv",new Text("Content-Style-Type")));
tag46.addHTMLPart(tag48);
paraselene.tag.Tag tag49=new Tag("title",false);
Text text27=new Text("日付選択");
tag49.addHTMLPart(text27);
tag46.addHTMLPart(tag49);
paraselene.tag.Tag tag50=new Tag("style",false);
tag50.setAttribute(new Attribute("type",new Text("text/css")));
Text text28=new Text("\n*{}\ndiv.ajd4jp_outline, div.ajd4jp_outline * {\nmargin: 0px;\npadding: 0px;\n}\ndiv.ajd4jp_outline * table, div.ajd4jp_outline * tr, div.ajd4jp_outline * td {\nborder:0px none;\n}\ntable.ajd4jp {\nfont-size:80%;\nbackground-color:white;\n}\ntable.ajd4jp a{\ntext-decoration:none;\ncolor:black;\n}\ntable.ajd4jp a:hover{  \n  background-color:MEDIUMSPRINGGREEN;\n}\ndiv.ajd4jp_calendar td{\nborder:0px;\ntext-align:right;\npadding:0px 4px;\nmargin: 0px;\ncolor:LIGHTGREY;\n}\ndiv.ajd4jp_calendar td.sun a {\ncolor:red;\n}\ndiv.ajd4jp_calendar td.sat a {\ncolor:blue;\n}\n.ajd4jp_allow {\nfont-size:150%;\nfont-weight:bold;\ntext-shadow: 1px 1px 2px gray;\n}\n");
tag50.addHTMLPart(text28);
tag46.addHTMLPart(tag50);
tag45.addHTMLPart(tag46);
paraselene.tag.Tag tag51=new Tag("body",false);
paraselene.tag.Tag tag52=new Tag("div",false);
tag52.setAttribute(new Attribute("class",new Text("ajd4jp_outline")));
paraselene.tag.form.Form tag53=new Form();
tag53.setAttribute(new Attribute("id",new Text("ajd4jp_form")),new Attribute("style",new Style(Property.create("margin:0px;padding:0px;","UTF-8"))),new Attribute("action",new URIValue("jp_calendar.html")));
paraselene.tag.table.Table tag54=new Table();
tag54.setAttribute(new Attribute("class",new Text("ajd4jp")));
paraselene.tag.table.Line tag56=new Line();
paraselene.tag.table.Column tag57=new Column(Column.Type.DATA);
tag57.setAttribute(new Attribute("align",new Text("right")));
paraselene.tag.Anchor tag58=new Anchor();
tag58.setAttribute(new Attribute("title",new Text("閉じる")),new Attribute("class",new Text("ajd4jp_allow")),new Attribute("href",new URIValue("jp_calendar.html?close=")));
Text text29=new Text("×");
tag58.addHTMLPart(text29);
tag57.addHTMLPart(tag58);
tag56.addHTMLPart(tag57);
tag54.addHTMLPart(tag56);
paraselene.tag.table.Line tag59=new Line();
paraselene.tag.table.Column tag60=new Column(Column.Type.DATA);
tag60.setAttribute(new Attribute("align",new Text("center")));
paraselene.tag.Anchor tag61=new Anchor();
tag61.setAttribute(new Attribute("title",new Text("前月へ")),new Attribute("name",new Text("prev")),new Attribute("class",new Text("ajd4jp_allow")),new Attribute("href",new URIValue("jp_calendar.html?prev=")));
Text text30=new Text("◀");
tag61.addHTMLPart(text30);
tag60.addHTMLPart(tag61);
Text text31=new Text("    ");
tag60.addHTMLPart(text31);
paraselene.tag.form.Select tag62=new Select();
tag62.setAttribute(new Attribute("style",new Style(Property.create("font-size:85%;","UTF-8"))),new Attribute("name",new Text("year")));
paraselene.tag.form.SelectItem tag63=new SelectItem();
tag63.setAttribute(new Attribute("value",new Text("1900")));
Text text35=new Text("1900");
tag63.addHTMLPart(text35);
tag62.addHTMLPart(tag63);
paraselene.tag.form.SelectItem tag64=new SelectItem();
tag64.setAttribute(new Attribute("value",new Text("1901")));
Text text36=new Text("1901");
tag64.addHTMLPart(text36);
tag62.addHTMLPart(tag64);
tag60.addHTMLPart(tag62);
Text text37=new Text("年 ");
tag60.addHTMLPart(text37);
paraselene.tag.form.Select tag65=new Select();
tag65.setAttribute(new Attribute("style",new Style(Property.create("font-size:85%;","UTF-8"))),new Attribute("name",new Text("month")));
paraselene.tag.form.SelectItem tag66=new SelectItem();
tag66.setAttribute(new Attribute("value",new Text("1")));
Text text39=new Text("1");
tag66.addHTMLPart(text39);
tag65.addHTMLPart(tag66);
paraselene.tag.form.SelectItem tag67=new SelectItem();
tag67.setAttribute(new Attribute("value",new Text("12")));
Text text40=new Text("12");
tag67.addHTMLPart(text40);
tag65.addHTMLPart(tag67);
tag60.addHTMLPart(tag65);
Text text41=new Text("月  ");
tag60.addHTMLPart(text41);
paraselene.tag.form.Button tag68=new Button(Button.Type.SUBMIT);
tag68.setAttribute(new Attribute("style",new Style(Property.create("font-size:90%;","UTF-8"))),new Attribute("name",new Text("change")),new Attribute("value",new Text("変更")),new Attribute("type",new Text("submit")));
tag60.addHTMLPart(tag68);
Text text44=new Text("  ");
tag60.addHTMLPart(text44);
paraselene.tag.Anchor tag69=new Anchor();
tag69.setAttribute(new Attribute("title",new Text("翌月へ")),new Attribute("name",new Text("next")),new Attribute("class",new Text("ajd4jp_allow")),new Attribute("href",new URIValue("jp_calendar.html?next=")));
Text text46=new Text("▶");
tag69.addHTMLPart(text46);
tag60.addHTMLPart(tag69);
tag59.addHTMLPart(tag60);
tag54.addHTMLPart(tag59);
paraselene.tag.table.Line tag70=new Line();
paraselene.tag.table.Column tag71=new Column(Column.Type.DATA);
paraselene.tag.Tag tag72=new Tag("div",false);
tag72.setAttribute(new Attribute("class",new Text("ajd4jp_calendar")));
paraselene.tag.table.Table tag73=new Table();
tag73.setAttribute(new Attribute("name",new Text("main")));
paraselene.tag.table.Line tag75=new Line();
paraselene.tag.table.Column tag76=new Column(Column.Type.DATA);
tag76.setAttribute(new Attribute("class",new Text("sun")));
paraselene.tag.Anchor tag77=new Anchor();
tag77.setAttribute(new Attribute("href",new URIValue("jp_calendar.html")));
Text text47=new Text("　1");
tag77.addHTMLPart(text47);
tag76.addHTMLPart(tag77);
tag75.addHTMLPart(tag76);
paraselene.tag.table.Column tag78=new Column(Column.Type.DATA);
paraselene.tag.Anchor tag79=new Anchor();
tag79.setAttribute(new Attribute("href",new URIValue("jp_calendar.html")));
Text text48=new Text("　2");
tag79.addHTMLPart(text48);
tag78.addHTMLPart(tag79);
tag75.addHTMLPart(tag78);
paraselene.tag.table.Column tag80=new Column(Column.Type.DATA);
paraselene.tag.Anchor tag81=new Anchor();
tag81.setAttribute(new Attribute("href",new URIValue("jp_calendar.html")));
Text text49=new Text("　3");
tag81.addHTMLPart(text49);
tag80.addHTMLPart(tag81);
tag75.addHTMLPart(tag80);
paraselene.tag.table.Column tag82=new Column(Column.Type.DATA);
paraselene.tag.Anchor tag83=new Anchor();
tag83.setAttribute(new Attribute("href",new URIValue("jp_calendar.html")));
Text text50=new Text("　4");
tag83.addHTMLPart(text50);
tag82.addHTMLPart(tag83);
tag75.addHTMLPart(tag82);
paraselene.tag.table.Column tag84=new Column(Column.Type.DATA);
paraselene.tag.Anchor tag85=new Anchor();
tag85.setAttribute(new Attribute("href",new URIValue("jp_calendar.html")));
Text text51=new Text("　5");
tag85.addHTMLPart(text51);
tag84.addHTMLPart(tag85);
tag75.addHTMLPart(tag84);
paraselene.tag.table.Column tag86=new Column(Column.Type.DATA);
paraselene.tag.Anchor tag87=new Anchor();
tag87.setAttribute(new Attribute("href",new URIValue("jp_calendar.html")));
Text text52=new Text("　6");
tag87.addHTMLPart(text52);
tag86.addHTMLPart(tag87);
tag75.addHTMLPart(tag86);
paraselene.tag.table.Column tag88=new Column(Column.Type.DATA);
tag88.setAttribute(new Attribute("class",new Text("sat")));
paraselene.tag.Anchor tag89=new Anchor();
tag89.setAttribute(new Attribute("href",new URIValue("jp_calendar.html")));
Text text53=new Text("　7");
tag89.addHTMLPart(text53);
tag88.addHTMLPart(tag89);
tag75.addHTMLPart(tag88);
tag73.addHTMLPart(tag75);
tag72.addHTMLPart(tag73);
tag71.addHTMLPart(tag72);
tag70.addHTMLPart(tag71);
tag54.addHTMLPart(tag70);
tag53.addHTMLPart(tag54);
tag52.addHTMLPart(tag53);
tag51.addHTMLPart(tag52);
tag45.addHTMLPart(tag51);
setMainTag(tag45);
setDoctype(false,null);
addVersionMeta("Paraselene2 2.3.0 (http://paraselene.sourceforge.jp/)","Fri Dec 16 23:30:34 JST 2011");

super.init();
setInitialized( true );
}
catch( Exception e ) {
paraselene.supervisor.Option.debug( e );
}
}

private static PageID	page_id = null;
/**
 * ページファクトリーへのクラス追加。
 * 処理に先立って呼び出して下さい。
 * 以降、PageID を使ってアクセス可能となります。
 * @param pf 追加先ページファクトリー。
 * @return 発行されたPageID。
 */
public static PageID migrate( PageFactory pf ) throws Exception {
	if ( page_id != null )	return page_id;
	page_id = pf.addDefine( ajd4jp.web.logic.JpCalendarHtml.class );
	return page_id;
}
/**
 * ページファクトリーへのクラス追加。
 * ページファクトリーを、SandBox経由で取得します。
 * input/output 以外の処理から呼ぶと処理に失敗します。
 * @return 発行されたPageID。
 */
public static PageID migrate() throws Exception {
	if ( page_id != null )	return page_id;
	return migrate( SandBox.getCurrentRequestParameter().getSupervisor().getPageFactory() );
}

/**
 * ページIDの取得。
 * @return ページID。
 */
public paraselene.supervisor.PageID getID() {
try {
return migrate();
}
catch( Exception e ){}
return null;
}
/**
 * change.
 * <br>&lt;input style=&quot;font-size:90%;&quot; value=&quot;変更&quot; type=&quot;submit&quot;&gt;
 **/
public paraselene.tag.form.Button getChangeInput() {
return (paraselene.tag.form.Button)getTag( "change" );
}
/**
 * change.
 **/
public Tag[] getChangeTags() {
return getAllTag( "change" );
}
/**
 * next.
 * <br>&lt;a title=&quot;翌月へ&quot; class=&quot;ajd4jp_allow&quot; href=&quot;jp_calendar.html?next=&quot;&gt;
 **/
public paraselene.tag.Anchor getNextA() {
return (paraselene.tag.Anchor)getTag( "next" );
}
/**
 * next.
 **/
public Tag[] getNextTags() {
return getAllTag( "next" );
}
/**
 * month.
 * <br>&lt;select style=&quot;font-size:85%;&quot;&gt;
 **/
public paraselene.tag.form.Select getMonthSelect() {
return (paraselene.tag.form.Select)getTag( "month" );
}
/**
 * month.
 **/
public Tag[] getMonthTags() {
return getAllTag( "month" );
}
/**
 * year.
 * <br>&lt;select style=&quot;font-size:85%;&quot;&gt;
 **/
public paraselene.tag.form.Select getYearSelect() {
return (paraselene.tag.form.Select)getTag( "year" );
}
/**
 * year.
 **/
public Tag[] getYearTags() {
return getAllTag( "year" );
}
/**
 * prev.
 * <br>&lt;a title=&quot;前月へ&quot; class=&quot;ajd4jp_allow&quot; href=&quot;jp_calendar.html?prev=&quot;&gt;
 **/
public paraselene.tag.Anchor getPrevA() {
return (paraselene.tag.Anchor)getTag( "prev" );
}
/**
 * prev.
 **/
public Tag[] getPrevTags() {
return getAllTag( "prev" );
}
/**
 * main.
 * <br>&lt;table&gt;
 **/
public paraselene.tag.table.Table getMainTable() {
return (paraselene.tag.table.Table)getTag( "main" );
}
/**
 * main.
 **/
public Tag[] getMainTags() {
return getAllTag( "main" );
}
/**
 * name 属性の検証。
 * @param page 検証するページ
 * @param exclude 検証から除外する name。
 * @return エラーとなった name。
 */
public NameDefine[] inspectName(Page page,String ... exclude){
return DynamicPage.inspectName(page,new NameDefine[]{
new NameDefine("change",paraselene.tag.form.Button.class),
new NameDefine("next",paraselene.tag.Anchor.class),
new NameDefine("month",paraselene.tag.form.Select.class),
new NameDefine("year",paraselene.tag.form.Select.class),
new NameDefine("main",paraselene.tag.table.Table.class),
new NameDefine("prev",paraselene.tag.Anchor.class)},exclude);
}

}

