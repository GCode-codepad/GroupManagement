package org.example.groupmanageservice.config;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.gs.fw.common.mithra.MithraManager;
import com.gs.fw.common.mithra.MithraManagerProvider;

import jakarta.annotation.PostConstruct;

@Configuration
public class ReladomoConfig
{
    @Value("${reladomo.database.driver}")
    String driverClassname;

    @Value("${reladomo.database.url}")
    String serverName;

    @Value("${reladomo.database.schema}")
    String resourceName;

    @Value("${reladomo.database.port}")
    int port;

    @Value("${reladomo.database.username}")
    String user;

    @Value("${reladomo.database.password}")
    String password;

    @PostConstruct
    public void initReladomo() throws Exception
    {
        // Acquire the manager
        MithraManager mithraManager = MithraManagerProvider.getMithraManager();

        mithraManager.setTransactionTimeout(30 * 1000);

        String mithraConfigXml = "reladomo/MithraRuntimeConfig.xml";

        InputStream stream = loadReladomoXMLFromClasspath(mithraConfigXml);

        // Initialize the runtime from the XML
        mithraManager.readConfiguration(stream);

        stream.close();
    }

    private InputStream loadReladomoXMLFromClasspath(String fileName) throws Exception
    {
        InputStream stream = ReladomoConfig.class
                                .getClassLoader().getResourceAsStream(fileName);
        if (stream == null)
        {
            throw new Exception("Failed to locate " + fileName + " in classpath");
        }
        return stream;
    }
}
