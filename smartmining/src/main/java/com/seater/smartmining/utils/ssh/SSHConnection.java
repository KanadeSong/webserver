package com.seater.smartmining.utils.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.util.Properties;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/13 0013 20:56
 */
public class SSHConnection {

    // 自定义的中转接口，需要和数据源接口设置一样
    private final static int LOCAl_PORT = 3310;
    // 服务端的数据库端口
    private final static int REMOTE_PORT = 3306;
    // 服务器端SSH端口 默认是22
    private final static int SSH_REMOTE_PORT = 22;
    // SSH用户名
    private final static String SSH_USER = "root";
    // SSH使用密码
    private final static String SSH_PASSWORD = "d#G4H^m*QD#V$T@";
    // 连接到哪个服务端的SSH
    private final static String SSH_REMOTE_SERVER = "118.190.210.100";
    // 服务端的本地mysql服务
    private final static String MYSQL_REMOTE_SERVER = "127.0.0.1";
    private Session sesion; //represents each ssh session

    public void closeSSH ()
    {
        sesion.disconnect();
    }

    public SSHConnection () throws Throwable {
        JSch jsch  = new JSch();
        sesion = jsch.getSession(SSH_USER, SSH_REMOTE_SERVER, SSH_REMOTE_PORT);
        sesion.setPassword(SSH_PASSWORD);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        sesion.setConfig(config);
        sesion.connect();
        sesion.setPortForwardingL(LOCAl_PORT, MYSQL_REMOTE_SERVER, REMOTE_PORT);

    }
}
