/*
 * AJD4JP
 * Copyright (c) 2011-2018  Akira Terasaki
 * このファイルは同梱されているLicense.txtに定めた条件に
 * 同意できる場合にのみ利用可能です。
 */
package ajd4jp;

/**
 * 日時範囲。
 */
public class AJDRange {
	/**
	 * 日時範囲の関係性。<br>
<style>
#ajdinfo th,#ajdinfo td{border-top : 1px solid;border-bottom : 1px solid;}
</style>
<table border="0" style="border-collapse : collapse;empty-cells : show;">
  <caption>this と target(赤着色範囲) の関係</caption>
  <tbody id="ajdinfo">
    <tr>
      <th style="border: 1px solid black;"></th>
      <th></th>
      <th></th>
      <th align="center" colspan="4" style="background-color: #ffff00;border-left: dotted 1px black;border-right: dotted 1px black;">this</th>
      <th></th>
      <th></th>
      <th style="border: 1px solid black;">&nbsp; isBorder()&nbsp; </th>
      <th style="border: 1px solid black;">&nbsp; isOverlap()&nbsp; </th>
      <th style="border: 1px solid black;">&nbsp; isEquals()&nbsp; </th>
    </tr>
    <tr>
      <th style="border: 1px solid black;"></th>
      <td>&nbsp; &nbsp; </td>
      <td>&nbsp; &nbsp; </td>
      <td style="border-left: dotted 1px black;">&nbsp; &nbsp; </td>
      <td>&nbsp; &nbsp; </td>
      <td>&nbsp; &nbsp; </td>
      <td style="border-right: dotted 1px black;">&nbsp; &nbsp; </td>
      <td>&nbsp; &nbsp; </td>
      <td>&nbsp; &nbsp; </td>
      <td align="center" style="border: 1px solid black;"></td>
      <td align="center" style="border: 1px solid black;"></td>
      <td align="center" style="border: 1px solid black;"></td>
    </tr>
    <tr>
      <th style="border: 1px solid black;">LESS_OUTER</th>
      <td style="background-color: #ff0000;border: 1px solid black;"></td>
      <td></td>
      <td style="border-left: dotted 1px black;"></td>
      <td></td>
      <td></td>
      <td style="border-right: dotted 1px black;"></td>
      <td></td>
      <td></td>
      <td align="center" style="border: 1px solid black;"></td>
      <td align="center" style="border: 1px solid black;"></td>
      <td align="center" style="border: 1px solid black;"></td>
    </tr>
    <tr>
      <th style="border: 1px solid black;">LESS_BORDER</th>
      <td colspan="2" style="background-color: #ff0000;border: 1px solid black;"></td>
      <td></td>
      <td></td>
      <td></td>
      <td style="border-right: dotted 1px black;"></td>
      <td></td>
      <td></td>
      <td align="center" style="border: 1px solid black;">true</td>
      <td align="center" style="border: 1px solid black;"></td>
      <td align="center" style="border: 1px solid black;"></td>
    </tr>
    <tr>
      <th style="border: 1px solid black;">LESS_OVER</th>
      <td colspan="4" style="background-color: #ff0000;border: 1px solid black;"></td>
      <td></td>
      <td style="border-right: dotted 1px black;"></td>
      <td></td>
      <td></td>
      <td align="center" style="border: 1px solid black;"></td>
      <td align="center" style="border: 1px solid black;">true</td>
      <td align="center" style="border: 1px solid black;"></td>
    </tr>
    <tr>
      <th style="border: 1px solid black;">LESS_EQUALS</th>
      <td colspan="6" style="background-color: #ff0000;border: 1px solid black;"></td>
      <td></td>
      <td></td>
      <td align="center" style="border: 1px solid black;"></td>
      <td align="center" style="border: 1px solid black;">true</td>
      <td align="center" style="border: 1px solid black;">true</td>
    </tr>
    <tr>
      <th style="border: 1px solid black;">EQUALS</th>
      <td></td>
      <td></td>
      <td colspan="4" style="background-color: #ff0000;border: 1px solid black;"></td>
      <td></td>
      <td></td>
      <td align="center" style="border: 1px solid black;"></td>
      <td align="center" style="border: 1px solid black;">true</td>
      <td align="center" style="border: 1px solid black;">true</td>
    </tr>
    <tr>
      <th style="border: 1px solid black;">LESS_INNER</th>
      <td></td>
      <td></td>
      <td colspan="3" style="background-color: #ff0000;border: 1px solid black;"></td>
      <td style="border-right: dotted 1px black;"></td>
      <td></td>
      <td></td>
      <td align="center" style="border: 1px solid black;"></td>
      <td align="center" style="border: 1px solid black;">true</td>
      <td align="center" style="border: 1px solid black;">true</td>
    </tr>
    <tr>
      <th style="border: 1px solid black;">INNER</th>
      <td></td>
      <td></td>
      <td style="border-left: dotted 1px black;"></td>
      <td style="background-color: #ff0000;border: 1px solid black;" colspan="2"></td>
      <td style="border-right: dotted 1px black;"></td>
      <td></td>
      <td></td>
      <td align="center" style="border: 1px solid black;"></td>
      <td align="center" style="border: 1px solid black;">true</td>
      <td align="center" style="border: 1px solid black;"></td>
    </tr>
    <tr>
      <th style="border: 1px solid black;">GREATER_INNER</th>
      <td></td>
      <td></td>
      <td style="border-left: dotted 1px black;"></td>
      <td style="background-color: #ff0000;border: 1px solid black;" colspan="3"></td>
      <td></td>
      <td></td>
      <td align="center" style="border: 1px solid black;"></td>
      <td align="center" style="border: 1px solid black;">true</td>
      <td align="center" style="border: 1px solid black;">true</td>
    </tr>
    <tr>
      <th style="border: 1px solid black;">OVER</th>
      <td colspan="8" style="background-color: #ff0000;border: 1px solid black;"></td>
      <td align="center" style="border: 1px solid black;"></td>
      <td align="center" style="border: 1px solid black;">true</td>
      <td align="center" style="border: 1px solid black;"></td>
    </tr>
    <tr>
      <th style="border: 1px solid black;">GREATER_EQUALS</th>
      <td></td>
      <td></td>
      <td colspan="6" style="background-color: #ff0000;border: 1px solid black;"></td>
      <td align="center" style="border: 1px solid black;"></td>
      <td align="center" style="border: 1px solid black;">true</td>
      <td align="center" style="border: 1px solid black;">true</td>
    </tr>
    <tr>
      <th style="border: 1px solid black;">GREATER_OVER</th>
      <td></td>
      <td></td>
      <td style="border-left: dotted 1px black;"></td>
      <td></td>
      <td colspan="4" style="background-color: #ff0000;border: 1px solid black;"></td>
      <td align="center" style="border: 1px solid black;"></td>
      <td align="center" style="border: 1px solid black;">true</td>
      <td align="center" style="border: 1px solid black;"></td>
    </tr>
    <tr>
      <th style="border: 1px solid black;">GREATER_BORDER</th>
      <td></td>
      <td></td>
      <td style="border-left: dotted 1px black;"></td>
      <td></td>
      <td></td>
      <td></td>
      <td colspan="2" style="background-color: #ff0000;border: 1px solid black;"></td>
      <td align="center" style="border: 1px solid black;">true</td>
      <td align="center" style="border: 1px solid black;"></td>
      <td align="center" style="border: 1px solid black;"></td>
    </tr>
    <tr>
      <th style="border: 1px solid black;">GREATER_OUTER</th>
      <td></td>
      <td></td>
      <td style="border-left: dotted 1px black;"></td>
      <td></td>
      <td></td>
      <td style="border-right: dotted 1px black;"></td>
      <td></td>
      <td style="background-color: #ff0000;border: 1px solid black;"></td>
      <td align="center" style="border: 1px solid black;"></td>
      <td align="center" style="border: 1px solid black;"></td>
      <td align="center" style="border: 1px solid black;"></td>
    </tr>
  </tbody>
</table>
	 */
	public enum Relationship {
		/** 過去に外側 */
		LESS_OUTER(false, false, false),
		/** 過去で接する */
		LESS_BORDER(true, false, false),
		/** 過去と一部重複 */
		LESS_OVER(false, true, false),
		/** 過去と全て重複 */
		LESS_EQUALS(false, true, true),
		/** 完全一致 */
		EQUALS(false, true, true),
		/** 過去が一致する内側 */
		LESS_INNER(false, true, true),
		/** 内側 */
		INNER(false, true, false),
		/** 未来が一致する内側 */
		GREATER_INNER(false, true, true),
		/** 外側から内包 */
		OVER(false, true, false),
		/** 未来と全て重複 */
		GREATER_EQUALS(false, true, true),
		/** 未来と一部重複 */
		GREATER_OVER(false, true, false),
		/** 未来で接する */
		GREATER_BORDER(true, false, false),
		/** 未来に外側 */
		GREATER_OUTER(false, false, false);

		private boolean bd, ov, eq;
		private Relationship(boolean b, boolean o, boolean e) {
			bd = b;
			ov = o;
			eq = e;
		}
		/**
		 * 接しているか？
		 * @return trueなら接しており、重なりがない。
		 */
		public boolean isBorder() { return bd; }
		/**
		 * 重なりがあるか？
		 * @return trueなら重なり部分がある。
		 */
		public boolean isOverlap() { return ov; }
		/**
		 * 開始終了が一致するか？
		 * @return trueなら開始終了の何れか、または全部が一致する。
		 */
		public boolean isEquals() { return eq; }
	}
	private AJD start, end;
	/**
	 * コンストラクタ。
	 * @param a 開始日時または終了日時。
	 * @param b 開始日時または終了日時。
	 */
	public AJDRange(AJD a, AJD b) {
		if (a.compareTo(b) < 0) {
			start = a;
			end = b;
		}
		else {
			start = b;
			end = a;
		}
	}
	public AJD getFrom() { return start; }
	public AJD getTo() { return end; }
	/**
	 * 範囲の比較。
	 * @param target 比較対象。
	 * @return 比較の判定結果。
	 */
	public Relationship compare(AJDRange target) {
		if (this.start.equals(target.start) && this.end.equals(target.end)) { return Relationship.EQUALS; }
		if (this.start.equals(target.start)) {
			if (this.end.compareTo(target.end) < 0) { return Relationship.GREATER_EQUALS; }
			return Relationship.LESS_INNER;
		}
		if (this.end.equals(target.end)) {
			if (this.start.compareTo(target.start) < 0) { return Relationship.GREATER_INNER; }
			return Relationship.LESS_EQUALS;
		}
		if (this.start.equals(target.end)) { return Relationship.LESS_BORDER; }
		if (this.end.equals(target.start)) { return Relationship.GREATER_BORDER; }
		if (this.start.compareTo(target.start) < 0 && target.end.compareTo(this.end) < 0) { return Relationship.INNER; }
		if (this.start.compareTo(target.start) > 0 && target.end.compareTo(this.end) > 0) { return Relationship.OVER; }
		if (this.start.compareTo(target.start) > 0) {
			if (this.start.compareTo(target.end) > 0) { return Relationship.LESS_OUTER; }
			return Relationship.LESS_OVER;
		}
		if (this.end.compareTo(target.start) < 0) { return Relationship.GREATER_OUTER; }
		return Relationship.GREATER_OVER;
	}

	public String toString() {
		return start.toString() + " - " + end.toString();
	}
}

