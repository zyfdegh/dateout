package edu.nuist.dateout.util;

import java.io.File;

import edu.nuist.dateout.value.VariableHolder;

/**
 * 用于生成图片 语音等文件名
 * @author Veayo
 *
 */
public class FileNameGenTool
{
    /**
     * 通过原来的图片文件生成新的文件名,格式如IMAGESENT_7a549979887800b508cf22d026dd6aa7.jpg
     * @param originalFile
     * @return
     */
    public String generateSentImageName(File absulutePathFile){
        String fileName=absulutePathFile.getName();
        //获取选取的图片后缀名
        if(fileName!=null&&fileName.length()>0){
            String fileSuffixName=fileName.substring(fileName.lastIndexOf('.'));
            //对当前时间作MD5
            return VariableHolder.FILE_PREFIX_IMAGE_SENT+new MD5Assit().getMD5(TimeAssit.getDate())+fileSuffixName;
        }else {
            return null;
        }
    }
    
    /**
     * 通过原来的图片文件生成新的文件名,格式如IMAGEHEAD_user3_7a549979887800b508cf22d026dd6aa7.jpg
     * @param originalFile
     * @return
     */
    public String generateHeadImageName(File absulutePathFile,String userId){
        String fileName=absulutePathFile.getName();
        //获取选取的图片后缀名
        if(fileName!=null&&fileName.length()>0){
            String fileSuffixName=fileName.substring(fileName.lastIndexOf('.'));
            //对当前时间作MD5
            return VariableHolder.FILE_PREFIX_IMAGE_HEAD+userId+"_"+new MD5Assit().getMD5(TimeAssit.getDate())+fileSuffixName;
        }else {
            return null;
        }
    }
    
    /**
     * 用于生成语音的新的文件名,格式如IMAGESENT_7a549979887800b508cf22d026dd6aa7.jpg
     * @param originalFile
     * @return
     */
    public String generateAudioName(){
        //根据录音的时间,再MD5,用作文件名
        return VariableHolder.FILE_PREFIX_AUDIO_SENT+new MD5Assit().getRandomMD5()+".amr";
    }
    
    /**
     * 用于生成用户资料编辑页面上部6张图片的文件名,格式如IMAGEVCARD_user1_7a549979887800b508cf22d026dd6aa7.jpg
     * @return
     */
    public String generateVCardImageName(File absulutePathFile,String userId){
        String fileName=absulutePathFile.getName();
        //获取选取的图片后缀名
        if(fileName!=null&&fileName.length()>0){
            String fileSuffixName=fileName.substring(fileName.lastIndexOf('.'));
            //对当前时间作MD5
            return VariableHolder.FILE_PREFIX_IMAGE_VCARD+userId+"_"+new MD5Assit().getMD5(TimeAssit.getDate())+fileSuffixName;
        }else {
            return null;
        }
    }
    
    /**
     *  用于生成游戏背景图片的文件名,格式如IMAGEGAME_user1_7a549979887800b508cf22d026dd6aa7.jpg
     * @param absulutePathFile
     * @return
     */
    public String generateGameImageName(File absulutePathFile,String userId){
        String fileName=absulutePathFile.getName();
        //获取选取的图片后缀名
        if(fileName!=null&&fileName.length()>0){
            String fileSuffixName=fileName.substring(fileName.lastIndexOf('.'));
            //对当前时间作MD5
            return VariableHolder.FILE_PREFIX_IMAGE_GAME+userId+"_"+new MD5Assit().getMD5(TimeAssit.getDate())+fileSuffixName;
        }else {
            return null;
        }
    }
}
