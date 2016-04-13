package edu.nuist.dateout.model;

public class UploadedFileModel
{
    String fileName;
    String fileSavePath;
    short fileType;
    public UploadedFileModel(){
        super();
    }
    public UploadedFileModel(String fileName, String fileSavePath, short fileType)
    {
        super();
        this.fileName = fileName;
        this.fileSavePath = fileSavePath;
        this.fileType = fileType;
    }
    public String getFileName()
    {
        return fileName;
    }
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
    public String getFileSavePath()
    {
        return fileSavePath;
    }
    public void setFileSavePath(String fileSavePath)
    {
        this.fileSavePath = fileSavePath;
    }
    public short getFileType()
    {
        return fileType;
    }
    public void setFileType(short fileType)
    {
        this.fileType = fileType;
    }
    
}
