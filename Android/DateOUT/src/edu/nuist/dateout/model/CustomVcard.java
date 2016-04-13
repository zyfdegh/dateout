package edu.nuist.dateout.model;

/**
 * 
 * @author Veayo
 *
 */
public class CustomVcard
{
    
    private String nickName;// 昵称 
    
    private String birthPlace;// 家乡所在地 
    
    private String email;// 电子邮件 
    
    private String gender;// 性别
    
    private String moodie;// 个性签名
    
    private String birthDay;// 出生年月日
    
    private String interest;// 兴趣爱好
    
    private String singleState;// 情感状态
    
    private String job;// 职业
    
    private String height;// 身高
    
    private String weight;// 体重
    
    public CustomVcard()
    {
        super();
    }
    public CustomVcard(boolean useDefault)
    {
        super();
        if (useDefault)
        {
            this.nickName = "未设置";
            this.gender = "未设置";
            this.moodie = "未设置";
            this.birthDay = "未设置";
            this.birthPlace = "未设置";
            this.interest = "未设置";
            this.singleState = "未设置";
            this.email = "未设置";
            this.job = "未设置";
            this.height = "未设置";
            this.weight = "未设置";
        }
    }
    
    public CustomVcard(String nickName, String gender, String moodie, String birthDay, String birthPlace, String interest,
        String singleState, String email, String job, String height, String weight)
    {
        super();
        this.nickName = nickName;
        this.gender = gender;
        this.moodie = moodie;
        this.birthDay = birthDay;
        this.birthPlace = birthPlace;
        this.interest = interest;
        this.singleState = singleState;
        this.email = email;
        this.job = job;
        this.height = height;
        this.weight = weight;
    }
    
    public String getNickName()
    {
        return nickName;
    }
    
    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }
    
    public String getGender()
    {
        return gender;
    }
    
    public void setGender(String gender)
    {
        this.gender = gender;
    }
    
    public String getMoodie()
    {
        return moodie;
    }
    
    public void setMoodie(String moodie)
    {
        this.moodie = moodie;
    }
    
    public String getBirthDay()
    {
        return birthDay;
    }
    
    public void setBirthDay(String birthDay)
    {
        this.birthDay = birthDay;
    }
    
    public String getBirthPlace()
    {
        return birthPlace;
    }
    
    public void setBirthPlace(String birthPlace)
    {
        this.birthPlace = birthPlace;
    }
    
    public String getInterest()
    {
        return interest;
    }
    
    public void setInterest(String interest)
    {
        this.interest = interest;
    }
    
    public String getSingleState()
    {
        return singleState;
    }
    
    public void setSingleState(String singleState)
    {
        this.singleState = singleState;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public String getJob()
    {
        return job;
    }
    
    public void setJob(String job)
    {
        this.job = job;
    }
    
    public String getHeight()
    {
        return height;
    }
    
    public void setHeight(String height)
    {
        this.height = height;
    }
    
    public String getWeight()
    {
        return weight;
    }
    
    public void setWeight(String weight)
    {
        this.weight = weight;
    }
}
