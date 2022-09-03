package com.sadkoala.arbitrator.atworker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GlobalTest {

    @Test
    public void findGlobalObjectsTest() {
        Assertions.assertTrue(Global.findTokenByName("USDT").isPresent());
        Assertions.assertTrue(Global.findTokenByName("BNB").isEmpty());
        Assertions.assertTrue(Global.findTokenByName("LTC").isPresent());

        Assertions.assertTrue(Global.findPairByTokenNames("BTC", "USDT").isPresent());
        Assertions.assertTrue(Global.findPairByTokenNames("USDT", "BTC", true).isEmpty());
        Assertions.assertTrue(Global.findPairByTokenNames("USDT", "LTC").isPresent());

        Assertions.assertTrue(Global.findRouteByTokenNames("USDT", "LTC", "BTC").isPresent());
        Assertions.assertTrue(Global.findRouteByTokenNames("BTC", "BNB", "ETH").isEmpty());
        Assertions.assertTrue(Global.findRouteByTokenNames("USDT", "ETH", "BTC").isPresent());
    }

}
