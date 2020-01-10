package com.zhy.aliyunOSS.util;

import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.PutObjectRequest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

/**
 * <p>
 * Title: AilyunOSSUtil
 * </p>
 * <p>
 * Description:使用阿里云OSS
 * </p>
 * @author ZhangYin @date 2020年1月10日
 */
public class AilyunOSSUtil {
	public static final Logger logger = LoggerFactory.getLogger(AilyunOSSUtil.class);
	// Endpoint北京地区节点
	static String endpoint = "http://oss-cn-beijing.aliyuncs.com";
	// 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录
	// https://ram.console.aliyun.com 创建RAM账号。
	static String accessKeyId = "";
	static String accessKeySecret = "";
	// 空间
	private static String bucketName = "";
	/**
	 * <p>
	 * Title: uploadImg2Oss
	 * </p>
	 * <p>
	 * Description:上传图片
	 * </p>
	 * @param url图片路径
	 */
	public void uploadImg2Oss(String url) {
		// 创建文件
		File fileOnServer = new File(url);
		// 创建输入流
		FileInputStream fin;
		try {
			// 读取文件
			fin = new FileInputStream(fileOnServer);
			// 分割字符串 返回字符串数组
			String[] split = url.split("/");
			// 调用上传文件方法
			AilyunOSSUtil.uploadFile2OSS(fin, split[split.length - 1]);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * Title: uploadImg2Oss
	 * </p>
	 * <p>
	 * Description:MultipartFile 类型文件上传
	 * </p>
	 * @param file @return @throws Exception
	 */
	public String uploadImg2Oss(MultipartFile file) throws Exception {
		// 获取文件大小
		if (file.getSize() > 10 * 1024 * 1024) {
			throw new Exception("上传图片大小不能超过10M！");
		}
		// 取文件名
		String originalFilename = file.getOriginalFilename();
		// 截取文件名后缀准换小写
		String substring = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
		// 生成随机数
		Random random = new Random();
		// 生成新的文件名
		String name = random.nextInt(10000) + System.currentTimeMillis() + substring;
		try {
			// 读取文件流
			InputStream inputStream = file.getInputStream();
			// 调用上传文件方法
			AilyunOSSUtil.uploadFile2OSS(inputStream, name);
			// 返回新的文件名
			return name;
		} catch (Exception e) {
			throw new Exception("图片上传失败");
		}
	}

	/**
	 * 上传到OSS服务器 如果同名文件会覆盖服务器上的
	 * 
	 * @param instream 文件流
	 * @param fileName 文件名称 包括后缀名
	 * @return 出错返回"" ,唯一MD5数字签名
	 */
	public static String uploadFile2OSS(InputStream instream, String fileName) {
		String ret = "";
		try {
			// 创建上传Object的Metadata
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength(instream.available());
			objectMetadata.setCacheControl("no-cache");
			objectMetadata.setHeader("Pragma", "no-cache");
			objectMetadata.setContentType(getcontentType(fileName.substring(fileName.lastIndexOf("."))));
			objectMetadata.setContentDisposition("inline;filename=" + fileName);
			// 上传文件
			OSSClient ossClient= new OSSClient(endpoint, accessKeyId, accessKeySecret);
			PutObjectResult putResult = ossClient.putObject(bucketName, fileName, instream, objectMetadata);
			ret = putResult.getETag();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if (instream != null) {
					instream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	/**
	 * 获得url链接
	 *
	 * @param key 保存到OSS的文件路径
	 * @return
	 */
	public static String getUrl(String key) {
		// 设置URL过期时间为1年 3600l* 1000*24*365
		Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365);
		// 生成URL
		OSSClient ossClient= new OSSClient(endpoint, accessKeyId, accessKeySecret);
		URL url = ossClient.generatePresignedUrl(bucketName, key, expiration);
		if (url != null) {
			return url.toString();
		}
		return null;
	}

	/**
	 * 获得图片路径
	 *
	 * @param fileUrl
	 * @return
	 */
	public String getImgUrl(String fileUrl) {
		System.out.println(fileUrl);
		if (!StringUtils.isEmpty(fileUrl)) {
			String[] split = fileUrl.split("/");
			return AilyunOSSUtil.getUrl(split[split.length - 1]);
		}
		return null;
	}

	/* 返回文件的BASE64 编码 */
	@SuppressWarnings({ "restriction", "deprecation" })
	public static String getBaseFile(String file) throws Exception {
		// 指定过期时间为一年。
		Date expiration = new Date(new Date().getTime() + 3600L * 1000 * 24 * 365);
		GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucketName, file, HttpMethod.GET);
		req.setExpiration(expiration);
		OSSClient ossClient= new OSSClient(endpoint, accessKeyId, accessKeySecret);
		URL signedUrl = ossClient.generatePresignedUrl(req);
//	        2,得到HttpClient对象
		HttpClient client = new DefaultHttpClient();
		// 3,设置请求方式
		HttpGet get = new HttpGet(signedUrl.toString());
		// 4,执行请求, 获取响应信息
		HttpResponse response = client.execute(get);
		if (response.getStatusLine().getStatusCode() == 200) {
			// 得到实体
			HttpEntity entity = response.getEntity();
			byte[] data = EntityUtils.toByteArray(entity);
			BASE64Encoder encoder = new BASE64Encoder();
			// 返回Base64编码过的字节数组字符串
			String encode = encoder.encode(data);
			return encode;
		} else {
			return null;
		}
	}

	/*
	 * 文件形式上传
	 * 
	 * @author ZhangYin
	 * 
	 * @param fileName保存的路径文件名
	 * 
	 * @param file 要上传的文件
	 * 
	 * @return java.lang.String
	 */
	public static String uploadFileOSSFile(String fileName, File file) {
		// 创建PutObjectRequest对象。
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, file);
		// 上传文件。
		OSSClient ossClient= new OSSClient(endpoint, accessKeyId, accessKeySecret);
		PutObjectResult putResult = ossClient.putObject(putObjectRequest);
		String ret = putResult.getETag();
		return ret;
	}

	/**
	 * Description: 判断OSS服务文件上传时文件的contentType
	 *
	 * @param
	 * @return String
	 */
	public static String getcontentType(String filenameExtension) {
		if (filenameExtension.equalsIgnoreCase("bmp")) {
			return "image/bmp";
		}
		if (filenameExtension.equalsIgnoreCase("gif")) {
			return "image/gif";
		}
		if (filenameExtension.equalsIgnoreCase("jpeg") || filenameExtension.equalsIgnoreCase("jpg")
				|| filenameExtension.equalsIgnoreCase("png")) {
			return "image/jpeg";
		}
		if (filenameExtension.equalsIgnoreCase("html")) {
			return "text/html";
		}
		if (filenameExtension.equalsIgnoreCase("txt")) {
			return "text/plain";
		}
		if (filenameExtension.equalsIgnoreCase("vsd")) {
			return "application/vnd.visio";
		}
		if (filenameExtension.equalsIgnoreCase("pptx") || filenameExtension.equalsIgnoreCase("ppt")) {
			return "application/vnd.ms-powerpoint";
		}
		if (filenameExtension.equalsIgnoreCase("docx") || filenameExtension.equalsIgnoreCase("doc")) {
			return "application/msword";
		}
		if (filenameExtension.equalsIgnoreCase("xml")) {
			return "text/xml";
		}
		return "image/jpeg";
	}

}
