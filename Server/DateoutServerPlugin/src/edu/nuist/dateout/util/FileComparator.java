package edu.nuist.dateout.util;

import java.io.File;
import java.util.Comparator;

/**
 * 用于排序文件,根据文件最后修改时间进行降序排序(最新的在最前面)
 * @author Veayo
 *
 * @param <T> String
 */
public class FileComparator<T> implements Comparator<T> {
    public int compare(T o1, T o2) {
        File file1=new File((String)o1);
        File file2=new File((String)o2);
        return (int)(file2.lastModified()-file1.lastModified());
    }
}
