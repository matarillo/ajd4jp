/*
 * AJD4JP
 * Copyright (c) 2011-2012  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.web;


import paraselene.tag.*;
import paraselene.dyna.*;

/**
 * 派生クラス置き換え方法の提供。
 */
public class UI implements GrantTagProvider {
	private static final String	CLASS_NAME = "ajd4jp.web.JpCalendarButton";
	private static final String NEW_STRING = "new " + CLASS_NAME + "()";

	public GrantTag getGrantTag( Tag tag ) throws DynamicPageException {
		Attribute	cls = tag.getAttribute( "class" );
		if ( cls == null )	return null;
		if ( !CLASS_NAME.equals( cls.getString() ) )	return null;
		if ( !tag.getName().equals( "button" ) )	return null;
		return new GrantTag() {
			 public String getNewString() throws DynamicPageException {
			 	return NEW_STRING;
			 }
			 public Tag getNewTag() throws DynamicPageException {
			 	return new JpCalendarButton();
			 }
		};
	}
}

