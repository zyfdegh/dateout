package edu.nuist.dateout.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexAssit {
	/**
	 * 判断给定的字符串是否为合法IP
	 * @param ipToCheck 要检测的字符串
	 * @return true表示IP有效
	 */
	public boolean checkIp(String str)
	{
        String regex = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}"; 
        Pattern pattern = Pattern.compile(regex); 
        Matcher matcher = pattern.matcher(str); 
        return matcher.matches(); 
	}

	/**
	 * 用于判断给定的用户ID是否符合特定规则
	 * 用户名规则,数字0-9,大小写字母,下划线
	 * @param userIdToCheck 要检测的字符串
	 * @return true表示用户名有效
	 */
	//TODO 不要让用户ID带有'_',不然处理字符串分割会遇到麻烦
	public boolean checkUserId(String str){
		String regex="[0-9a-zA-Z\\|_]+";
		Pattern pattern = Pattern.compile(regex); 
        Matcher matcher = pattern.matcher(str); 
        return matcher.matches(); 
	}
	
	public boolean checkPwd(String str){
//	    String regex="[0-9a-zA-Z\\|_]+";
	    String regex=".{6,20}";
        Pattern pattern = Pattern.compile(regex); 
        Matcher matcher = pattern.matcher(str); 
        return matcher.matches();
	}
}
