package com.h3w.utils.TimerTask;

import com.h3w.utils.TimerTask.TimerManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by 60292 on 2017/11/24.
 */
public class NFDFlightDataTaskListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        new TimerManager();
    }

    public void contextDestroyed(ServletContextEvent sce) {
        // TODO Auto-generated method stub

    }

}
