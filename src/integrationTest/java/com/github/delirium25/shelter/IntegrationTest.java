package com.github.delirium25.shelter;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryScanConsistency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ShelterApplication.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class IntegrationTest {

    @Autowired
    private Cluster cluster;

    @BeforeEach
    void cleanUp() {
        cluster.query("DELETE from `shelter`", QueryOptions.queryOptions()
                .scanConsistency(QueryScanConsistency.REQUEST_PLUS).asTransaction());
    }
}
