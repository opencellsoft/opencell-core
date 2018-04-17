package org.meveo.admin.ftp;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.impl.DefaultFtpServer;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.service.admin.impl.UserService;
import org.slf4j.Logger;

/**
 * 
 * @author Tyshan Shi
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 *
 */
// @Startup
@Singleton
public class MeveoMinaFTPListener {
    @Inject
    private Logger log;
    @Inject
    private UserService userService;

    @Inject
    private MeveoDefaultFtplet meveoDefaultFtplet;

    private FtpServer server = null;

    @PostConstruct
    public void init() {
        String portStr = ParamBeanFactory.getAppScopeInstance().getProperty("ftpserver.port", null);
        if (StringUtils.isBlank(portStr)) {
            return;
        }
        Integer port = null;
        try {
            port = Integer.parseInt(portStr);
        } catch (Exception e) {
        }
        if (port == null) {
            log.info("meveo ftp server doesn't start with port {}", portStr);
            return;
        }
        FtpServerFactory serverFactory = new FtpServerFactory();

        ListenerFactory factory = new ListenerFactory();

        // set the port of the listener
        factory.setPort(port);

        serverFactory.addListener("default", factory.createListener());
        serverFactory.getFtplets().put("meveoFtplet", meveoDefaultFtplet);
        MeveoFtpUserManagerFactory managerFactory = new MeveoFtpUserManagerFactory(userService);
        UserManager userManager = managerFactory.createUserManager();
        serverFactory.setUserManager(userManager);

        // start the server
        server = serverFactory.createServer();
        try {
            server.start();
        } catch (FtpException e) {
            throw new RuntimeException(e);
        }
        log.debug("start meveo ftp server ...");
    }

    @PreDestroy
    public void stopServer() {
        if (server != null) {
            DefaultFtpServer defaultServer = (DefaultFtpServer) server;
            FtpServerContext serverContext = defaultServer.getServerContext();
            Map<String, Listener> listeners = serverContext.getListeners();
            for (Listener listener : listeners.values()) {
                listener.stop();
            }
            if (serverContext != null) {
                serverContext.dispose();
                serverContext = null;
            }
        }
        log.debug("ftp server is stopped!");
    }
}
