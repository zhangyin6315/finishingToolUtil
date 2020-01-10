package email;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.zhy.email.util.EmailUtil;

public class Testemail {
	@Test
public void email() {
	String to="1973354434@qq.com";//收件人
	String subject="主题";//主题
	String content="内容";//内容
	List<String> attachFileList=new ArrayList<String>();
	attachFileList.add("C:\\Users\\user\\Downloads\\1211567555763150849 (1).jpeg");
	EmailUtil.send(to, subject, content, attachFileList);
}
}
