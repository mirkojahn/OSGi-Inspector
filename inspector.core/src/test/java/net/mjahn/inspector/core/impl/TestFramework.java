package net.mjahn.inspector.core.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.options.TestContainerStartTimeoutOption;
import org.osgi.framework.BundleContext;
import static org.ops4j.pax.exam.CoreOptions.*;

//@RunWith( JUnit4TestRunner.class )
public class TestFramework {

	
//	@Inject BundleContext context;

//    @Configuration
    public static Option[] config()
    {
        return options(
        	mavenConfiguration(),
//        	cleanCaches(),
        	provision(mavenBundle().groupId("net.mjahn").artifactId("inspector.core")),
        	new TestContainerStartTimeoutOption(1000l)
        );
    }
	
	@Test
    public void simpliestTest()
    {
        System.out.println( "************ Hello from OSGi ************" );
    }
}
