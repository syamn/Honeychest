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

	/**
	 * 指定されたバージョンが、基準より新しいバージョンかどうかを確認する<br>
	 * 完全一致した場合もtrueになることに注意。
	 * @param version 確認するバージョン
	 * @param border 基準のバージョン
	 * @return 基準より確認対象の方が新しいバージョンかどうか
	 */
	public static boolean isUpperVersion(String version, String border) {

		String[] versionArray = version.split("\\.");
		int[] versionNumbers = new int[versionArray.length];
		for ( int i=0; i<versionArray.length; i++ ) {
			if ( !versionArray[i].matches("[0-9]+") )
				return false;
			versionNumbers[i] = Integer.parseInt(versionArray[i]);
		}

		String[] borderArray = border.split("\\.");
		int[] borderNumbers = new int[borderArray.length];
		for ( int i=0; i<borderArray.length; i++ ) {
			if ( !borderArray[i].matches("[0-9]+") )
				return false;
			borderNumbers[i] = Integer.parseInt(borderArray[i]);
		}

		int index = 0;
		while ( (versionNumbers.length > index) && (borderNumbers.length > index) ) {
			if ( versionNumbers[index] > borderNumbers[index] ) {
				return true;
			} else if ( versionNumbers[index] < borderNumbers[index] ) {
				return false;
			}
			index++;
		}
		if ( borderNumbers.length == index ) {
			return true;
		} else {
			return false;
		}
	}
}
