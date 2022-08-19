package com.sadkoala.arbitrator.atworker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GlobalTest {

    @Test
    public void findGlobalObjectsTest() {
        Assertions.assertTrue(Global.findTokenByName("USDT").isPresent());
        Assertions.assertTrue(Global.findTokenByName("BTC").isPresent());
        Assertions.assertTrue(Global.findTokenByName("LTC").isPresent());

        Assertions.assertTrue(Global.findPairByTokenNames("BTC", "USDT").isPresent());
        Assertions.assertTrue(Global.findPairByTokenNames("USDT", "BTC", true).isEmpty());
        Assertions.assertTrue(Global.findPairByTokenNames("USDT", "LTC").isPresent());

        Assertions.assertEquals(5, Global.routeList.size());
    }

}
