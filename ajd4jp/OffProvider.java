/*
 * AJD4JP
 * Copyright (c) 2011-2021  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 休日を管理します。
 * 土日祝日・夏期休暇、のような法律に依存しないものも含めた休日を管理します。
 */
public class OffProvider {
	/**
	 * 休日を表します。
	 */
	public interface Off {
	}

	private static class Dummy implements Off {
	}

	static final Off dummy = new Dummy();

	private boolean holi_f = false;
	private Week[] every_week = new Week[0];

	private class MD {
		private int hash;

		MD(AJD ajd) {
			hash = ajd.getMonth() << 5 | ajd.getDay();
		}

		public int hashCode() {
			return hash;
		}

		public boolean equals(Object o) {
			return hash == o.hashCode();
		}
	}

	private HashMap<MD, Off> every_year = new HashMap<MD, Off>();

	private Off getMD(HashMap<MD, Off> md, AJD ajd) {
		if (md == null)
			return null;
		return md.get(new MD(ajd));
	}

	private static class W {
		private int m;
		private int seq;
		private Week week;
		private Off off;

		W(int mm, int s, Week w, Off o) {
			m = mm;
			seq = s;
			week = w;
			off = (o == null) ? OffProvider.dummy : o;
		}

		public Off getOff(AJD ajd) {
			if (ajd.getMonth() != m)
				return null;
			if (ajd.getWeek() != week)
				return null;
			try {
				Month mon = new Month(ajd.toYear(), m);
				if (ajd.trim().equals(mon.getWeek(seq, week)))
					return off;
			} catch (AJDException e) {
			}
			return null;
		}
	}

	private ArrayList<W> all_week = new ArrayList<W>();

	private Off getW(ArrayList<W> week, AJD ajd) {
		if (week == null)
			return null;
		for (W w : week) {
			Off off = w.getOff(ajd);
			if (off != null)
				return off;
		}
		return null;
	}

	private class Y<E> {
		private int from, to;
		private E data;

		Y(int f, int t, E e) {
			from = f;
			to = t;
			data = e;
		}

		E get(int y) {
			if (from > y || to < y)
				return null;
			return data;
		}

		E get(int f, int t) {
			if (from == f && to == t)
				return data;
			return null;
		}
	}

	private ArrayList<Y<HashMap<MD, Off>>> one_year = new ArrayList<Y<HashMap<MD, Off>>>();
	private ArrayList<Y<ArrayList<W>>> one_week = new ArrayList<Y<ArrayList<W>>>();

	/**
	 * コンストラクタ。
	 * 休日を全く持たないプロバイダを生成します。
	 */
	public OffProvider() {
	}

	/**
	 * コンストラクタ。
	 * 祝日の有効化指定、曜日指定を行います。
	 * @param holiday_f true:祝日を休日にします、false:祝日を休日にしません。
	 * @param week 毎週、指定曜日を休日にします。
	 */
	public OffProvider(boolean holiday_f, Week... week) {
		holi_f = holiday_f;
		every_week = week;
	}

	private void add(HashMap<MD, Off> map, AJD ajd, Off off) {
		if (off == null)
			off = dummy;
		map.put(new MD(ajd), off);
	}

	/**
	 * 毎年の、休日追加。
	 * @param ajd 指定の日(月・日)を休日にします。
	 * @param off その休日を表すインスタンス。
	 * nullを指定すると、ダミーインスタンスを割り当てます。
	 */
	public void addOffEveryYear(AJD ajd, Off off) {
		synchronized (every_year) {
			add(every_year, ajd, off);
		}
	}

	/**
	 * 毎年の、休日追加。
	 * @param from 指定の日(月・日)から休日にします。
	 * @param to 指定の日(月・日)までを休日にします。
	 * @param off その休日を表すインスタンス。
	 * nullを指定すると、ダミーインスタンスを割り当てます。
	 */
	public void addOffEveryYear(AJD from, AJD to, Off off) {
		try {
			from = new AJD(2000, from.getMonth(), from.getDay());
			to = new AJD(2000, to.getMonth(), to.getDay());
		} catch (AJDException e) {
		}
		while (true) {
			addOffEveryYear(from, off);
			if (from.getMonth() == to.getMonth() && from.getDay() == to.getDay())
				break;
			from = from.addDay(1);
		}
	}

	/**
	 * 毎年毎月の、休日追加。第n番目の曜日を休日にします。
	 * @param seq 何番目か？1～。
	 * @param week 曜日。
	 * @param off その休日を表すインスタンス。
	 * nullを指定すると、ダミーインスタンスを割り当てます。
	 */
	public void addOffEveryYearMonth(int seq, Week week, Off off) {
		for (int i = 1; i <= 12; i++) {
			addOffEveryYearMonth(i, seq, week, off);
		}
	}

	/**
	 * 毎年の、休日追加。n月の第n番目の曜日を休日にします。
	 * @param mm 月。1～12。
	 * @param seq 何番目か？1～。
	 * @param week 曜日。
	 * @param off その休日を表すインスタンス。
	 * nullを指定すると、ダミーインスタンスを割り当てます。
	 */
	public void addOffEveryYearMonth(int mm, int seq, Week week, Off off) {
		synchronized (all_week) {
			add(all_week, mm, seq, week, off);
		}
	}

	private void add(ArrayList<W> list, int mm, int seq, Week w, Off off) {
		list.add(new W(mm, seq, w, off));
	}

	/**
	 * 特定年の、休日追加。
	 * @param from_y 開始年。
	 * @param to_y 終了年。
	 * @param ajd 指定の日(月・日)を休日にします。
	 * @param off その休日を表すインスタンス。
	 * nullを指定すると、ダミーインスタンスを割り当てます。
	 */
	public void addOff(int from_y, int to_y, AJD ajd, Off off) {
		synchronized (one_year) {
			HashMap<MD, Off> map = null;
			for (Y<HashMap<MD, Off>> y : one_year) {
				map = y.get(from_y, to_y);
				if (map != null)
					break;
			}
			if (map == null) {
				map = new HashMap<MD, Off>();
				one_year.add(new Y<HashMap<MD, Off>>(from_y, to_y, map));
			}
			add(map, ajd, off);
		}
	}

	/**
	 * 特定年の、休日追加。
	 * @param from_y 開始年。
	 * @param to_y 終了年。
	 * @param from 指定の日(月・日)から休日にします。
	 * @param to 指定の日(月・日)までを休日にします。
	 * @param off その休日を表すインスタンス。
	 * nullを指定すると、ダミーインスタンスを割り当てます。
	 */
	public void addOff(int from_y, int to_y, AJD from, AJD to, Off off) {
		try {
			from = new AJD(2000, from.getMonth(), from.getDay());
			to = new AJD(2000, to.getMonth(), to.getDay());
		} catch (AJDException e) {
		}
		while (true) {
			addOff(from_y, to_y, from, off);
			if (from.getMonth() == to.getMonth() && from.getDay() == to.getDay())
				break;
			from = from.addDay(1);
		}
	}

	/**
	 * 特定年毎月の、休日追加。第n番目の曜日を休日にします。
	 * @param from_y 開始年。
	 * @param to_y 終了年。
	 * @param seq 何番目か？1～。
	 * @param week 曜日。
	 * @param off その休日を表すインスタンス。
	 * nullを指定すると、ダミーインスタンスを割り当てます。
	 */
	public void addOff(int from_y, int to_y, int seq, Week week, Off off) {
		for (int i = 1; i <= 12; i++) {
			addOffEveryMonth(from_y, to_y, i, seq, week, off);
		}
	}

	/**
	 * 特定年の、休日追加。n月の第n番目の曜日を休日にします。
	 * @param from_y 開始年。
	 * @param to_y 終了年。
	 * @param mm 月。1～12。
	 * @param seq 何番目か？1～。
	 * @param week 曜日。
	 * @param off その休日を表すインスタンス。
	 * nullを指定すると、ダミーインスタンスを割り当てます。
	 */
	public void addOffEveryMonth(int from_y, int to_y, int mm, int seq, Week week, Off off) {
		synchronized (one_week) {
			ArrayList<W> list = null;
			for (Y<ArrayList<W>> y : one_week) {
				list = y.get(from_y, to_y);
				if (list != null)
					break;
			}
			if (list == null) {
				list = new ArrayList<W>();
				one_week.add(new Y<ArrayList<W>>(from_y, to_y, list));
			}
			add(list, mm, seq, week, off);
		}
	}

	/**
	 * 休日判定。
	 * @param ajd 判定日。
	 * @return 休日を表すインスタンス。<br>
	 * addOff系メソッドで登録したものの他、
	 * {@link Week}(コンストラクタで指定した曜日)と{@link Holiday}を
	 * 返す場合があります。<br>
	 * 休日でなければ null です。
	 */
	public Off getOff(AJD ajd) {
		Off off = null;
		if (holi_f) {
			off = Holiday.getHoliday(ajd);
			if (off != null)
				return off;
		}
		synchronized (every_year) {
			off = getMD(every_year, ajd);
		}
		if (off != null)
			return off;
		synchronized (all_week) {
			off = getW(all_week, ajd);
		}
		if (off != null)
			return off;
		synchronized (one_year) {
			for (Y<HashMap<MD, Off>> y : one_year) {
				off = getMD(y.get(ajd.getYear()), ajd);
				if (off != null)
					return off;
			}
		}
		synchronized (one_week) {
			for (Y<ArrayList<W>> y : one_week) {
				off = getW(y.get(ajd.getYear()), ajd);
				if (off != null)
					return off;
			}
		}
		Week w = ajd.getWeek();
		for (Week d : every_week) {
			if (d == w)
				return d;
		}
		return null;
	}
}
