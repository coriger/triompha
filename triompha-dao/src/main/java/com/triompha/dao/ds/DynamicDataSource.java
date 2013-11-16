package com.triompha.dao.ds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
    private static Logger logger = LoggerFactory.getLogger(DynamicDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        String datasourceName = DdsContext.getDataSource();
        logger.debug("current datasourceName is: " + datasourceName);
        return datasourceName;
    }

}
