package org.quran.tafseer;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

	@Test
	public void testSearchHighlight() throws Exception {
		String text =  "besm ellah ";
		String textHighlighted = "besm <font color=\"red\">ellah</font> ";
		assertEquals(textHighlighted, Highlight.highlight(text, "ellah"));
	}
}
