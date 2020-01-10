package com.zhy.email.entity;


import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * @Description: 邮箱授权类
 * @Author: ZhangYin
 * @Version: V1.0
 */
    public class MyAuthenticator extends Authenticator {
        String userName=null;
        String password=null;
        public MyAuthenticator(){

        }
        public MyAuthenticator(String username, String password) {
            this.userName = username;
            this.password = password;
        }
        protected PasswordAuthentication getPasswordAuthentication(){
            return new PasswordAuthentication(userName, password);
        }
    }

