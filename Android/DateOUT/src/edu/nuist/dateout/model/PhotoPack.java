package edu.nuist.dateout.model;

import android.widget.ImageView;

/** 内部类 用来记录照片以及照片的状态 */
/**
 * 包含ImageView和isDefaultImage布尔型值,用在EditVCard中,方便处理照片是否被修改过
 * @author Veayo
 *
 */
public class PhotoPack
{
    private ImageView imageView;
    private Boolean isDefaultImage;

    public PhotoPack(ImageView imageView, Boolean isDefaultImage)
    {
        super();
        this.imageView = imageView;
        this.isDefaultImage = isDefaultImage;
    }
    
    public ImageView getImageView()
    {
        return imageView;
    }
    
    public void setImageView(ImageView imageView)
    {
        this.imageView = imageView;
    }

    public Boolean getIsDefaultImage()
    {
        return isDefaultImage;
    }

    public void setIsDefaultImage(Boolean isDefaultImage)
    {
        this.isDefaultImage = isDefaultImage;
    }
}
