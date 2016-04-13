package edu.nuist.dateout.db;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.model.CustomMsgPack;
import edu.nuist.dateout.model.RecentMsgItem;

public class DBAssit
{
    // 数据库的相对存放路径,一般位于/data/data/packagename/目录下
    private final String dbPath = "/dateout.db";
    
    private SQLiteDatabase db;// 存储聊天记录的数据库
    
    private Context context;
    
    private String[] tablesStrArr = {"t_chat_history", "t_recent_chatlist"};
    
    /**
     * 数据库默认为chat.db3
     * 
     * @param context
     */
    public DBAssit()
    {
        this.context = DateoutApp.getInstance().getApplicationContext();
        // 打开数据库,数据库不存在则创建
        // 目录为/data/data/edu.nuist.dateout/files/chat.db3
        db = SQLiteDatabase.openOrCreateDatabase(context.getFilesDir().toString() + dbPath, null);
    }
    
    @Override
    protected void finalize()
        throws Throwable
    {
        // TODO Auto-generated method stub
        super.finalize();
    }
    
    /**
     * 用于手动关闭连接
     */
    public void closeDbConnect()
    {
        db.close();
    }
    
    /**
     * 用于手动关闭数据库连接后重连数据库
     */
    public void connectDb()
    {
        db = SQLiteDatabase.openOrCreateDatabase(context.getFilesDir().toString() + dbPath, null);
    }
    
    /**
     * 用于判断数据库是否连接
     * 
     * @return
     */
    public boolean isDbConnected()
    {
        if (db.isOpen())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * 用于插入聊天记录
     * 
     * @param msgPack 消息包
     */
    public void saveChatMessage(CustomMsgPack msgPack)
    {
        // 尝试插入数据
        String senderId = msgPack.getSenderId();// 发方Id
        String receiverId = msgPack.getReceiverId();// 收方Id
        String msgBody = msgPack.getMsgBody();// 消息内容
        String dateAndTime = msgPack.getDateAndTime();// 消息时间
        String msgType = msgPack.getMsgType() + "";// 消息类型,转换为String型进行存储
        // 执行插入语句
        try
        {
            db.execSQL("insert into t_chat_history values(null , ? , ? , ? , ? , ?)", new String[] {senderId,
                receiverId, msgBody, dateAndTime, msgType});
            Log.v("Dateout", "DBAssit==>" + "成功：插入聊天记录");
        }
        catch (SQLiteException e)
        {
            Log.v("Dateout", "DBAssit==>" + "失败：插入聊天记录");
            e.printStackTrace();
        }
        // db.close();
    }
    
    /**
     * 用于从数据库查找两个特定用户的最近20条聊天记录
     * 
     * @param senderIdArg 发方Id
     * @param receiverIdArg 收方Id
     * @param offset 数据表中记录的偏移位置
     * @return 聊天记录消息包队列
     */
    public ArrayList<CustomMsgPack> getChatMessageHistory(String senderIdArg, String receiverIdArg, int offset)
    {
        ArrayList<CustomMsgPack> chatMsgHistroyList = new ArrayList<CustomMsgPack>();
        String senderId;
        String receiverId;
        String msgBody;// 消息内容
        String dateAndTime;// 消息时间
        short msgType;// 消息类型
        CustomMsgPack msgPack;
        
        try
        {
            String sqlStr =
                "select * from t_chat_history "
                    + "where ((senderId=? and receiverId=?) or (senderId=? and receiverId=?)) "
                    + "order by _id desc limit 20 offset ?";
            Cursor cursor =
                db.rawQuery(sqlStr, new String[] {senderIdArg, receiverIdArg, receiverIdArg, senderIdArg, offset + ""});
            int rowNum = cursor.getCount();// 查询结果数
            if (rowNum > 0)
            {
                cursor.moveToFirst();
                for (int i = 0; i < rowNum; i++)
                {
                    senderId = cursor.getString(1);
                    receiverId = cursor.getString(2);
                    msgBody = cursor.getString(3);
                    dateAndTime = cursor.getString(4);
                    msgType = (short)Integer.parseInt(cursor.getString(5));
                    msgPack = new CustomMsgPack(senderId, receiverId, msgBody, dateAndTime, msgType);
                    // chatMsgHistroyList.add(msgPack);
                    chatMsgHistroyList.add(0, msgPack);
                    cursor.moveToNext();// 游标下移
                }
                cursor.close();
                Log.v("Dateout", "DBAssit==>" + "成功：查询用户" + senderIdArg + "与" + receiverIdArg + "最近20条聊天记录");
                return chatMsgHistroyList;
            }
            else
            {
                cursor.close();
                Log.v("Dateout", "DBAssit==>" + "成功：查询成功,但用户" + senderIdArg + "与" + receiverIdArg + "无聊天记录");
                return chatMsgHistroyList;// 返回空列表
            }
        }
        catch (SQLiteException e)
        {
            Log.v("Dateout", "DBAssit==>" + "失败：查询用户" + senderIdArg + "与" + receiverIdArg + "最近20条聊天记录");
            e.printStackTrace();
            return null;
        }
        // db.close();
    }
    
    /**
     * 用于从表t_chat_history中查找出用户A和用户B的最后一条聊天记录
     * 
     * @param senderIdArg 发方Id
     * @param receiverIdArg 收方Id
     * @return
     */
    public CustomMsgPack getLastChatMsg(String senderIdArg, String receiverIdArg)
    {
        
        String sqlStr =
            "select * from t_chat_history " + "where ((senderId=? and receiverId=?) or (senderId=? and receiverId=?)) "
                + "order by _id desc limit 0,1";
        CustomMsgPack lastMsgPack = new CustomMsgPack();
        try
        {
            // TODO 执行到此处可能出现db没有打开的错误
            Cursor cursor = db.rawQuery(sqlStr, new String[] {senderIdArg, receiverIdArg, receiverIdArg, senderIdArg});
            if (cursor.getCount() != 0)
            {
                cursor.moveToFirst();
                lastMsgPack.setSenderId(cursor.getString(1));
                lastMsgPack.setReceiverId(cursor.getString(2));
                lastMsgPack.setMsgBody(cursor.getString(3));
                lastMsgPack.setDateAndTime(cursor.getString(4));
                lastMsgPack.setMsgType((short)Integer.parseInt(cursor.getString(5)));
                return lastMsgPack;
            }
            cursor.close();
            // db.close();
            Log.v("Dateout", "DBAssit==>" + "成功：查询用户" + senderIdArg + "与" + receiverIdArg + "最后一条聊天记录");
            return null;
        }
        catch (SQLiteException e)
        {
            Log.v("Dateout", "DBAssit==>" + "失败：查询用户" + senderIdArg + "与" + receiverIdArg + "最后一条聊天记录");
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 创建表t_chat_history,用于存储聊天记录
     */
    private void createTableChatHistroy()
    {
        String tableName = "t_chat_history";
        try
        {
            // 执行DDL创建数据表
            String sqlStr =
                "create table " + tableName + " (_id integer primary key autoincrement," + " senderId varchar(32),"
                    + " receiverId varchar(32)," + " msgBody varchar(1024)," + " dateAndTime varchar(32),"
                    + " msgType varchar(2))";
            db.execSQL(sqlStr);
            Log.v("Dateout", "DBAssit==>" + "成功：创建数据表" + tableName);
        }
        catch (SQLException e)
        {
            Log.v("Dateout", "DBAssit==>" + "失败：创建数据表" + tableName);
            e.printStackTrace();
        }
        // db.close();
    }
    
    /**
     * 创建表t_recent_chatlist,用于存储最近联系人页面的聊天历史
     */
    private void createTableRecentChatList()
    {
        String tableName = "t_recent_chatlist";
        try
        {
            // 执行DDL创建数据表
            String sqlStr =
                "create table " + tableName
                    + "(loginId varchar(32)," + " chatId varchar(32)," + " msgType int," + " unReadMsgNum int,"
                    + " msgBody varchar(16)," + " dateAndTime varchar(32)," + " primary key (loginId,chatId))";
            db.execSQL(sqlStr);
            Log.v("Dateout", "DBAssit==>" + "成功：创建数据表" + tableName);
        }
        catch (SQLException e)
        {
            Log.v("Dateout", "DBAssit==>" + "失败：创建数据表" + tableName);
            e.printStackTrace();
        }
    }
    
    /**
     * 读取用户loginId的最近联系人列表
     * 
     * @param loginId 登录用户的Id
     * @return 返回用户的最近联系人列表
     */
    public ArrayList<RecentMsgItem> getRecentMsgList(String loginId)
    {
        String sqlStr = "select * from t_recent_chatlist" + " where loginId=?" + " order by dateAndTime desc";
        // RecentMsgItem recentMsgItem=new RecentMsgItem();
        ArrayList<RecentMsgItem> recentMsgItemList = new ArrayList<RecentMsgItem>();
        try
        {
            // TODO 执行到此处可能出现db没有打开的错误
            Cursor cursor = db.rawQuery(sqlStr, new String[] {loginId});
            int rowNum = cursor.getCount();// 查询结果数
            if (rowNum > 0)
            {
                cursor.moveToFirst();
                for (int i = 0; i < rowNum; i++)
                {
                    RecentMsgItem recentMsgItem = new RecentMsgItem();
                    // 没有_id列
                    // cursor.getString(0)是loginId
                    recentMsgItem.setUserId(cursor.getString(1));// chatId
                    recentMsgItem.setMsgType((short)cursor.getInt(2));// msgType
                    recentMsgItem.setUnReadMsgNum(cursor.getInt(3));// unReadMsgNum
                    recentMsgItem.setLastMessage(cursor.getString(4));// msgBody
                    recentMsgItem.setMsgDateAndTime(cursor.getString(5));
                    
                    recentMsgItemList.add(recentMsgItem);
                    cursor.moveToNext();// 游标下移
                }
                cursor.close();
                Log.v("Dateout", "DBAssit==>" + "成功：查询用户" + loginId + "的最近联系人列表");
                return recentMsgItemList;
            }
            else
            {
                cursor.close();
                Log.v("Dateout", "DBAssit==>" + "成功：但用户" + loginId + "最近联系人列表为空");
                return recentMsgItemList;// 返回空列表
            }
        }
        catch (SQLiteException e)
        {
            Log.v("Dateout", "DBAssit==>" + "失败：无法查询用户" + loginId + "最近联系人列表");
            e.printStackTrace();
            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 插入最近联系人记录到表t_recent_chatlist中
     * 
     * @param loginId 登录用户Id
     * @param item RecentMsgItem对象
     */
    public void saveRecentMsgList(String loginId, RecentMsgItem item)
    {
        String chatId = item.getUserId();
        String msgBody = item.getLastMessage();
        String dateAndTime = item.getMsgDateAndTime();
        short msgType = item.getMsgType();
        int unReadMsgNum = item.getUnReadMsgNum();
        
        // replace操作即insertOrUpdate操作,如果存在记录,则更新记录
        String sqlStr = "replace into t_recent_chatlist values(?,?,?,?,?,?)";
        Object[] sqlParams = new Object[] {loginId, chatId, msgType, unReadMsgNum, msgBody, dateAndTime};
        try
        {
            db.execSQL(sqlStr, sqlParams);
            Log.v("Dateout", "DBAssit==>" + "成功：插入最近联系人记录");
        }
        catch (Exception e)
        {
            Log.v("Dateout", "DBAssit==>" + "失败：插入聊天记录");
            e.printStackTrace();
        }
    }
    
    /**
     * 用于检测数据库中的表,如果不存在,创建它们
     */
    public void initDatabase()
    {
        // 连接未打开的话就打开
        if (!isDbConnected())
        {
            connectDb();
        }
        // 检测表的存在
        // 检测数据表的有效性,如果不存在,则创建表
        checkTables();
        closeDbConnect();// 关闭连接
    }
    
    /**
     * 用于批量检测数据库中表是否存在
     * 
     * @param tablesStrArr 多个表名字符串数组
     */
    private void checkTables()
    {
        // 检测表t_chat_history
        if (!existTable(tablesStrArr[0]))
        {
            Log.v("Dateout", "DBAssit==>" + tablesStrArr[0] + "表不存在");
            createTableChatHistroy();
        }
        else
        {
            Log.v("Dateout", "DBAssit==>" + tablesStrArr[0] + "表存在");
        }
        // 检测表t_recent_chatlist
        if (!existTable(tablesStrArr[1]))
        {
            Log.v("Dateout", "DBAssit==>" + tablesStrArr[1] + "表不存在");
            createTableRecentChatList();
        }
        else
        {
            Log.v("Dateout", "DBAssit==>" + tablesStrArr[1] + "表存在");
        }
    }
    
    /**
     * 用于判断指定的表是否存在
     * 
     * @param tableName 表名
     * @return true表示存在
     */
    private boolean existTable(String tableName)
    {
        String sqlStr = "SELECT count(*) FROM sqlite_master WHERE type='table' AND name=?;";
        Cursor cursor = db.rawQuery(sqlStr, new String[] {tableName});
        cursor.moveToFirst();
        int tableNum = cursor.getInt(0);
        cursor.close();
        if (tableNum == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    /**
     * 清除表中的所有数据 !!谨慎调用
     * 
     * @return
     */
    public void deleteAllFromTableChatHistory()
    {
        String sqlStr = "delete from t_chat_history";
        try
        {
            db.execSQL(sqlStr);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void deleteAllFromTableMsgList()
    {
        String sqlStr = "delete from t_recent_chatlist";
        try
        {
            db.execSQL(sqlStr);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void deleteTheirChatHistory(String userId1, String userId2)
    {
        String sqlStr =
            "delete from t_chat_history where (senderId=? and receiverId=?) or (receiverId=? and senderId=?)";
        Object[] sqlParams = new Object[] {userId1, userId2, userId2, userId1};
        try
        {
            db.execSQL(sqlStr, sqlParams);
            Log.v("Dateout", "DBAssit==>" + "成功：删除" + userId1 + "与" + userId2 + "的所有聊天记录记录");
        }
        catch (Exception e)
        {
            Log.v("Dateout", "DBAssit==>" + "失败：删除" + userId1 + "与" + userId2 + "的所有聊天记录记录");
            e.printStackTrace();
        }
    }
    
    public void deleteRecentChatListBetween(String loginId, String chatId)
    {
        String sqlStr = "delete from t_recent_chatlist where loginId=? and chatId=?";
        Object[] sqlParams = new Object[] {loginId, chatId};
        try
        {
            db.execSQL(sqlStr, sqlParams);
            Log.v("Dateout", "DBAssit==>" + "成功：删除用户" + loginId + "与" + chatId + "的会话记录");
        }
        catch (Exception e)
        {
            Log.v("Dateout", "DBAssit==>" + "失败：删除用户" + loginId + "与" + chatId + "的会话记录");
            e.printStackTrace();
        }
    }
}
