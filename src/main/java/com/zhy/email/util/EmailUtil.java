package com.zhy.email.util;

import com.sun.mail.util.MailSSLSocketFactory;

import com.zhy.email.entity.MyAuthenticator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * 邮件发送工具类。 以下邮件中的配置参数，请在实际环境中，根据需要采取合适的配置方式。 发送邮件依赖 com.sun.mail（1.6.1）
 * 包、javax.mail（1.5.0-b01） 包。 如果使用 Idea 运行，请将这两个包（可以直接到Maven目录下面去找）添加到项目的
 * Libraries 里面（快捷键：Ctrl + Alt + Shift + S）
 *
 * @author zhangyin
 */
public class EmailUtil {

	/* 发件人别名（可以为空） */
	private final static String fromAliasName = "我";

	/* 登录用户名 */
	private static String ACCOUNT = "";// 邮箱

	/* 登录密码 */
	private static String PASSWORD = "";// 邮箱授权码

	/* 网易邮箱 
	 * 	服务器名称 			地址				SSl 		非SSl
	 * 	IMAP 		imap.163.com 		993 		143
	 * 	SMTP		smtp.163.com		465/994		25(阿里云服务器禁用25端口)
	 * 	POP3		pop.163.com			995			110
	 *
	 **/
	/* 邮件服务器地址 */
	private static String HOST = "smtp.163.com";
	/* 发信端口 */
	private static String PORT = "465";
	/* 发信协议 */
	private final static String PROTOCOL = "ssl";


	/**
	*<p>Title: send</p>
	*<p>Description:邮件发送工具类</p>
	　 * @param to			收件人
	　 * @param subject	主题
	　 * @param content	内容
	　 * @param attachFileList	附件列表
	　 * @return
	*/
	public static boolean send(String to, String subject, String content, List<String> attachFileList) {
		// 设置邮件属性
		Properties prop = new Properties();
		prop.setProperty("mail.transport.protocol", PROTOCOL);
		prop.setProperty("mail.smtp.host", HOST);
		prop.setProperty("mail.smtp.port", PORT);
		prop.setProperty("mail.smtp.auth", "true");
		MailSSLSocketFactory sslSocketFactory = null;
		
		try {
			sslSocketFactory = new MailSSLSocketFactory();
			sslSocketFactory.setTrustAllHosts(true);
		} catch (GeneralSecurityException e1) {
			e1.printStackTrace();
		}
		
		if (sslSocketFactory == null) {
			System.err.println("开启 MailSSLSocketFactory 失败");
		} else {
			prop.put("mail.smtp.ssl.enable", "true");
			prop.put("mail.smtp.ssl.socketFactory", sslSocketFactory);
			// 创建邮件会话（注意，如果要在一个进程中切换多个邮箱账号发信，应该用 Session.getInstance）
			Session session = Session.getDefaultInstance(prop, new MyAuthenticator(ACCOUNT, PASSWORD));
			// 开启调试模式（生产环境中请不要开启此项）
			session.setDebug(true);
			try {
				MimeMessage mimeMessage = new MimeMessage(session);
				// 设置发件人别名（如果未设置别名就默认为发件人邮箱）
				mimeMessage.setFrom(new InternetAddress(ACCOUNT, fromAliasName));
				// 设置主题和收件人、发信时间等信息
				mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to, "收件人"));
				mimeMessage.setSubject(subject);
				mimeMessage.setSentDate(new Date());
				// 正文
				MimeBodyPart text = new MimeBodyPart();
				text.setContent("", "text/html;charset=utf-8"); // 注意编码问题/*这里正文内容 for img<img src='cid:img'><br/>*/
				// 描述数据关系
				MimeMultipart mm = new MimeMultipart();
				mm.addBodyPart(text);
				mm.setSubType("related");

				// 图片-文本--复合--转普通节点
				MimeBodyPart tex_image_tPart = new MimeBodyPart();
				tex_image_tPart.setContent(mm);

				// 如果有附件信息，则添加附件
				if (!attachFileList.isEmpty()) {
					Multipart multipart = new MimeMultipart();
					MimeBodyPart body = new MimeBodyPart();
					body.setContent(content, "text/html; charset=UTF-8");
					multipart.addBodyPart(body);
					// 添加所有附件（添加时判断文件是否存在）
					for (String filePath : attachFileList) {
						if (Files.exists(Paths.get(filePath))) {
							MimeBodyPart tempBodyPart = new MimeBodyPart();
							tempBodyPart.attachFile(filePath);
							multipart.addBodyPart(tempBodyPart);
						}
					}
					mimeMessage.setContent(multipart);
				} else {
	                Multipart multipart = new MimeMultipart();
	                MimeBodyPart body = new MimeBodyPart();
	                body.setContent(mm, "text/html; charset=UTF-8");
	                multipart.addBodyPart(body);
					MimeMultipart mimeMultipart1 = new MimeMultipart();
					mimeMultipart1.addBodyPart(tex_image_tPart);
					mimeMultipart1.setSubType("mixd");// 混合关系
					mimeMessage.setContent(mimeMultipart1, "text/html; charset=UTF-8");
					mimeMessage.setText(content);
				}
				// 开始发信
				mimeMessage.saveChanges();
				Transport.send(mimeMessage);
				return true;
			} catch (IOException | MessagingException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

}
