package com.mmall.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FTPUtil {

    private static final Logger logger= LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp=PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser=PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass=PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public FTPUtil(String ip, int port, String user, String pwd) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    /**
     * 连接服务器
     * @param ip
     * @param port
     * @param user
     * @param pwd
     * @return
     */
    private boolean connectServer(String ip,int port,String user,String pwd){
        boolean isSuccess=false;
        ftpClient=new FTPClient();
        try {
            ftpClient.connect(ip);  //连接
            isSuccess=ftpClient.login(user,pwd); //登陆
        } catch (IOException e) {
            logger.error("连接FTP服务器异常",e);
        }
        return isSuccess;
    }

    /**
     * 上传文件到FTP服务器
     * @param remotePath
     * @param fileList
     * @return
     */
    private boolean uploadFile(String remotePath,List<File> fileList) throws IOException {
        boolean uploaded=true;
        FileInputStream fis=null;
        //连接FTP服务器
        if(connectServer(this.ip,this.port,this.user,this.pwd)){
            try {
                //更改工作目录
                ftpClient.changeWorkingDirectory(remotePath);
                //设置缓冲区
                ftpClient.setBufferSize(1024);
                //设置字符编码
                ftpClient.setControlEncoding("UTF-8");
                //设置文件类型,设置为二进制文件
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                //打开本地的被动模式
                ftpClient.enterLocalPassiveMode();
                for(File fileItem:fileList){
                    fis=new FileInputStream(fileItem);
                    //存储文件
                    ftpClient.storeFile(fileItem.getName(),fis);
                }
            } catch (IOException e) {
                logger.error("上传文件异常",e);
                uploaded=false;
            } finally {
                fis.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }

    /**
     * 对外开放的接口
     * @param fileList
     * @return
     * @throws IOException
     */
    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil=new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        logger.info("开始连接FTP服务器");
        boolean result=ftpUtil.uploadFile("image",fileList);
        logger.info("开始连接ftp服务器，结束上传，上传结果{}");
        return result;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
