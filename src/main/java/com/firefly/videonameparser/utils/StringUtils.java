package com.firefly.videonameparser.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;
import android.util.Log;

public class StringUtils {

	/**
	 * 检查字符串中是否包含中文字符
	 * @param str
	 * @return
	 */
	public static boolean   checkChina(String str) {  
		String exp="^[\u4E00-\u9FA5|\\！|\\,|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]$";  
		Pattern pattern=Pattern.compile(exp);  
		for (int i = 0; i < str.length(); i++) {// 遍历字符串每一个字符  
			char c = str.charAt(i);  
			Matcher matcher=pattern.matcher(c + "");  
			if(matcher.matches()) {  
				return true;
			}  
		}  
		return false;  
	}  

	private static String[] ChineseInterpunction = { "“", "”", "‘", "’", "。", "，", "；", "：", "？", "！", "……", "—", "～", "（", "）", "《", "》","【","】" };   
	private static String[] EnglishInterpunction = {"\"", "\"", "'", "'", ".", ",", ";", ":", "?", "!", "…", "-", "~", "(", ")", "<", ">","[","]" };   
	public static String  ChineseToEnglish(String str)   
	{   
		if(str == null || str.length() == 0) return str;
		for (int i = 0; i < ChineseInterpunction.length; i++)   
		{   
			str = str.replaceAll(ChineseInterpunction[i], EnglishInterpunction[i]);
		}  
		return str; 
	}

	public static boolean hasGB2312(String str) {
		for (int i = 0; i < str.length(); i++) {
			String bb = str.substring(i, i + 1);
			// 生成一个Pattern,同时编译一个正则表达式,其中的u4E00("一"的unicode编码)-\u9FA5("龥"的unicode编码)
			boolean cc = java.util.regex.Pattern.matches("[\u4E00-\u9FA5]", bb);
			if(cc)
				return true;
		}
		return false;
	}

	public static String getOnlyGB2312(String str){
		StringBuffer sb=new StringBuffer();
		for(char c:str.toCharArray()){
			if('\u4E00'<=c&&c<='\u9F45'){
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static boolean hasHttpUrl(String str) {
		if(TextUtils.isEmpty(str)) return false;
		//把中文替换为#  
		str = str.replaceAll("[\u4E00-\u9FA5]", "#");  
		System.out.println(str);  
		String url[]=str.split("#");  
		//转换为小写  
		if(url!=null&&url.length>0){  
			for(String tempurl:url){   
				tempurl = tempurl.toLowerCase();  
				String regex = "^((https|http|ftp|rtsp|mms)?://)"    
						+ "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@    
						+ "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184    
						+ "|" // 允许IP和DOMAIN（域名）   
						+ "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.    
						+ "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名    
						+ "[a-z]{2,6})" // first level domain- .com or .museum    
						+ "(:[0-9]{1,4})?" // 端口- :80    
						+ "((/?)|" // a slash isn't required if there is no file name    
						+ "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";   

				Pattern p = Pattern.compile(regex);  
				Matcher matcher = p.matcher(tempurl);  
				if(matcher.find())
					return true;
			}  
		}  
		return false;
	}




	public static boolean contain(String[] array,String str)
	{
		if(array == null || array.length ==0) return false;
		List<String> list=Arrays.asList(array);
		return list.contains(str);
	}

	public static boolean matchListFind(String[] regexs,String input){

		for (String regex : regexs) {
			if(matchFind(regex,input))
			{
				Log.v("sjfqq","regex:"+regex);
				return true;
			}
		}
		return false;
	}


	public static boolean matchFind(String regex,String input){
		Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(input);
		return m.find();
	}

	public static boolean matchFindStrictMode(String regex,String input){
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input);
		return m.find();
	}


	public static  String[] matcher(String regex,String input) {
		return matcher(regex,Pattern.CASE_INSENSITIVE, input);
	}

	public static  String[] matcher(String regex,int flag,String input) {
		Pattern pattern = Pattern.compile(regex,flag);
		Matcher matcher = pattern.matcher(input);
		List<String> list = new ArrayList<String>();
		while (matcher.find()) {
			for (int i = 0; i <= matcher.groupCount(); i++) {
				list.add(matcher.group(i));
			}

		}

		return list.toArray(new String[0]);
	}

	public static  String[] matcher2(String regex,String input) {
		return matcher2(regex,Pattern.CASE_INSENSITIVE, input);
	}
	 
	public static  String[] matcher2(String regex,int flag,String input) {
		Pattern pattern = Pattern.compile(regex,flag);
		Matcher matcher = pattern.matcher(input);
		List<String> list = new ArrayList<String>();
		while (matcher.find()) {
			Log.v("sjfqq","matcher.groupCount():"+matcher.groupCount()+","+matcher.group());
			if(matcher.groupCount() > 0)
			{
				list.add(matcher.group(0));
			}
		}

		return list.toArray(new String[0]);
	}

	/**
	 * 中文数字转阿拉伯数字
	 * (长度不能超过long最大值)
	 * @param chNum 中文数字
	 * @return 阿拉伯数字
	 */
	public static long ch2Num(String chNum) {
		int[] numLen = {16, 8, 4, 3, 2, 1};//对应下面单位后面多少个零

		String[] dw = {"兆", "亿", "万", "千", "百", "十"}; //中文单位

		String[] dw1 = {"兆", "亿", "萬", "仟", "佰", "拾"}; //中文单位另一版

		String[] sz = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"}; //中文数字

		String[] sz1 = {"〇", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖", "拾"}; //中文数字另一版

		if (chNum == null) return 0;//空对象返回0
		for (int i = 0; i < sz.length; i++) { //统一文字版本
			if (i < dw.length)
				chNum = chNum.replaceAll(dw1[i], dw[i]);
			chNum = chNum.replaceAll(sz1[i], sz[i]);
		}
		chNum = chNum.replaceAll("(百.)\\b", "$1十"); //正则替换为了匹配中文类似二百五这样的词
		if (chNum.length() == 1) {
			for (int i = 0; i < sz.length; i++) {
				if (chNum.equals(sz[i])) return i;
			}
			return 0; //中文数字没有这个字
		}
		chNum = strReverse(chNum); //调转输入的字符串
		for (int i = 0; i < dw.length; i++) {
			if (chNum.contains(dw[i])) {
				String part[] = chNum.split(dw[i], 2); //把字符串分割2部分
				long num1 = ch2Num(strReverse(part[1]));
				long num2 = ch2Num(strReverse(part[0]));
				return (long) ((num1 == 0 ? 1 : num1) * Math.pow(10, numLen[i]) + num2);
			}
		}
		char[] c = chNum.toCharArray();
		long sum = 0;
		for (int i = 0; i < c.length; i++) { //一个个解析数字
			String tem = String.valueOf(c[i]); //根据索引转成对应数字
			sum += ch2Num(tem) * Math.pow(10, i);//根据位置给定数字
		}
		return sum;
	}
	//字符串掉转
	private static String strReverse(String str) {
		return new StringBuilder(str).reverse().toString();
	}

	public static String removeAll(String str, ArrayList<String> removeWords){
		if(TextUtils.isEmpty(str) || removeWords == null || removeWords.size() == 0) return str;
		for (String string : removeWords) {
			str = str.replace( string,"");
		}
		return str;

	}

	public static String deleteSubString(String str,String sub_str){
		if(TextUtils.isEmpty(str) || TextUtils.isEmpty(sub_str)) return str;

		int index = str.indexOf(sub_str);
		if(index > -1)
		{
			//	str.
		}

		return str;

	}
	/**
	 * 转义正则特殊字符 （$()*+.[]?\^{},|）
	 * 
	 * @param keyword
	 * @return
	 */
	public static String escapeExprSpecialWord(String keyword) {
		if (!TextUtils.isEmpty(keyword)) {
			String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
			for (String key : fbsArr) {
				if (keyword.contains(key)) {
					keyword = keyword.replace(key, "\\" + key);
				}
			}
		}
		return keyword;
	}

	public static void debug(String[] matches){
		Log.v("sjfqq","▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽");
		for (String string : matches) {
			Log.v("sjfqq","debug string："+string);
		}
		Log.v("sjfqq","△△△△△△△△△△△△△△△△");
	}

}
