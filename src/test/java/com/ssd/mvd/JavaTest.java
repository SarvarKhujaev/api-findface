package com.ssd.mvd;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith( value = Suite.class )
@Suite.SuiteClasses ( value = {
        KafkaConnectionTest.class,
        GaiServiceAPICheckTests.class,
        OvirServiceAPICheckTests.class,
} )
public final class JavaTest {
}
