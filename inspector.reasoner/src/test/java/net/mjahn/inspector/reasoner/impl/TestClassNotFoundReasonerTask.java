package net.mjahn.inspector.reasoner.impl;

import org.junit.Test;

public class TestClassNotFoundReasonerTask {
	
	@Test
	public void testCNFException01() throws Exception {
		try {
			this.getClass().getClassLoader().loadClass("non.existing.class.Test");
		} catch (Throwable t){
			System.out.println("message: "+t.getMessage());
			System.out.println("stack: "+t.getStackTrace()[6].getClassName());
		}
	}

}
