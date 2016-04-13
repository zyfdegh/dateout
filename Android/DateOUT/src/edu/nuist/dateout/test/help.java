package edu.nuist.dateout.test;

import java.util.ArrayList;
import java.util.List;

import edu.nuist.dateout.model.FriendItem;
import edu.nuist.dateout.test.CharacterParser;

public class help {

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	public help() {
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
	}
	public List<FriendItem> filledData(List<FriendItem> list) {
		List<FriendItem> mSortList = new ArrayList<FriendItem>();

		for (int i = 0; i < list.size(); i++) {
			FriendItem sortModel = list.get(i);
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(list.get(i).getRemarkName());
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		
		return mSortList;
	}
}
