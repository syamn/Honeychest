package syam.Honeychest.util;

import java.util.Collection;
import java.util.Iterator;

public class Util {

	/**
	 * 文字列が整数型に変換できるか返す
	 * @param str チェックする文字列
	 * @return 変換成功ならtrue、失敗ならfalse
	 */
	public static boolean isInteger(String str) {
		try{
			Integer.parseInt(str);
		}catch (NumberFormatException e){
			return false;
		}
		return true;
	}

	/**
	 * 文字列がdouble型に変換できるか返す
	 * @param str チェックする文字列
	 * @return 変換成功ならtrue、失敗ならfalse
	 */
	public static boolean isDouble(String str) {
		try{
			Double.parseDouble(str);
		}catch (NumberFormatException e){
			return false;
		}
		return true;
	}

	/**
	 * PHPの join(array, delimiter) と同じ関数
	 * @param s 結合するコレクション
	 * @param delimiter デリミタ文字
	 * @return 結合後の文字列
	 */
	public static String join(Collection<?> s, String delimiter) {
		StringBuffer buffer = new StringBuffer();
		Iterator<?> iter = s.iterator();

		// 要素が無くなるまでループ
		while (iter.hasNext()){
			buffer.append(iter.next());
			// 次の要素があればデリミタを挟む
			if (iter.hasNext()){
				buffer.append(delimiter);
			}
		}
		// バッファ文字列を返す
		return buffer.toString();
	}

	/**
	 * ファイル名から拡張子を返します。
	 * @param fileName ファイル名
	 * @return ファイルの拡張子
	 */
	public static String getSuffix(String fileName) {
	    if (fileName == null)
	        return null;
	    int point = fileName.lastIndexOf(".");
	    if (point != -1) {
	        return fileName.substring(point + 1);
	    }
	    return fileName;
	}
}
