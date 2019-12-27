package com.sztvis.push.util;

import com.sztvis.push.socket.EchoServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class SpringContextUtil implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(SpringContextUtil.class);

    private static ApplicationContext applicationContext = null;
    private ExecutorService executorService;

    private EchoServer echoServer;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
        System.out.println("ApplicationContextAware");
        ThreadFactory namedThreadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "ExecutorService-%d");
            }
        };
        executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(1024),
                namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
        log.info("ExecutorService init.");
        log.info("tomcat is going start");
        log.info("echoServer is ready");
        echoServer = (EchoServer) SpringContextUtil.getBean("echoServer");
        executorService.execute(() -> {
            try {
                log.info("runing......");
                echoServer.run();
            } catch (Exception e) {
                log.error("socket listen an serve errror.", e);
            }
        });
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String beanName){
        return applicationContext.getBean(beanName);
    }

    public static Object getBean(Class cls){
        return applicationContext.getBean(cls);
    }
}
