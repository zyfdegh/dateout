package edu.nuist.dateout.util;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.VCardProvider;

import android.util.Log;
import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.model.CustomVcard;
import edu.nuist.dateout.value.VariableHolder;

/**
 * 用于处理VCard的上传与下载,获取与设定指定域
 * 
 * @author Veayo
 *
 */
public class VCardAssit
{
    
    private XMPPConnection connection;
    
    public VCardAssit(XMPPConnection connection)
    {
        super();
        this.connection = connection;
    }
    
    /**
     * 加载指定用户的VCard,同时赋值类中的成员变量vCard,从服务端获取VCard之前需要先调用该方法
     * 
     * @param userId 用户名
     * @return VCard对象,加载失败返回null
     */
    public VCard loadVCard(String userId)
    {
        VCard vCard = new VCard();
        ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
        try
        {
            vCard.load(connection, userId + "@" + DateoutApp.getInstance().getServiceName());
            Log.v("Dateout", "VCardAssit==>" + "加载用户" + userId + "VCard成功");
            return vCard;
        }
        catch (XMPPException e)
        {
            Log.v("Dateout", "VCardAssit==>" + "加载用户" + userId + "VCard失败");
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 用于将服务端的VCard解析并转换为我自定义的MyVCard类
     * 
     * @param vCard 服务端标准的VCard类
     * @return 自定义MyVCard类
     */
    public CustomVcard convertToMyVCard(VCard vCard)
    {
        if (vCard != null)
        {
            CustomVcard myCard = new CustomVcard();
            myCard.setNickName(checkValue(vCard.getField(VariableHolder.VCARD_FIELD_NICKNM)));
            myCard.setEmail(checkValue(vCard.getField(VariableHolder.VCARD_FIELD_EMAIL)));
            myCard.setBirthDay(checkValue(vCard.getField(VariableHolder.VCARD_FIELD_BIRTHDAY)));
            myCard.setBirthPlace(checkValue(vCard.getField(VariableHolder.VCARD_FIELD_CITY)));
            myCard.setGender(checkValue(vCard.getField(VariableHolder.VCARD_FIELD_GENDER)));
            myCard.setHeight(checkValue(vCard.getField(VariableHolder.VCARD_FIELD_HEIGHT)));
            myCard.setInterest(checkValue(vCard.getField(VariableHolder.VCARD_FIELD_INTEREST)));
            myCard.setJob(checkValue(vCard.getField(VariableHolder.VCARD_FIELD_JOB)));
            myCard.setMoodie(checkValue(vCard.getField(VariableHolder.VCARD_FIELD_MOODIE)));
            myCard.setSingleState(checkValue(vCard.getField(VariableHolder.VCARD_FIELD_SINGLESTAT)));
            myCard.setWeight(checkValue(vCard.getField(VariableHolder.VCARD_FIELD_WEIGHT)));
            return myCard;
        }
        else
        {
            return null;
        }
    }
    
    public VCard convertToVCard(CustomVcard myCard)
    {
        if (myCard != null)
        {
            VCard vCard = new VCard();
            vCard.setField(VariableHolder.VCARD_FIELD_BIRTHDAY, checkValue(myCard.getBirthDay()), true);
            vCard.setField(VariableHolder.VCARD_FIELD_CITY, checkValue(myCard.getBirthPlace()), true);
            vCard.setField(VariableHolder.VCARD_FIELD_EMAIL, checkValue(myCard.getEmail()), true);
            vCard.setField(VariableHolder.VCARD_FIELD_GENDER, checkValue(myCard.getGender()), true);
            vCard.setField(VariableHolder.VCARD_FIELD_HEIGHT, checkValue(myCard.getHeight()), true);
            vCard.setField(VariableHolder.VCARD_FIELD_INTEREST, checkValue(myCard.getInterest()), true);
            vCard.setField(VariableHolder.VCARD_FIELD_JOB, checkValue(myCard.getJob()), true);
            vCard.setField(VariableHolder.VCARD_FIELD_MOODIE, checkValue(myCard.getMoodie()), true);
            vCard.setField(VariableHolder.VCARD_FIELD_NICKNM, checkValue(myCard.getNickName()), true);
            vCard.setField(VariableHolder.VCARD_FIELD_SINGLESTAT, checkValue(myCard.getSingleState()), true);
            vCard.setField(VariableHolder.VCARD_FIELD_WEIGHT, checkValue(myCard.getWeight()), true);
            return vCard;
        }
        else
        {
            return null;
        }
    }
    
    /**
     * 指定用户Id,从服务器下载VCard并转换为MyVCard
     * 
     * @param userId 用户Id
     * @return MyVCard类型
     */
    public CustomVcard loadMyVCard(String userId)
    {
        // 下载VCard
        VCard vCard = loadVCard(userId);
        if (vCard != null)
        {
            return convertToMyVCard(vCard);// 转换为MyVCard并返回
        }
        else
        {
            return null;
        }
    }
    
    public boolean uploadMyVCard(CustomVcard myCard)
    {
        if (myCard == null)
        {
            return false;
        }
        else
        {
            // 转换为VCard
            VCard vCard = convertToVCard(myCard);
            try
            {
                // 上传VCard
                vCard.save(connection);
                Log.v("Dateout", "VcardInfoActivity==>" + "上传用户资料成功");
                return true;
            }
            catch (XMPPException e)
            {
                e.printStackTrace();
                Log.v("Dateout", "VcardInfoActivity==>" + "上传用户资料失败");
                return false;
            }
        }
    }
    
    /**
     * 用于检测字符串是否为null,如果是,返回"",如果不是,返回该字符
     * 
     * @param str
     * @return
     */
    private String checkValue(String str)
    {
        return str == null ? "" : str;
    }
}
