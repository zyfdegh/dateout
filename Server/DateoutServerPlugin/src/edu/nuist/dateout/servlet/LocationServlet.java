package edu.nuist.dateout.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.nuist.dateout.config.ServerConfig;
import edu.nuist.dateout.db.DBAssit;
import edu.nuist.dateout.model.UserLoc;
import edu.nuist.dateout.model.UserNear;
import edu.nuist.dateout.util.JsonAssit;

/**
 * 用于摇一摇页面地理位置信息的保存,并计算服务端附近的人列表,返回附近的人
 * @author Veayo
 *
 */
public class LocationServlet extends HttpServlet
{
    private static final long serialVersionUID = -5826021359906494765L;
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        System.out.println("\n");
        System.out.println("====收到地理位置处理请求====");
        SimpleDateFormat formatBuilder = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        System.out.println("时间" + formatBuilder.format(new Date()));
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String ip = request.getRemoteAddr();
        String port = String.valueOf(request.getRemotePort());
        System.out.println("请求来自" + ip + ":" + port);
        
        response.setContentType("text/html");
        javax.servlet.ServletOutputStream out = response.getOutputStream();
        String paramUserId = new String(request.getParameter("userId").getBytes("UTF-8"), "UTF-8").toString();
        String paramLocJingdu = new String(request.getParameter("locJingdu").getBytes("UTF-8"), "UTF-8").toString();
        String paramLocWeidu = new String(request.getParameter("locWeidu").getBytes("UTF-8"), "UTF-8").toString();
        System.out.println("请求参数:" + paramUserId + "," + paramLocJingdu + "," + paramLocWeidu);
        
        // 存储到数据库
        UserLoc user = new UserLoc();
        user.setUserId(paramUserId);
        user.setLocJingdu(Double.parseDouble(paramLocJingdu));
        user.setLocWeidu(Double.parseDouble(paramLocWeidu));
        boolean insertOk=new DBAssit().insertIntoTableLocation(user);
        if (insertOk)
        {
            System.out.println("已保存用户"+paramUserId+"的位置信息");
        }else {
            System.out.println("保存用户"+paramUserId+"的位置信息失败=.=");
        }
        
        // 从数据库取出数据,挨个计算距离,保存距离小于1000m的用户
        List<UserNear> usersNearList = new DBAssit().selectUsersNearFromTableLocation(user);
        System.out.println("找到" + usersNearList.size() + "位和" + user.getUserId() + "距离小于" + ServerConfig.DISTANCE_NEAR
            + "米的用户");
        for (int i = 0; i < usersNearList.size(); i++)
        {
            System.out.println(i + 1 + ".用户" + usersNearList.get(i).getUserId() + "的距离为:"
                + usersNearList.get(i).getDistance() + "米\t位置最近更新时间:" + usersNearList.get(i).getTime().toString());
        }
        // 使用JSON返回数据
        String responeStr = new JsonAssit().userNearList2JsonStr(usersNearList);
        System.out.println("响应内容:" + responeStr);
        out.print(responeStr);
        System.out.println("======完成请求======");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        doGet(request, response);
    }
}
