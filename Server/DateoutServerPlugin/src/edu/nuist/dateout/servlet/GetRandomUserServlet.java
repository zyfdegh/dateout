package edu.nuist.dateout.servlet;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.nuist.dateout.db.DBAssit;
import edu.nuist.dateout.model.User;
import edu.nuist.dateout.model.UserLoc;
import edu.nuist.dateout.model.UserRandom;
import edu.nuist.dateout.util.DistanceCaculator;
import edu.nuist.dateout.util.FilePathAssit;
import edu.nuist.dateout.util.JsonAssit;
import edu.nuist.dateout.util.OnlineStatusJudge;

/**
 * 用于软件Tab1中随机用户的数据的获取,返回一批随机的用户
 * 
 * @author Veayo
 *
 */
public class GetRandomUserServlet extends HttpServlet
{
    private static final long serialVersionUID = 7789150139225361127L;
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        System.out.println("\n");
        System.out.println("====收到随机好友处理请求====");
        SimpleDateFormat formatBuilder = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        System.out.println("时间" + formatBuilder.format(new Date()));
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String ip = request.getRemoteAddr();
        String port = String.valueOf(request.getRemotePort());
        System.out.println("请求来自" + ip + ":" + port);
        
        response.setContentType("text/plain");
        javax.servlet.ServletOutputStream out = response.getOutputStream();
        // 获取请求参数
        String paramUserId = new String(request.getParameter("userId").getBytes("UTF-8"), "UTF-8").toString();
        String paramLocJingdu = new String(request.getParameter("locJingdu").getBytes("UTF-8"), "UTF-8").toString();
        String paramLocWeidu = new String(request.getParameter("locWeidu").getBytes("UTF-8"), "UTF-8").toString();
        System.out.println("请求参数:" + paramUserId + "," + paramLocJingdu + "," + paramLocWeidu);
        
        //从数据库获取一批随机的用户ID(不包括admin和所有包含dateout字段的用户,即特殊用户)
        ArrayList<User> usersList = new DBAssit().selectRandomUsers();
        UserLoc paramUserLoc =
            new UserLoc(paramUserId,Double.parseDouble(paramLocWeidu), Double.parseDouble(paramLocJingdu), new Timestamp(
                System.currentTimeMillis()));
        //存储用户位置信息
//        new DBAssit().insertIntoTableLocation(paramUserLoc);
        
        // 获取随机好友的头像下载链接,在线状态,距离信息,并封装到UserRandom列表
        ArrayList<UserRandom> usersRandomList=fillUserInfo(usersList,paramUserLoc);
        //转换为JSON并输出
        String jsonStr=new JsonAssit().userRandomList2JsonStr(usersRandomList);
        out.print(jsonStr);
        System.out.println("响应内容:"+jsonStr);
        System.out.println("======完成请求======");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        doGet(request, response);
    }
    
    /**
     * 用于填充随机获取到的用户的信息,如头像下载链接,在线状态,距离等信息 其中距离是通过Servlet请求参数中的用户(即paramUserId)和随机用户计算而来
     * 
     * @param usersList 随机用户列表,每个User中只包含一个userId
     * @param paramUserId 请求参数userId,用于服务端计算两者距离,其距离为最近一次定位数据,客户端不必再定位并上传
     * @return 返回填充好的用户列表
     */
    private ArrayList<UserRandom> fillUserInfo(ArrayList<User> usersList, UserLoc paramUserLoc)
    {
        ArrayList<UserRandom> userRandomList = new ArrayList<UserRandom>();
        if (usersList == null)
        {
            usersList = new ArrayList<User>();
        }
        for (int i = 0; i < usersList.size(); i++)
        {
            UserRandom item=new UserRandom();
            //一个随机用户的userId
            String userId = usersList.get(i).getUserId();
            item.setUserId(userId);
            
            // 获取在线状态
            short onlineStat = OnlineStatusJudge.getUserOnlineStatus(userId);
            item.setOnlineStat(onlineStat);
            
            // 获取用户距离
            UserLoc userLoc = new DBAssit().selectLastestUserLoc(userId);// 随机用户
            if (userLoc==null||userLoc.getUserId()==null)
            {
                //这个用户没有定位过,也就是位置表中找不到该用户
                item.setDistance(-1);
            }else {
                double distance=new DistanceCaculator().getDistance(userLoc, paramUserLoc);
                item.setDistance(distance);
            }
            
            // 获取用户头像下载链接
            String downLink=FilePathAssit.getHeadImageDownLink(userId);
            if (downLink==null)
            {
                downLink="";
            }
            item.setHeadUrl(downLink);
            
            //加入列表
            userRandomList.add(item);
        }
        return userRandomList;
    }
}
