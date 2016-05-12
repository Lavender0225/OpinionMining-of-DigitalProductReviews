package so.lexicon.negative;

import java.io.IOException;
import java.util.HashSet;

import basic.FileManipulator;

public class NegativeWord {
	private static HashSet<String> negativeWords = null;

	public static boolean isNegativeWord(String word) throws IOException {
		if (negativeWords == null) {
			negativeWords = FileManipulator.loadHashSetFromFile("etc/neg.txt");
		}
		return negativeWords.contains(word);
	}
}
