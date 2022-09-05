package com.sadkoala.arbitrator.atworker;

import java.net.http.WebSocket;
import java.sql.Connection;

public class GlobalResources {

    public static final String APPLICATION_PROPERTY_NAME__WORKER_DB_FILE_PATH = "arbitrator_worker_db_file_path";
    public static final String APPLICATION_PROPERTY_NAME__MONITOR_DB_FILE_PATH = "arbitrator_monitor_db_file_path";


    public static final String PARAMS_FILE_PATH = "atworker.properties";
    public static final String ATWORKER_ACTIVE_FILE_PATH = ".atworker_active";
    public static final String ATWORKER_STOP_FILE_PATH = ".atworker_stop";
    public static Connection monitorDbConnection = null;
    public static Connection workerDbConnection = null;
    public static WebSocket webSocket = null;
}
