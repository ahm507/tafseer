package org.sonna.www.sonna;

import android.app.Application;
import android.test.ApplicationTestCase;

import org.junit.Test;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
	public ApplicationTest() {
		super(Application.class);
	}

	@Test
	public void addition_isCorrect() throws Exception {
		assertEquals(4, 5);
		System.out.print("Hello world!");
	}

}