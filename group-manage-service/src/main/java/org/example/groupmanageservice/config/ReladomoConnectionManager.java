package org.example.groupmanageservice.config;

import com.gs.fw.common.mithra.connectionmanager.XAConnectionManager;
import com.gs.fw.common.mithra.bulkloader.BulkLoader;
import com.gs.fw.common.mithra.bulkloader.BulkLoaderException;
import com.gs.fw.common.mithra.connectionmanager.SourcelessConnectionManager;
import com.gs.fw.common.mithra.databasetype.DatabaseType;
import com.gs.fw.common.mithra.databasetype.OracleDatabaseType;

import java.util.Properties;
import java.util.TimeZone;

import org.example.groupmanageservice.exception.ResourceNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;

public class ReladomoConnectionManager implements SourcelessConnectionManager
{
    protected static ReladomoConnectionManager instance;

    protected static final String MAX_POOL_SIZE_KEY = "maxPoolSize";
    protected final int DEFAULT_MAX_WAIT = 500;
    protected static final int DEFAULT_POOL_SIZE = 10;
    private static final TimeZone NEW_YORK_TIMEZONE = TimeZone.getTimeZone("America/New_York");

    private XAConnectionManager xaConnectionManager;

    public static synchronized ReladomoConnectionManager getInstance()
    {
        if (instance == null)
        {
            instance = new ReladomoConnectionManager();
        }
        return instance;
    }
    protected ReladomoConnectionManager()
    {
        this.createConnectionManager();
    }

    private XAConnectionManager createConnectionManager()
    {
        Properties properties = new Properties();
        InputStream inputStream = ReladomoConnectionManager.class
                .getResourceAsStream("/application.properties");

        if (inputStream == null) {
            throw new ResourceNotFoundException("cannot find application.properties");
        }

        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        xaConnectionManager = new XAConnectionManager();
        xaConnectionManager.setMaxWait(DEFAULT_MAX_WAIT);
        xaConnectionManager.setJdbcConnectionString(properties.get("spring.datasource.url").toString());
        xaConnectionManager.setDriverClassName(properties.getProperty("reladomo.database.driver"));
        // xaConnectionManager.setHostName(properties.getProperty("reladomo.database.url"));
        // xaConnectionManager.setPort(Integer.parseInt(properties.getProperty("reladomo.database.port")));
        xaConnectionManager.setJdbcUser(properties.getProperty("reladomo.database.username"));
        xaConnectionManager.setJdbcPassword(properties.getProperty("reladomo.database.password"));
        xaConnectionManager.setPoolName("myproj connection pool");
        xaConnectionManager.setInitialSize(1);
        xaConnectionManager.setPoolSize(DEFAULT_POOL_SIZE);
        xaConnectionManager.setDefaultSchemaName(properties.getProperty("reladomo.database.schema"));
        xaConnectionManager.initialisePool();
        return xaConnectionManager;
    }

    public Connection getConnection()
    {
        return xaConnectionManager.getConnection();
    }

    public DatabaseType getDatabaseType()
    {
        return OracleDatabaseType.getInstance();
    }

    public TimeZone getDatabaseTimeZone()
    {
        return NEW_YORK_TIMEZONE;
    }

    public BulkLoader createBulkLoader() throws BulkLoaderException
    {
        return this.getDatabaseType().createBulkLoader(
                    "sa",
                    "",
                    this.xaConnectionManager.getHostName(),
                    this.xaConnectionManager.getPort());
    }

    public String getDatabaseIdentifier()
    {
        return xaConnectionManager.getLdapName()+":"+xaConnectionManager.getDefaultSchemaName();
    }
}

