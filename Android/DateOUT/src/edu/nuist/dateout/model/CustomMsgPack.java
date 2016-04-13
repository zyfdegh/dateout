package edu.nuist.dateout.model;

import edu.nuist.dateout.value.VariableHolder;


/**
 * @author Veayo
 *封装好的信息包,方便聊天气泡显示用户名,时间,内容.可以用于收方也可以是发方的消息包
 */
public class CustomMsgPack{
	/**
	 * 
	 */
	private String senderId;//发方用户名
	private String receiverId;//收方用户名
	private String msgBody;//消息内容
	private String dateAndTime;//消息时间
	private short msgType;//消息类型
	
	public CustomMsgPack(){
		this.senderId = "";
		this.receiverId = "";
		this.msgBody = "";
		this.dateAndTime = "";
		this.msgType = VariableHolder.MSG_TYPE_NOTSET;
	}
	public CustomMsgPack(String senderId, String receiverId, String msgBody,
			String dateAndTime, short msgType) {
		super();
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.msgBody = msgBody;
		this.dateAndTime = dateAndTime;
		this.msgType = msgType;
	}
	public short getMsgType() {
		return msgType;
	}
	public void setMsgType(short msgType) {
		this.msgType = msgType;
	}
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}
	public String getMsgBody() {
		return msgBody;
	}
	public void setMsgBody(String msgBody) {
		this.msgBody = msgBody;
	}
	public String getDateAndTime() {
		return dateAndTime;
	}
	public void setDateAndTime(String dateAndTime) {
		this.dateAndTime = dateAndTime;
	}
}