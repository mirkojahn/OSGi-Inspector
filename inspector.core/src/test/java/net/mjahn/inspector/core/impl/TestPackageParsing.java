package net.mjahn.inspector.core.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestPackageParsing {

  @Test
  public void testImportedPackageString01() throws Exception {
    String packageString = "net.mjahn.inspector.core;version=\"1.0.0\",net.mjahn.inspector.core.impl";
    String[] parsed = Util.parseDelimitedString(packageString, ",");
    for (int i = 0; i < parsed.length; i++) {
      System.out.println(parsed[i]);
    }
    assertTrue(parsed.length != 0);
  }

  @Test
  public void testImportedPackageString02() throws Exception {
    String packageString = "net.mjahn.inspector.core;version=\"1.0.0\",net.mjahn.inspector.core.impl,org.osgi.framework;version=\"[1.3.0,2.0.0)\";resolution:=optional";
    Object[][][] parsed = Util.parseStandardHeader(packageString);

    assertTrue(parsed.length != 0);
  }

  @Test
  public void testExportedPackageString01() throws Exception {
    String packageString = "net.mjahn.inspector.core;uses:=\"org.osgi.framework,org.osgi.framework.hooks.service\";version=\"1.0.0\",org.osgi.service.framework; version=\"1.0\"; x-internal:=true";
    Object[][][] parsed = Util.parseStandardHeader(packageString);

    assertTrue(parsed.length != 0);
  }
}
