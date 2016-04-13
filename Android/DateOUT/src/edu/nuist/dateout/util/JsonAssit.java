package edu.nuist.dateout.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import edu.nuist.dateout.model.GameConfig;
import edu.nuist.dateout.model.UserNear;
import edu.nuist.dateout.model.UserRandom;

public class JsonAssit
{
    /**
     * 用于将Json字符串转换为附近的人列表
     * 
     * @param jsonStr Json形式的字符串
     * @return 返回UserNear对象列表
     */
    public List<UserNear> jsonStr2UserNearList(String jsonStr)
    {
        List<UserNear> usersNearList = new ArrayList<UserNear>();
        try
        {
            JSONArray jUsersNearArr = new JSONObject(jsonStr).getJSONArray("UsersNear");
            for (int i = 0; i < jUsersNearArr.length(); i++)
            {
                JSONObject jUserNear = jUsersNearArr.getJSONObject(i);
                UserNear userNear = new UserNear();
                userNear.setUserId(jUserNear.getString("user_id"));
                userNear.setLocJingdu(jUserNear.getDouble("loc_lnt"));
                userNear.setLocWeidu(jUserNear.getDouble("loc_lat"));
                userNear.setDistance(jUserNear.getDouble("distance"));
                userNear.setTime(Timestamp.valueOf(jUserNear.getString("loc_time")));
                usersNearList.add(userNear);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return usersNearList;
    }
    
    /**
     * 解析Json字符串到UserRandom列表中
     * @param jsonStr 待解析的字符串
     * @return 返回UserRandom列表
     */
    public List<UserRandom> jsonStr2UsersRandomList(String jsonStr)
    {
        List<UserRandom> userRandomList = new ArrayList<UserRandom>();
        try
        {
            JSONArray jUsersRandomArr = new JSONObject(jsonStr).getJSONArray("UsersRandom");
            for (int i = 0; i < jUsersRandomArr.length(); i++)
            {
                JSONObject jUserRandom = jUsersRandomArr.getJSONObject(i);
                UserRandom userRandom = new UserRandom();
                userRandom.setUserId(jUserRandom.getString("user_id"));
                userRandom.setOnlineStat((short)jUserRandom.getInt("online_stat"));
                userRandom.setDistance(jUserRandom.getDouble("distance"));
                userRandom.setHeadUrl(jUserRandom.getString("head_url"));
                userRandomList.add(userRandom);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return userRandomList;
    }
    
    /**
     * 用Gson解析游戏配置Json字符串并返回一个GameConfig对象 Gson为Google提供的Json解析器,*注意:使用Gson解析要求类中成员变量名和Json字符串中的字段名完全一致
     * 
     * @param jsonStr 游戏配置字符串
     * @return 返回一个游戏配置GameConfig对象
     */
    public GameConfig jsonStr2GameConfig(String jsonStr)
    {
        Gson gson = new Gson();
        GameConfig config = gson.fromJson(jsonStr, GameConfig.class);
        return config;
    }
}
