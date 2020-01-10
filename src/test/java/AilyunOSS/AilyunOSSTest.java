package AilyunOSS;

import java.io.File;

import org.junit.Test;

import com.zhy.aliyunOSS.util.AilyunOSSUtil;


public class AilyunOSSTest {
	
	@Test
public void OSSUploadFile() {
		//上传文件
		File file =new File("C:\\Users\\user\\Downloads\\1211567555763150849 (1).jpeg");
		AilyunOSSUtil.uploadFileOSSFile("image/img.jpeg", file);
}
	@Test
public void OSSBase64() {
		//获取BASE64
		try {
			System.out.print(AilyunOSSUtil.getBaseFile("image/img.jpeg"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
	@Test
public void OSSUrl() {
		//获取BASE64
		try {
			System.out.print(AilyunOSSUtil.getUrl("image/img.jpeg"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
}
