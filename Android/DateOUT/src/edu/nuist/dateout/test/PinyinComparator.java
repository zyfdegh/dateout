package edu.nuist.dateout.test;

import java.util.Comparator;
import edu.nuist.dateout.model.FriendItem;
/**
 * 首字母字母比较器
 */
public class PinyinComparator implements Comparator<FriendItem> {

	public int compare(FriendItem o1, FriendItem o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
