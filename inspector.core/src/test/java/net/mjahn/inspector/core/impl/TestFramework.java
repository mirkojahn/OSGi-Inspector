package net.mjahn.inspector.core.impl;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.ops4j.pax.exam.CoreOptions.equinox;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.net.URL;
import java.util.Enumeration;

import net.mjahn.inspector.core.reasoner.base.Utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.TestAddress;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.ops4j.pax.exam.testforge.SingleClassProvider;
import org.osgi.framework.BundleContext;
import org.slf4j.LoggerFactory;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy( AllConfinedStagedReactorFactory.class )
public class TestFramework {

	// @Inject BundleContext context;

	// @Configuration
	// public static Option[] config2() {
	// return options(
	// mavenConfiguration(),
	// // cleanCaches(),
	// provision(mavenBundle().groupId("net.mjahn").artifactId(
	// "inspector.core")),
	// new TestContainerStartTimeoutOption(1000l));
	// }
	@Configuration()
	public Option[] config() {
		return options(junitBundles(), felix(), equinox());
	}

//	@Test
//	public void simpliestTest() {
//		System.out.println("************ Hello from OSGi ************");
//	}

	@Test
	public void withBC(BundleContext ctx) {
		// make sure, we have a context
		assertThat(ctx, is(notNullValue()));
		System.out.println("BundleContext of bundle injected: "
				+ ctx.getBundle().getSymbolicName());

	}

	@Test
	public void withBC2(BundleContext ctx) {
		// make sure, we have a context
		assertThat(ctx, is(notNullValue()));
		String className = this.getClass().getName();
		// format it to find a class
		String newClassName = className.replace(".", "/")+".class";
		
//		System.out.println("*************   Trying to find class with name: "+newClassName);
		String[] packageAndClass = splitPackageAndClassForSearch(className);
		
//		System.out.println("*************   package: "+packageAndClass[0] + ", class: " + packageAndClass[1]);
		URL url = ctx.getBundle().getEntry(packageAndClass[2]);
		
		Assert.assertNotNull(url);

	}

	@Test
	public TestAddress prebuilt(TestProbeBuilder builder) {
		return builder.addTest(SingleClassProvider.class,
				(Object) LoggerFactory.class.getName());
	}
	
	public static String[] splitPackageAndClassForSearch(String fullQualifiedClassName){
		String fqcn = fullQualifiedClassName.replace(".", "/");
		String[] temp = fqcn.split("/");
		
		// get the className
		String className = temp[temp.length-1];
		if(!className.endsWith("class")){
			// add ".class" to be able to find it
			className+=".class";
			fqcn+=".class";
		}
		
		// get the packageName
		String packageName = "";
		if(temp.length >0){
			int i = fqcn.indexOf(temp[temp.length-1]);
			if(i>0){
				// only in case a package was found (this should always be true due to the previous check
				packageName = fqcn.substring(0, i-1); // don't include the "/"
			}
		}
		return new String[] {packageName, className, fqcn, fullQualifiedClassName};
	}
}
