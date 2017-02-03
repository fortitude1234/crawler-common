package com.dianping.ssp.crawler.common.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.ssp.file.upload.SSPUpload;
import com.dianping.ssp.file.upload.client.venus.result.UploadResult;

/**
 * Created by iClaod on 10/17/16.
 */
public class ImgUploadUtil {

    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(ImgUploadUtil.class);

    /**
     * 将下载的图片上传，返回一个新的地址
     */
    public static String upload(byte[] picBytes, String fileName) {
        String filePath = "";
        for (int i = 0; i < 3; i++ ) {
            try {
                UploadResult result = SSPUpload.Venus.getImageUploadClient().uploadFile(picBytes, fileName, null, null, null, null, "0");
                if (result == null || !result.isSuccess() || result.getData() == null || StringUtils.isEmpty(result.getData().getOriginalLink())) {
                    continue;
                }
                filePath = result.getData().getOriginalLink();
                break;
            } catch (Exception e) {
                LOGGER.error("upload img error", e);
            }
        }
        return filePath;
    }


    /**
     * 获取图片字节流
     *
     * @param uri
     * @return
     * @throws Exception
     */
    public static byte[] downloadImg(String uri) {
        CloseableHttpClient client = HttpClientBuilder.create().setConnectionTimeToLive(30, TimeUnit.SECONDS).build();
        HttpGet get = new HttpGet(uri);
        for (int i = 0; i < 3; i++) {
            try {
                HttpResponse response = client.execute(get);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    Header[] headers = response.getAllHeaders();
                    for (Header header: headers) {
                        if (StringUtils.isNotBlank(header.getName()) && header.getName().equalsIgnoreCase("content-type")
                                && StringUtils.isNotBlank(header.getValue()) && !header.getValue().matches(".*?image.*")) {
                            return new byte[0];
                        }
                    }
                    if (entity != null) {
                        return EntityUtils.toByteArray(entity);
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("download img error for url:" + uri, e);
            } finally {
                try {
                    client.close();
                } catch (Exception e1) {
                    LOGGER.warn("close client error", e1);
                }
            }
        }
        return new byte[0];
    }
    
    public static String getLocalPath(String uri,boolean isThumbnail) {
        byte[] bytes = downloadImg(uri);
        String newUrl = "";
        if (bytes.length > 0) {
            newUrl = upload(bytes, uri);
        } else {
            LOGGER.info("img size is 0, url: " + uri);
        }
        try {
            if (newUrl.contains("gif")) {
                newUrl = getOriginalFilePath(newUrl);
            } else {
                BufferedImage sourceImg = ImageIO.read(new ByteArrayInputStream(bytes));
                if (sourceImg != null && (sourceImg.getWidth() > 720) && !isThumbnail) {
                    newUrl = addSizeToImgUrl(newUrl, 720);
                }
            }
        } catch (Exception e) {
            LOGGER.error("failed to load size", e);
        }
        return newUrl;
    }

    public static String addSizeToImgUrl(String originUrl, int size) {
        return originUrl.replace("/ssparticlepicture", String.format("/%d.0.%d/ssparticlepicture", size, 90));
    }

    public static String getOriginalFilePath(String originUrl) {
        return originUrl.replace("/ssparticlepicture", "/0.0.o/ssparticlepicture");
    }
//
//    public static void main(String[] args) {
//        System.out.println(getLocalPath("http://www.zggonglue.com/upload/a9d78f608b777170965ccada5c9e79a7.jpg"));
//    }
}
