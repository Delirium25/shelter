package com.github.delirium25.shelter.config;

import com.couchbase.client.java.query.QueryScanConsistency;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;

@Configuration
@EnableCouchbaseRepositories(basePackages = {"com.github.delirium25.shelter"})
public class CouchbaseConfig extends AbstractCouchbaseConfiguration {
    @Override
    public String getConnectionString() {
        return "couchbase://127.0.0.1";
    }

    @Override
    public String getUserName() {
        //TODO: Move to config
        return "shelter";
    }

    @Override
    public String getPassword() {
        //TODO: Move to config
        return "shelter";
    }

    @Override
    public String getBucketName() {
        //TODO: Move to config
        return "shelter";
    }

    @Override
    public QueryScanConsistency getDefaultConsistency() {

        return QueryScanConsistency.REQUEST_PLUS;
    }
}
