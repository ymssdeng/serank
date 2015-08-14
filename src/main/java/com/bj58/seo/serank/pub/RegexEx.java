package com.bj58.seo.serank.pub;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

public class RegexEx {

	public String urlEncode(String value) {
		try {
			return URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("参数编码错误：" + value);
		}
	}

	public List<String> matchValues(String content, String regex) {
		Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(content);

		List<String> list = new ArrayList<String>();

		while (matcher.find()) {
			list.add(matcher.group());
		}

		return list;
	}

	public String matchValue_n(String content, String regex, int group) {
		Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(content);

		if (matcher.find()) {
			return matcher.group(group);
		}

		return "";
	}

	public String matchValue_1(String content, String regex) {
		return this.matchValue_n(content, regex, 1);
	}
	
	public String matchValue_NonEmpty(String content, String regex) {
        Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(content);

        while (matcher.find()) {
            String g= matcher.group();
            if (!Strings.isNullOrEmpty(g)) {
                return g;
            }
        }

        return "";
	}

	public String removeHtml(String html) {
		String content = html;

		String[][] replacements = new String[][] {
		        // 去除HTML模式
		        new String[] { "&(amp|#38);", "&" }, // 去掉&
		        new String[] { "&(lt|#60);", "<" }, // 去掉<
		        new String[] { "&(gt|#62);", ">" }, // 去掉>
		        new String[] { "&(nbsp|#160);", "" }, // 去掉空格
		        new String[] { "&(quot|#34);", "\"" }, // 去掉"
		        new String[] { "&(iexcl|#161);", "\\xa1" }, // 去掉¡
		        new String[] { "&(cent|#162);", "\\xa2" }, // 去掉¢
		        new String[] { "&(pound|#163);", "\\xa3" }, // 去掉£
		        new String[] { "&(copy|#169);", "\\xa9" }, // 去掉©
		        new String[] { "(\\s*(\r\n)\\s*)+", "\r\n" }, // 将多个回车换行整理为1个
		        new String[] { "[\r\n\t]", "#nowrap#" }, // 去除回车、换行、制表符
		        new String[] { "<style[^>]*?>.*?</style>", "" }, // 去除CSS
		        new String[] { "<!--.*?-->", "" }, // 去除注释
		        new String[] { "<([^>]*?)>", "" }, // 去除Html标签代码，<([^>]*?)+>存在引起CPU100%问题，修改为<([^>]*?)>
		};

		for (String[] rep : replacements) {
			Matcher matcher = Pattern.compile(rep[0]).matcher(content);
			content = matcher.replaceAll(rep[1]);
		}

		return content;
	}

}
