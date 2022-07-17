package com.jiaruiblog.justforonce.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @Author Jarrett Luo
 * @Date 2022/4/25 11:26
 * @Version 1.0
 */
@RestController
@RequestMapping("file")
@Slf4j
public class FileController {
    @Value("${file.upload.url}")
    private String uploadFilePath;

    /**
     * 上传文件并保存文件到指定路径中
     * @param files
     * @return
     */
    @SuppressWarnings("warning")
    @RequestMapping("/upload")
    public String httpUpload(@RequestParam("files") MultipartFile files[]){
        JSONObject object=new JSONObject();
        for(int i=0;i<files.length;i++){
            // 文件名
            String fileName = files[i].getOriginalFilename();
            File dest = new File(uploadFilePath +'/'+ fileName);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            try {
                files[i].transferTo(dest);
            } catch (Exception e) {
                log.error("{}",e);
                object.put("success",2);
                object.put("result","程序错误，请重新上传");
                return object.toString();
            }
        }
        object.put("success",1);
        object.put("result","文件上传成功");
        return object.toString();
    }

    /**
     * 根据文件名下载指定文件
     * @param request
     * @param response
     * @param fileName
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("/download")
    public String fileDownLoad(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam("fileName") String fileName) throws UnsupportedEncodingException {
        File file = new File(uploadFilePath +'/'+ fileName);
        if(!file.exists()){
            return "下载文件不存在";
        }

        // 获取要下载的文件名
        String formatFileName;
        String userAgent = request.getHeader("User-Agent");
        // 针对IE或者以IE为内核的浏览器
        if(userAgent.contains("MSIE")||userAgent.contains("Trident")){
            formatFileName = java.net.URLEncoder.encode(fileName, "UTF-8");
        } else {
            formatFileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
        }

        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + formatFileName );

        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
            byte[] buff = new byte[1024];
            OutputStream os  = response.getOutputStream();
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            log.error("{}",e);
            return "下载失败";
        }
        return "下载成功";
    }

    /**
     * 定时任务，删除路径下所有文件夹和文件。
     */
    @Scheduled(cron="0 0 3 * * ?")
    private void deleteFiles(){
        deleteFile(new File(uploadFilePath));
    }

    public void deleteFile(File file){
        //判断文件不为null或文件目录存在
        if (file == null || !file.exists()){
            log.info("暂无文件");
            return;
        }
        //取得这个目录下的所有子文件对象
        File[] files = file.listFiles();
        //遍历该目录下的文件对象
        for (File f: files){
            //打印文件名
            String name = f.getName();
            log.info(name);
            //判断子目录是否存在子目录,如果是文件则删除
            if (f.isDirectory()){
                deleteFile(f);
            }else {
                f.delete();
            }
        }
        //删除空文件夹  for循环已经把上一层节点的目录清空。
        file.delete();
    }

}
