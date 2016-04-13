package edu.nuist.dateout.util;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import edu.nuist.dateout.model.GameConfig;
import edu.nuist.dateout.model.UserNear;
import edu.nuist.dateout.model.UserRandom;

/**
 * 用于转换对象为JSON数据
 * 
 * @author Veayo
 *
 */
public class JsonAssit
{
    /**
     * 用于将UserNearMe对象的内容转换为Json字符串
     * 
     * @return JSON可以解析的字符串
     */
    public String userNearList2JsonStr(List<UserNear> usersNearList)
    {
        JSONObject jUsersPack =  new JSONObject();
        JSONArray userArray = new JSONArray();
        
        for (int i = 0; i < usersNearList.size(); i++)
        {
            JSONObject jUser = new JSONObject();
            jUser.put("user_id", usersNearList.get(i).getUserId());
            jUser.put("loc_lnt", usersNearList.get(i).getLocJingdu());
            jUser.put("loc_lat", usersNearList.get(i).getLocWeidu());
            jUser.put("distance", usersNearList.get(i).getDistance());
            jUser.put("loc_time",usersNearList.get(i).getTime().toString());
            userArray.add(jUser);
        }
        jUsersPack.put("UsersNear", userArray);
        return jUsersPack.toString();
    }
    
    public String gameConfig2JsonStr(GameConfig config)
    {
        if (config==null)
        {
            return "";
        }else {
            JSONObject jConfig = new JSONObject();
            jConfig.put("userId", config.getUserId());
            jConfig.put("picUrl", config.getPicUrl());
            jConfig.put("timeOut", config.getTimeOut());
            jConfig.put("difficulty", config.getDifficulty());
            return jConfig.toString();
        }
    }
    
    public String userRandomList2JsonStr(List<UserRandom> userRandomList){
        JSONObject jRandomUsersPack =  new JSONObject();
        JSONArray userArray = new JSONArray();
        
        for (int i = 0; i < userRandomList.size(); i++)
        {
            JSONObject jUser = new JSONObject();
            jUser.put("user_id", userRandomList.get(i).getUserId());
            jUser.put("distance", userRandomList.get(i).getDistance());
            jUser.put("head_url",userRandomList.get(i).getHeadUrl());
            jUser.put("online_stat",userRandomList.get(i).getOnlineStat());
            
            userArray.add(jUser);
        }
        jRandomUsersPack.put("UsersRandom", userArray);
        return jRandomUsersPack.toString();
    }
}
