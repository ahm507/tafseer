package org.quran.tafseer;

/**
 * Created by Macpro on 3/Apr/17.
 */
public class Highlight {

	static private String processArabicWord(String arabic) {
		String wordBoundary = "[ .\\(\\),،\\-]+";

		String result = "";
		//unicode diacritics letters from url,
		//http://unicode.org/charts/PDF/U0600.pdf
		String vowels = "[\u064B-\u065F]*";

		for (int i = 0; i < arabic.length(); i++) {
			result += arabic.charAt(i) + vowels;
		}
		return result;
	}

	static String highlight(String bodyString, String highlightWords) {
		String spanStart = "<font color=\"red\">";
		String spanEnd = "</font>";

		for (String word : highlightWords.split(" ")) {
			word = word.trim();
			if (word.length() > 0) {
				String processedWord = processArabicWord(word);
				bodyString = bodyString.replaceAll("(\\b" + processedWord + "\\b)", spanStart + word + spanEnd);
		    }
		}

		//Now, remove the inserted space at the start and at the end
		return bodyString;
	}
}
