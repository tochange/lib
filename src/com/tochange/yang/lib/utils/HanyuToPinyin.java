package com.tochange.yang.lib.utils;

import com.tochange.yang.lib.log;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class HanyuToPinyin {
	private static HanyuPinyinOutputFormat mDefaultFormat = new HanyuPinyinOutputFormat();

	private static HanyuPinyinOutputFormat getInstance() {
		if (mDefaultFormat == null) {
			mDefaultFormat = new HanyuPinyinOutputFormat();
		}
		return mDefaultFormat;
	}
    //取出汉字的编码
	public static  int gbValue(char ch) {
        
        String str = new String();
        str += ch;
        try {
            byte[] bytes = str.getBytes("GB2312");
            if (bytes.length < 2)
                return 0;
            return (bytes[0] << 8 & 0xff00) + (bytes[1] &
                    0xff);
        } catch (Exception e) {
            return 0;
        }
    }
	public static String converterToFirstSpell(String chinese) {
		String pinyinName = "";
		try {
			char[] nameChar = chinese.toCharArray();
			getInstance().setCaseType(HanyuPinyinCaseType.UPPERCASE);
			getInstance().setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			for (int i = 0; i < nameChar.length; i++) {
				if (nameChar[i] > 128) {
					try {
						pinyinName += PinyinHelper.toHanyuPinyinStringArray(
								nameChar[i], getInstance())[0].charAt(0);
					} catch (BadHanyuPinyinOutputFormatCombination e) {
						log.e(e.toString());
					}
				} else {
					pinyinName += nameChar[i];
				}
			}
		} catch (Exception e) {
		    log.e(e.toString());
			pinyinName = "";
		}
		return pinyinName;
	}

	public static String converterToSpell(String chines) {
		String pinyinName = "";
		try {
			char[] nameChar = chines.toCharArray();
			getInstance().setCaseType(HanyuPinyinCaseType.UPPERCASE);
			getInstance().setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			for (int i = 0; i < nameChar.length; i++) {
				if (nameChar[i] > 128) {
					try {
						pinyinName += PinyinHelper.toHanyuPinyinStringArray(
								nameChar[i], getInstance())[0];
					} catch (BadHanyuPinyinOutputFormatCombination e) {
					    log.e(e.toString());
					}
				} else {
					pinyinName += nameChar[i];
				}
			}
		} catch (Exception e) {
		    log.e(e.toString());
			pinyinName = "";
		}
		return pinyinName;
	}
}
