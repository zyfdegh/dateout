package edu.nuist.dateout.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import edu.nuist.dateout.value.AppConfig;

/**
 * Bitmap与DrawAble与byte[]与InputStream之间的转换工具类
 * 
 * @author Veayo
 * 
 */
public class FormatTools
{
    private static FormatTools tools = new FormatTools();
    
    public static FormatTools getInstance()
    {
        if (tools == null)
        {
            tools = new FormatTools();
            return tools;
        }
        return tools;
    }
    
    // 将byte[]转换成InputStream
    public static InputStream Byte2InputStream(byte[] b)
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        return bais;
    }
    
    // 将InputStream转换成byte[]
    public static byte[] InputStream2Bytes(InputStream is)
    {
        String str = "";
        byte[] readByte = new byte[1024];
        try
        {
            while ((is.read(readByte, 0, 1024)) != -1)
            {
                str += new String(readByte).trim();
            }
            return str.getBytes();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    // 将Bitmap转换成InputStream
    public static InputStream Bitmap2InputStream(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }
    
    // 将Bitmap转换成InputStream
    public static InputStream Bitmap2InputStream(Bitmap bm, int quality)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }
    
    // 将InputStream转换成Bitmap
    public static Bitmap InputStream2Bitmap(InputStream is)
    {
        return BitmapFactory.decodeStream(is);
    }
    
    // Drawable转换成InputStream
    public static InputStream Drawable2InputStream(Drawable d)
    {
        Bitmap bitmap = drawable2Bitmap(d);
        return Bitmap2InputStream(bitmap);
    }
    
    // InputStream转换成Drawable
    public static Drawable InputStream2Drawable(InputStream is)
    {
        Bitmap bitmap = InputStream2Bitmap(is);
        return bitmap2Drawable(bitmap);
    }
    
    // Drawable转换成byte[]
    public static byte[] Drawable2Bytes(Drawable d)
    {
        Bitmap bitmap = drawable2Bitmap(d);
        return Bitmap2Bytes(bitmap);
    }
    
    // byte[]转换成Drawable
    public static Drawable Bytes2Drawable(byte[] b)
    {
        Bitmap bitmap = Bytes2Bitmap(b);
        return bitmap2Drawable(bitmap);
    }
    
    // Bitmap转换成byte[]
    public static byte[] Bitmap2Bytes(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    
    // byte[]转换成Bitmap
    public static Bitmap Bytes2Bitmap(byte[] b)
    {
        if (b.length != 0)
        {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return null;
    }
    
    // Drawable转换成Bitmap
    public static Bitmap drawable2Bitmap(Drawable drawable)
    {
        Bitmap bitmap =
            Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    
    // Bitmap转换成Drawable
    public static Drawable bitmap2Drawable(Bitmap bitmap)
    {
        BitmapDrawable bd = new BitmapDrawable(bitmap);
        Drawable d = (Drawable)bd;
        return d;
    }
    
    /**
     * 用于从图片文件的Uri读取图片并返回为Bitmap
     * 
     * @param context 上下文,一般为Activity.this
     * @param picUri 图片Uri
     * @return 返回图片对应的Bitmap
     */
    public static Bitmap uri2Bitmap(Context context, Uri url)
    {
      //TODO 这个方法在跨分区处理图像时候会报空指针异常
        try
        {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), url);
            return bitmap;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 用于将Bitmap转换为Uri
     * 
     * @param context 上下文,一般为Activity.this
     * @param bitmap Bitmap图片对象
     * @return 返回Bitmap图片对象对应的Uri
     */
    public static Uri bitmap2Uri(Context context, Bitmap bitmap)
    {
        //TODO 这个方法在跨分区处理图像时候会报空指针异常
        //返回空表示无法存储,一般是跨区拷贝文件
        String path=MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null, null);
        if (path==null)
        {
            return null;
        }else {
            return Uri.parse(path);
        }
    }
    
    /**
     * 文件转为Drawable
     * 
     * @param file
     * @return
     */
    public static Drawable file2Drawable(File file)
    {
        FileInputStream fis;
        try
        {
            fis = new FileInputStream(file);
            return FormatTools.InputStream2Drawable(fis);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    // public File drawable2File(Drawable drawable){
    // InputStream is=Drawable2InputStream(drawable);
    // File file=new Inputstream
    // }
    // public Uri drawable2Uri(Drawable drawable){
    // return bitmap2Uri(context, bitmap)
    // }
    
    /**
     * 返回资源Uri
     * 
     * @param r
     * @param resId
     * @return
     */
    public static Uri resId2Uri(Resources r, int resId)
    {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + r.getResourcePackageName(resId) + "/"
            + r.getResourceTypeName(resId) + "/" + r.getResourceEntryName(resId));
    }
    
    /**
     * Try to return the absolute file path from the given Uri
     *返回给定Uri的绝对文件路径,如将content://...转换为/mnt/sdcard/...
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String uri2Path(final Context context, final Uri uri)
    {
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        String result = null;
        if (scheme == null)
            result = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme))
        {
            result = uri.getPath();
        }
        else if (ContentResolver.SCHEME_CONTENT.equals(scheme))
        {
            Cursor cursor = context.getContentResolver().query(uri, new String[] {ImageColumns.DATA}, null, null, null);
            if (null != cursor)
            {
                if (cursor.moveToFirst())
                {
                    int index = cursor.getColumnIndex(ImageColumns.DATA);
                    if (index > -1)
                    {
                        result = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        else if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme))
        {
            //TODO 转换格式为android.resource://edu.nuist.dateout/drawable/default_game_bkg这样的Uri到path
            //暂时只能这样了，不知道有没有更好的办法
        }
        return result;
    }

    /**
     * 将res.drawable内的资源图片存储到文件，再从文件读取得到文件路径=.=
     * @param context Activity上下文
     * @param resId 资源文件id
     * @return 得到序列化后的文件绝对路径
     */
    public static String resId2Path(final Context context, final int resId){
        try
        {
            File f=new File(new FileAssit().getValidSdCardPath()+"/"+AppConfig.FILE_SERIALIZE_CACHE_IMAGE);
            InputStream inputStream = context.getResources().openRawResource(resId);
            OutputStream out=new FileOutputStream(f);
            byte buf[]=new byte[1024];
            int len;
            while((len=inputStream.read(buf))>0)
            out.write(buf,0,len);
            out.close();
            inputStream.close();
            return f.getAbsolutePath();
        }
        catch (IOException e){
            return null;
        }
    }
    /**
     * 
     * @param context
     * @param drawable
     * @return
     */
    public static Uri drawable2Uri(Context context,Drawable drawable){
        return bitmap2Uri(context, drawable2Bitmap(drawable));
    }
    
//    public static Drawable uri2Drawable(Uri uri){
//        Bitmap bitmap=new BitmapFactory().decode
//    }
}