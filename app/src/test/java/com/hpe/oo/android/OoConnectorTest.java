package com.hpe.oo.android;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by revnic on 5/21/2016.
 */
public class OoConnectorTest {
    private static OoConnector sMOoConnector;

    @BeforeClass
    public static void init() {
        //sMOoConnector = new OoConnector("http://16.60.160.67:8080/oo/rest/v2");
    }

    @Test
    public void getRunsTest() throws Exception {
        sMOoConnector.searchRuns("");
    }
}
