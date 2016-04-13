package edu.nuist.dateout.model;

public class DownloadResult{
    private String filePath;//下载完成后存放在本地的路径
    private boolean downloadSuccess;
    
    public String getFilePath()
    {
        return filePath;
    }
    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }
    public boolean isDownloadSuccess()
    {
        return downloadSuccess;
    }
    public void setDownloadSuccess(boolean downloadSuccess)
    {
        this.downloadSuccess = downloadSuccess;
    }
}