package com.tangqiang.weixin;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		assertTrue(true);
	}

	public void testTemplate() {
		for (int i = 0; i < 10; i++) {
			System.out.println(String.format("{{ti%s.DATA}}", i,i));
			System.out.println(String.format("来自:{{wx%s.DATA}} {{t%s.DATA}}", i,i));
			System.out.println(String.format("赞数:{{p%s.DATA}}/{{r%s.DATA}}",i,i));
			System.out.println(String.format("链接:{{h%s.DATA}}", i));
			System.out.println();
		}

	}
}
