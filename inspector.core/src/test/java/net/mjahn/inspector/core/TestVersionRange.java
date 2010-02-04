package net.mjahn.inspector.core;

import junit.framework.TestCase;

import org.osgi.framework.Version;

public class TestVersionRange extends TestCase{
	
	public void testVersionWithinRange01() throws Exception {
		VersionRange vr1 = new VersionRange("[1.4.4,4.2.1)");
		Version v1 = new Version("2.3.2.xlf");
		assertFalse("a version could never be within a range", vr1.contains(v1)); 
	}
	
	public void testVersionWithinRange02() throws Exception {
		VersionRange vr1 = new VersionRange("[1.4.4,4.2.1)");
		Version v1 = new Version("1.4.4");
		assertFalse("a version could never be within a range",vr1.contains(v1));
	}
	
	public void testVersionWithinRange03() throws Exception {
		VersionRange vr1 = new VersionRange("[1.4.0,4.2.1)");
		Version v1 = new Version("1.4");
		assertFalse("a version could never be within a range", vr1.contains(v1));
	}

	public void testVersionWithinRange04() throws Exception {
		VersionRange vr1 = new VersionRange("[1.4,4.2.1)");
		Version v1 = new Version("1.4.0");
		assertFalse("a version could never be within a range", vr1.contains(v1));
	}
	public void testVersionWithinRange05() throws Exception {
		VersionRange vr1 = new VersionRange("[1.4.4,4.2.1)");
		Version v1 = new Version("4.2.1");
		assertFalse("a version could never be within a range", vr1.contains(v1));
	}
	
	
	public void testVersionWithinRange06() throws Exception {
		VersionRange vr1 = new VersionRange("[1.4.4,4.2.1)");
		Version v1 = new Version("4.2.0.test");
		assertFalse("a version could never be within a range", vr1.contains(v1));
	}
	
	public void testVersionWithinRange07() throws Exception {
		VersionRange vr1 = new VersionRange("[1.4.4,4.2.1.SNAPSHOT)");
		Version v1 = new Version("4.2.1");
		assertFalse("a version could never be within a range", vr1.contains(v1));
	}
	
	public void testVersionWithinRange08() throws Exception {
		VersionRange vr1 = new VersionRange("1.4.4");
		Version v1 = new Version("4.2.1");
		assertTrue("always within range if single version is lower than the included one", vr1.contains(v1));
	}
	
	public void testVersionWithinRange09() throws Exception {
		VersionRange vr1 = new VersionRange("1.4.4");
		Version v1 = new Version("1.4.4");
		assertTrue("always within range if single version is lower or equal than the included one", vr1.contains(v1));
	}
	
	public void testVersionWithinRange10() throws Exception {
		VersionRange vr1 = new VersionRange("1.4.0");
		Version v1 = new Version("1.4");
		assertTrue("always within range if single version is lower or equal than the included one", vr1.contains(v1));
	}
	
	public void testVersionWithinRange11() throws Exception {
		VersionRange vr1 = new VersionRange("1.4");
		Version v1 = new Version("1.4.0");
		assertTrue("always within range if single version is lower or equal than the included one", vr1.contains(v1));
	}
	
	public void testVersionWithinRange12() throws Exception {
		VersionRange vr1 = new VersionRange("1.4.4");
		Version v1 = new Version("1.4.4.t");
		assertTrue("Beware of String compare on quantifier", vr1.contains(v1));
	}
	
	public void testVersionWithinRange13() throws Exception {
		VersionRange vr1 = new VersionRange("1.4.4.t");
		Version v1 = new Version("1.4.4");
		assertFalse("Beware of String compare on quantifier", vr1.contains(v1));
	}	
	
	public void testVersionWithinRange14() throws Exception {
		VersionRange vr1 = new VersionRange("1.4.4.a");
		Version v1 = new Version("1.4.4.t");
		assertTrue("Beware of String compare on quantifier", vr1.contains(v1));
	}	
	
	public void testVersionRangeWithinRange01() throws Exception {
		VersionRange vr1 = new VersionRange("[1.4.4,4.2.1.SNAPSHOT)");
		VersionRange vr2 = new VersionRange("[1.4.4,4.2.1.SNAPSHOT)");
		assertTrue("two equal VersionRanges should always be within range",vr1.contains(vr2));
	}
	
	public void testVersionRangeWithinRange02() throws Exception {
		VersionRange vr1 = new VersionRange("[1.4.0,4.2.1.SNAPSHOT)");
		VersionRange vr2 = new VersionRange("[1.4,4.2.1.SNAPSHOT)");
		assertTrue("two equal VersionRanges should always be within range",vr1.contains(vr2));
	}
	
	public void testVersionRangeWithinRange03() throws Exception {
		VersionRange vr1 = new VersionRange("[1.4.0,4.2.1.SNAPSHOT)");
		VersionRange vr2 = new VersionRange("[1.4,4.2.1.SNAPSHOT]");
		assertFalse("the upper edge is NOT within the range",vr1.contains(vr2));
	}
	
	public void testVersionRangeWithinRange04() throws Exception {
		VersionRange vr1 = new VersionRange("[1.4.0,4.2.1.SNAPSHOT]");
		VersionRange vr2 = new VersionRange("[1.4,4.2.1.SNAPSHOT]");
		assertTrue("two equal VersionRanges should always be within range",vr1.contains(vr2));
	}
	
	public void testVersionRangeWithinRange05() throws Exception {
		VersionRange vr1 = new VersionRange("(1.4.0,4.2.1.SNAPSHOT)");
		VersionRange vr2 = new VersionRange("[1.4,4.2.1.SNAPSHOT)");
		assertFalse("the lower edge is NOT within the range",vr1.contains(vr2));
	}
	
	public void testVersionRangeWithinRange06() throws Exception {
		VersionRange vr1 = new VersionRange("1.4.0");
		VersionRange vr2 = new VersionRange("[1.4,4.2.1.SNAPSHOT]");
		assertTrue("a single version descibes a range",vr1.contains(vr2));
	}
	
	public void testVersionRangeWithinRange07() throws Exception {
		VersionRange vr1 = new VersionRange("1.4.0");
		VersionRange vr2 = new VersionRange("(1.4,4.2.1.SNAPSHOT]");
		assertTrue("the border is NOT included!",vr1.contains(vr2));
	}
	
	// comparing ranges with versions
	public void testOverlappingRange01() throws Exception {
		VersionRange vr1 = new VersionRange("1.4.0");
		VersionRange vr2 = new VersionRange("(1.4,4.2.1.SNAPSHOT]");
		VersionRange got = vr1.getIntersectingRange(vr2);
		VersionRange expected = new VersionRange("(1.4.0,4.2.1.SNAPSHOT]");
		assertTrue("expected Version range "+expected+", but instead got "+got,got.equals(expected));
	}
	
	public void testOverlappingRange02() throws Exception {
		VersionRange vr1 = new VersionRange("1.4.0");
		VersionRange vr2 = new VersionRange("[1.4,4.2.1.SNAPSHOT]");
		VersionRange got = vr1.getIntersectingRange(vr2);
		VersionRange expected = new VersionRange("[1.4.0,4.2.1.SNAPSHOT]");
		assertTrue("expected Version range "+expected+", but instead got "+got,got.equals(expected));
	}
	
	public void testOverlappingRange03() throws Exception {
		VersionRange vr1 = new VersionRange("1.4.0");
		VersionRange vr2 = new VersionRange("(1.3,4.2.1]");
		VersionRange got = vr1.getIntersectingRange(vr2);
		VersionRange expected = new VersionRange("[1.4.0,4.2.1]");
		assertTrue("expected Version range "+expected+", but instead got "+got,got.equals(expected));
	}
	
	public void testOverlappingRange04() throws Exception {
		VersionRange vr1 = new VersionRange("(1.4,4.2.1.SNAPSHOT]");
		VersionRange vr2 = new VersionRange("1.4.0");
		VersionRange got = vr1.getIntersectingRange(vr2);
		VersionRange expected = new VersionRange("(1.4.0,4.2.1.SNAPSHOT]");
		assertTrue("expected Version range "+expected+", but instead got "+got,got.equals(expected));
	}
	
	public void testOverlappingRange05() throws Exception {
		VersionRange vr1 = new VersionRange("[1.4,4.2.1.SNAPSHOT]");
		VersionRange vr2 = new VersionRange("1.4.0");
		VersionRange got = vr1.getIntersectingRange(vr2);
		VersionRange expected = new VersionRange("[1.4.0,4.2.1.SNAPSHOT]");
		assertTrue("expected Version range "+expected+", but instead got "+got,got.equals(expected));
	}
	
	public void testOverlappingRange06() throws Exception {
		VersionRange vr1 = new VersionRange("(1.3,4.2.1]");
		VersionRange vr2 = new VersionRange("1.4.0");
		VersionRange got = vr1.getIntersectingRange(vr2);
		VersionRange expected = new VersionRange("[1.4.0,4.2.1]");
		assertTrue("expected Version range "+expected+", but instead got "+got,got.equals(expected));
	}

	// comparing single versions
	public void testOverlappingRange07() throws Exception {
		VersionRange vr1 = new VersionRange("1.4.3");
		VersionRange vr2 = new VersionRange("1.4");
		VersionRange got = vr1.getIntersectingRange(vr2);
		VersionRange expected = new VersionRange("1.4.3");
		assertTrue("expected Version range "+expected+", but instead got "+got,got.equals(expected));
	}
	
	public void testOverlappingRange08() throws Exception {
		VersionRange vr1 = new VersionRange("1.4");
		VersionRange vr2 = new VersionRange("1.4.3");
		VersionRange got = vr1.getIntersectingRange(vr2);
		VersionRange expected = new VersionRange("1.4.3");
		assertTrue("expected Version range "+expected+", but instead got "+got,got.equals(expected));
	}
	
	// comparing ranges
	public void testOverlappingRange09() throws Exception {
		VersionRange vr1 = new VersionRange("(1.4,4.2.1.SNAPSHOT]");
		VersionRange vr2 = new VersionRange("(1.4,4.2.1.SNAPSHOT]");
		VersionRange got = vr1.getIntersectingRange(vr2);
		VersionRange expected = new VersionRange("(1.4.0,4.2.1.SNAPSHOT]");
		assertTrue("expected Version range "+expected+", but instead got "+got,got.equals(expected));
	}

	public void testOverlappingRange10() throws Exception {
		VersionRange vr1 = new VersionRange("(1.4,4.2.1.SNAPSHOT]");
		VersionRange vr2 = new VersionRange("(1.3,4.2.1]");
		VersionRange got = vr1.getIntersectingRange(vr2);
		VersionRange expected = new VersionRange("(1.4.0,4.2.1]");
		assertTrue("expected Version range "+expected+", but instead got "+got,got.equals(expected));
	}

	public void testOverlappingRange11() throws Exception {
		VersionRange vr1 = new VersionRange("[1.4,4.2.1.SNAPSHOT]");
		VersionRange vr2 = new VersionRange("(1.3,4.2.1.Test]");
		VersionRange got = vr1.getIntersectingRange(vr2);
		VersionRange expected = new VersionRange("[1.4.0,4.2.1.SNAPSHOT]");
		assertTrue("expected Version range "+expected+", but instead got "+got,got.equals(expected));
	}

	public void testOverlappingRange12() throws Exception {
		VersionRange vr1 = new VersionRange("(1.4,4.2.1.SNAPSHOT]");
		VersionRange vr2 = new VersionRange("(1.3,4.2.1)");
		VersionRange got = vr1.getIntersectingRange(vr2);
		VersionRange expected = new VersionRange("(1.4.0,4.2.1)");
		assertTrue("expected Version range "+expected+", but instead got "+got,got.equals(expected));
	}
	
	public void testOverlappingRange13() throws Exception {
		VersionRange vr1 = new VersionRange("[1.4,4.2.1.SNAPSHOT]");
		VersionRange vr2 = new VersionRange("(1.3,1.4]");
		VersionRange got = vr1.getIntersectingRange(vr2);
		VersionRange expected = new VersionRange("[1.4.0,1.4]");
		assertTrue("expected Version range "+expected+", but instead got "+got,got.equals(expected));
	}
	
	public void testOverlappingRange14() throws Exception {
		VersionRange vr1 = new VersionRange("(1.3,1.4]");
		VersionRange vr2 = new VersionRange("[1.4,4.2.1.SNAPSHOT]");
		VersionRange got = vr1.getIntersectingRange(vr2);
		VersionRange expected = new VersionRange("[1.4.0,1.4]");
		assertTrue("expected Version range "+expected+", but instead got "+got,got.equals(expected));
	}
	
	// not intersecting
	public void testOverlappingRange15() throws Exception {
		VersionRange vr1 = new VersionRange("(1.3,1.4]");
		VersionRange vr2 = new VersionRange("(1.4,4.2.1.SNAPSHOT]");
		VersionRange got = vr1.getIntersectingRange(vr2);
		VersionRange expected = null;
		assertTrue("expected Version range "+expected+", but instead got "+got,got == expected);
	}
	public void testOverlappingRange16() throws Exception {
		VersionRange vr1 = new VersionRange("(1.4,4.2.1.SNAPSHOT]");
		VersionRange vr2 = new VersionRange("(1.3,1.4]");
		VersionRange got = vr1.getIntersectingRange(vr2);
		VersionRange expected = null;
		assertTrue("expected Version range "+expected+", but instead got "+got,got == expected);
	}
}
