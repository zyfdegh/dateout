package edu.nuist.dateout.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import edu.nuist.dateout.config.ServerConfig;
import edu.nuist.dateout.model.GameConfig;
import edu.nuist.dateout.model.User;
import edu.nuist.dateout.model.UserLoc;
import edu.nuist.dateout.model.UserNear;
import edu.nuist.dateout.util.DistanceCaculator;

public class DBAssit
{
    public DBAssit()
    {
        // TODO 初始化数据库
        // TODO 检测表
    }
    
    /**
     * 用于返回特定用户的最近位置信息 输入用户Id,返回该用户最近位置信息,并封装在UserLoc对象中,作为返回值
     * 
     * @param userId 用户Id
     * @return 返回一个带有用户最新位置的UserLoc对象
     */
    public UserLoc selectLastestUserLoc(String userId)
    {
        UserLoc result = new UserLoc();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        /* 选取用户userId */
        String sqlstr = "select * from dateout_location where user_id=? order by _id desc limit 1";
        try
        {
            conn = DBUtil.getConnForMySql();
            ps = conn.prepareStatement(sqlstr);
            ps.setString(1, userId);
            rs = ps.executeQuery();
            while (rs.next())
            {
                // rs结果集长度为1,故这里不需要List来存储结果
                result.setUserId(userId);
                result.setLocJingdu(rs.getDouble(3));
                result.setLocWeidu(rs.getDouble(4));
                result.setTime(rs.getTimestamp(5));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            DBUtil.CloseResources(conn, ps, rs);
        }
        return result;
    }
    
    /**
     * 插入位置信息到表dateout_location
     * 
     * @param user UserLoc对象
     * @return 返回true表示插入成功
     */
    public boolean insertIntoTableLocation(UserLoc user)
    {
        boolean result = false;
        String userId = user.getUserId();
        double locJingdu = user.getLocJingdu();
        double locWeidu = user.getLocWeidu();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            /**
             * 能够防止主键冲突的SQL语句,如果冲突就更新值 INSERT INTO dateout_location(user_id,user_loc_jingdu,user_loc_weidu)
             * VALUES(?,?,?) ON DUPLICATE KEY UPDATE user_loc_jingdu=?,user_loc_weidu=?;
             */
            String sqlInsert =
                "INSERT INTO dateout_location(user_id,loc_jingdu,loc_weidu,time) VALUES(?,?,?,now())"
                    + " ON DUPLICATE KEY UPDATE loc_jingdu=?,loc_weidu=?,time=now()";
            conn = DBUtil.getConnForMySql();
            ps = conn.prepareStatement(sqlInsert);
            ps.setString(1, userId);
            ps.setDouble(2, locJingdu);
            ps.setDouble(3, locWeidu);
            ps.setDouble(4, locJingdu);
            ps.setDouble(5, locWeidu);
            ps.executeUpdate();
            result = true;
        }
        catch (MySQLIntegrityConstraintViolationException e)
        {
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            DBUtil.CloseResources(conn, ps, rs);
        }
        return result;
    }
    
    /**
     * 用于读表并计算和返回附近的人 输入一个UserLoc,获取他的经纬度坐标,从数据库中按行读取记录,计算两个用户的距离,如果距离小于1000米,存放 用户到列表usersList中,作为返回值
     * 
     * @param user UserLoc对象
     * @return 返回附近的人列表
     */
    public List<UserNear> selectUsersNearFromTableLocation(UserLoc user)
    {
        List<UserNear> usersList = new ArrayList<UserNear>();// 存放返回值
        
        Connection conn = null;
        CallableStatement cs = null;
        ResultSet rs = null;
        
        /**
         * 选出每个用户最近一次上传坐标的数据 select a.* from dateout_location a where not exists(select 1 from dateout_location b where
         * b.user_id=a.user_id and b.time>a.time)
         */
        String sqlstr =
            "select a.* from dateout_location a" + " where not exists(" + " select 1 from dateout_location b"
                + " where b.user_id=a.user_id and b.time>a.time)";
        try
        {
            conn = DBUtil.getConnForMySql();
            cs = conn.prepareCall(sqlstr, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = cs.executeQuery();
            while (rs.next())
            {
                UserNear userSelected = new UserNear();
                userSelected.setUserId(rs.getString(2));
                userSelected.setLocJingdu(rs.getDouble(3));
                userSelected.setLocWeidu(rs.getDouble(4));
                userSelected.setTime(rs.getTimestamp(5));
                // 排除自己
                if (!userSelected.getUserId().equals(user.getUserId()))
                {
                    // 计算用户user与userSelected之间的距离
                    double distanceInMeter = new DistanceCaculator().getDistance(user, userSelected);
                    // 小于1000米,则加入列表
                    if (distanceInMeter < ServerConfig.DISTANCE_NEAR)
                    {
                        userSelected.setDistance(distanceInMeter);
                        usersList.add(userSelected);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            DBUtil.CloseResources(conn, cs, rs);
        }
        return usersList;
    }
    
    /**
     * 用于向dateout_game表中插入记录
     * 
     * @param gameConfig 游戏配置对象
     * @return 返回插入结果,true表示成功
     */
    public boolean insertIntoTableGame(GameConfig gameConfig)
    {
        boolean result = false;
        String userId = gameConfig.getUserId();
        String picUrl = gameConfig.getPicUrl();
        int timeOut = gameConfig.getTimeOut();
        int difficulty = gameConfig.getDifficulty();
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            /**
             * 能够防止主键冲突的SQL语句,如果冲突就更新值 INSERT INTO dateout_location(user_id,user_loc_jingdu,user_loc_weidu)
             * VALUES(?,?,?) ON DUPLICATE KEY UPDATE user_loc_jingdu=?,user_loc_weidu=?;
             */
            String sqlInsert =
                "INSERT INTO dateout_game VALUES(?,?,?,?)"
                    + " ON DUPLICATE KEY UPDATE g_pic_url=?,g_time=?,g_difficulty=?";
            conn = DBUtil.getConnForMySql();
            ps = conn.prepareStatement(sqlInsert);
            ps.setString(1, userId);
            ps.setString(2, picUrl);
            ps.setInt(3, timeOut);
            ps.setInt(4, difficulty);
            ps.setString(5, picUrl);
            ps.setInt(6, timeOut);
            ps.setInt(7, difficulty);
            ps.executeUpdate();
            result = true;
        }
        catch (MySQLIntegrityConstraintViolationException e)
        {
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            DBUtil.CloseResources(conn, ps, rs);
        }
        return result;
    }
    
    /**
     * 用于从表dateout_game查询用户userId的游戏配置记录
     * 
     * @param userId 待查询用户
     * @return 返回一个GameConfig对象,内含游戏配置
     */
    public GameConfig selectGameConfigFromTableGame(String userId)
    {
        List<GameConfig> result = new ArrayList<GameConfig>();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sqlstr = "select * from dateout_game where user_id=?";
        try
        {
            conn = DBUtil.getConnForMySql();
            ps = conn.prepareStatement(sqlstr);
            ps.setString(1, userId);
            rs = ps.executeQuery();
            // cs = conn.prepareCall(sqlstr, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            // rs = cs.executeQuery();
            while (rs.next())
            {
                GameConfig config = new GameConfig();
                config.setUserId(rs.getString(1));
                config.setPicUrl(rs.getString(2));
                config.setTimeOut(rs.getInt(3));
                config.setDifficulty(rs.getInt(4));
                result.add(config);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            DBUtil.CloseResources(conn, ps, rs);
        }
        if (result.size()>=1)
        {
            return result.get(0);
        }else {
            return null;
        }
    }
    
    /**
     * 用于从数据库中选择出一批随机的常规用户 用户不是特殊的用户(不是admin,也不是带有dateout字样的内部用户) 如果用户总数少于24,返回所有用户
     * 
     * @return 返回一组用户列表
     */
    public ArrayList<User> selectRandomUsers()
    {
        ArrayList<User> usersList = new ArrayList<User>();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        // 随机选取24个常规用户
        // 在MySQL中,like关键字忽略大小写
        String sqlstr =
            "select username from ofuser where username!='admin' and username not like '%dateout%' order by rand() limit 24";
        try
        {
            conn = DBUtil.getConnForMySql();
            ps = conn.prepareStatement(sqlstr);
            rs = ps.executeQuery();
            while (rs.next())
            {
                User user = new User();
                user.setUserId(rs.getString(1));
                usersList.add(user);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            DBUtil.CloseResources(conn, ps, rs);
        }
        return usersList;
    }
}
