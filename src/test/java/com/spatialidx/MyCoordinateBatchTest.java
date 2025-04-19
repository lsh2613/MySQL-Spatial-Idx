package com.spatialidx;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MyCoordinateBatchTest {
    @Autowired
    private MyCoordinateBatchUtil myCoordinateBatchUtil;

    @Test
    void testSetupData() {
        int totalDataCnt = 10000;
        int queryHit = 200;

        myCoordinateBatchUtil.setupData(totalDataCnt, queryHit);
    }

    @Test
    void testCleanupData() {
        myCoordinateBatchUtil.cleanupData();
    }
}
