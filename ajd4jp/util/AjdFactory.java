/*
 * AJD4JP
 * Copyright (c) 2021  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp.util;

import java.time.ZoneId;

import ajd4jp.AJD;
import ajd4jp.AJDException;
import ajd4jp.iso.AJD310;
import ajd4jp.iso.UTC;

public class AjdFactory {
	public static AJD makeAJD(int yyyy, int mm, int dd, int hh, int mi, int ss, ZoneId zone) throws AJDException{
		if (zone == null || zone == AJD.OFFSET) { return new AJD(yyyy, mm, dd, hh, mi, ss); }
		else if (zone == UTC.OFFSET) { return new UTC(yyyy, mm, dd, hh, mi, ss); }
		return AJD310.of(yyyy, mm, dd, hh, mi, ss, zone);
	}
	public static AJD makeAJD(int yyyy, int mm, int dd, ZoneId zone) throws AJDException{
		return makeAJD(yyyy, mm, dd, 0, 0, 0, zone);
	}
	public static AJD makeAJD(Number n, ZoneId zone) {
		AJD ret = new AJD(n);
		if (zone == null || zone == AJD.OFFSET) { return ret; }
		else if (zone == UTC.OFFSET) { return new UTC(n); }
		return AJD310.of(ret, zone);
	}
	public static AJD now(ZoneId zone) {
		if (zone == null || zone == AJD.OFFSET) { return new AJD(); }
		else if (zone == UTC.OFFSET) { return new UTC(); }
		return AJD310.now(zone);
	}
}
