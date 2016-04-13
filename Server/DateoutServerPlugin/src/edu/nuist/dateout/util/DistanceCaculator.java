package edu.nuist.dateout.util;

import edu.nuist.dateout.model.UserLoc;

public class DistanceCaculator
{
    private final double EARTH_RADIUS = 6378137;
    
    private double rad(double d)
    {
        return d * Math.PI / 180.0;
    }
    
    /**
     * 根据两点间经纬度坐标，计算两点间距离，单位为米
     * 
     * @return
     */
    /**
     * 用于计算两个用户之间的距离(单位为米),输入的是两个UserLoc用户对象
     * @param user1 UserLoc对象
     * @param user2 UserLoc对象
     * @return 返回两个用户之间的距离
     */
    public double getDistance(UserLoc user1, UserLoc user2)
    {
        double lng1=user1.getLocJingdu();
        double lat1=user1.getLocWeidu();
        double lng2=user2.getLocJingdu();
        double lat2=user2.getLocWeidu();
        return getDistance(lng1, lat1, lng2, lat2);
    }
    
    /**
     * 计算地球球面上两个点之间沿着球面的距离,输入为两个点的经纬度坐标,返回值单位是米（double型）
     * @param lng1 点1的经度
     * @param lat1 点1的纬度
     * @param lng2 点2的经度
     * @param lat2 点1的纬度
     * @return 返回点1和点2之间沿着地球球面的距离,单位是米
     */
    public double getDistance(double lng1,double lat1,double lng2,double lat2){
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s =
            2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }
}
