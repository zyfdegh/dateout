package edu.nuist.dateout.util;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import edu.nuist.dateout.value.VariableHolder;

/**
 * @author Veayo 显示照片选取对话列表,包含从相册选取和拍照
 */
public class PhotoAssit
{
    private final CharSequence[] items = {"拍照", "相册"};
    
    private Activity activity;
    
    public PhotoAssit(Activity activity)
    {
        this.activity = activity;
    }
    
    /**
     * 用于弹出照片选取对话框
     */
    public void photoChooseDlg()
    {
        AlertDialog dlg =
            new AlertDialog.Builder(activity).setTitle("选择图片").setItems(items, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int item)
                {
                    dialog.dismiss();
                    if (item == 0)// 拍照
                    {
                        startCamera();
                    }
                    else if (item == 1)// 相册
                    {
                        startGallery();
                    }
                }
            }).create();
        dlg.show();
    }
    
    /**
     * 用于启动相机
     */
    public void startCamera()
    {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Uri.parse(FilePathTool.getCameraCachedImagePath())
        // Uri.fromFile(new File(new FileAssit().getValidSdCardPath()+"/"+AppConfig.DIR_APP_CACHE_IMAGE_CAMERA))
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FilePathTool.getCameraCachedImagePath())));
        activity.startActivityForResult(cameraIntent, VariableHolder.REQUEST_CODE_PHOTO_FROM_CAMERA);
    }
    
    /**
     * 用于启动相册
     */
    public void startGallery()
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, null);
        galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(galleryIntent, VariableHolder.REQUEST_CODE_PHOTO_FROM_GALLERY);
    }
    
    /**
     * 裁剪图片,用于选取头像,128*128像素 较低图像质量
     * 
     * @param uri
     */
    public void startPhotoZoomStyle1(Uri uri)
    {
        startPhotoZoomSmall(1, 1, 150, 150, uri);
    }
    
    /**
     * 裁剪图片,480*480像素,1:1 一般图像质量
     * 
     * @param uri
     */
    public void startPhotoZoomStyle2(Uri uri)
    {
        startPhotoZoomSmall(1, 1, 480, 480, uri);
    }
    
    /**
     * 裁剪图片,720*720像素,1:1 较高图像质量 onActivityResult中接收REQUEST_CODE_PHOTO_ZOOM_FINISH_LARGE
     * 
     * @param uri
     */
    public void startPhotoZoomStyle3(Uri uri)
    {
        startPhotoZoomLarge(1, 1, 720, 720, uri);
    }
    
    /**
     * 裁剪图片,1080*1080像素,1:1 高清图像质量 谨慎使用,防止OOM onActivityResult中接收REQUEST_CODE_PHOTO_ZOOM_FINISH_LARGE
     * 
     * @param uri
     */
    public void startPhotoZoomStyle4(Uri uri)
    {
        startPhotoZoomLarge(1, 1, 1080, 1080, uri);
    }
    
    // TODO 学习EIM,添加图像压缩功能,依据图片尺寸大小进行图片尺寸压缩
    
    /**
     * 用于调用系统的图像裁剪器
     * 
     * @param x 横纵比
     * @param y 横纵比
     * @param width 宽度
     * @param height 高度
     * @param uri 输入图像Uri
     */
    private void startPhotoZoomSmall(int x, int y, int width, int height, Uri uri)
    {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例,这里为1:1正方形
        intent.putExtra("aspectX", x);
        intent.putExtra("aspectY", y);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// 返回Uri
        activity.startActivityForResult(intent, VariableHolder.REQUEST_CODE_PHOTO_ZOOM_FINISH_SMALL);
    }
    
    private void startPhotoZoomLarge(int x, int y, int width, int height, Uri uri)
    {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例,这里为1:1正方形
        intent.putExtra("aspectX", x);
        intent.putExtra("aspectY", y);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);// 不返回Data
        
        Uri cropCacheImage = Uri.parse("file://" + FilePathTool.getCropCachedImagePath());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropCacheImage);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// 返回Uri
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, VariableHolder.REQUEST_CODE_PHOTO_ZOOM_FINISH_LARGE);
    }
}
