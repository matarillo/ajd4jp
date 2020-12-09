/*
 * AJD4JP
 * Copyright (c) 2011  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp;

/**
 * 日時指定の不正を表す例外です。
 */
public class AJDException extends Exception {
	/**
	 * コンストラクタ。
	 * @param m 例外メッセージ。
	 */
	public AJDException( String m ) { super( m ); }
}

