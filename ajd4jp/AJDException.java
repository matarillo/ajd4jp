/*
 * AJD4JP
 * Copyright (c) 2011-2020  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp;

import java.time.DateTimeException;

/**
 * 日時指定の不正を表す例外です。
 */
public class AJDException extends DateTimeException {
	/**
	 * コンストラクタ。
	 * @param m 例外メッセージ。
	 */
	public AJDException( String m ) { super( m ); }

	/**
	 * コンストラクタ。
	 * @param e 例外。
	 */
	public AJDException(Throwable e) { super(e.getMessage(), e); }
}

