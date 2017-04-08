package org.sonna.www.tafseer;

/**
 * Created by Macpro on 3/Apr/17.
 */
public class Highlight {

	static private String processArabicWord(String arabic) {
		String wordBoundary = "[ .\\(\\),،\\-]+";
//		Python line of code
//		word_boundary_re = u'[ ;:,،.«»\'\"\\(\\)\\-\\{\\}\\<\\>]'  # Just one character must exist (+ is one or more)

		String result = "";
		//unicode diacritics letters from url,
		//http://unicode.org/charts/PDF/U0600.pdf
		String vowels = "[\u064B-\u065F]*";

		for (int i = 0; i < arabic.length(); i++) {
			result += arabic.charAt(i) + vowels;
		}
		//Insert word boundary mark
//		result = "\b" + result + "\b"; // DOES NOT WORK with ARABIC
//		result = wordBoundary + result + wordBoundary;
		return result;
	}


	static String highlight(String bodyString, String highlightWords) {
//    highlightWords = highlightWords.trim();
		//Because of word boundary problem, I have to add space at the start and at the end
//		bodyString = " " + bodyString + " ";
//    console.log("highlighted word is:" + result);
//		String words = highlightWords.split(" ");
		String spanStart = "<font color=\"red\">";
		String spanEnd = "</font>";
//		content += "<font color=\"red\">This is some red color text!</font>";
//		for (var i = 0; i < words.length; i++) {

		for (String word : highlightWords.split(" ")) {
//			var word = words[i].trim();
			word = word.trim();
			if (word.length() > 0) {
				String processedWord = processArabicWord(word);

				bodyString = bodyString.replaceAll("(" + processedWord + ")", spanStart + word + spanEnd);

			}
		}

		//Now, remove the inserted space at the start and at the end
//		bodyString = bodyString.substring(1, bodyString.length() - 1);
		return bodyString;
	}
}
