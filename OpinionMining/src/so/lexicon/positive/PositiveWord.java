package so.lexicon.positive;

import java.io.IOException;
import java.util.HashSet;

import basic.FileManipulator;

public class PositiveWord {
	private static HashSet<String> positiveWords = null;

	public static boolean isPositiveWord(String word) throws IOException {
		if (positiveWords == null) {
			positiveWords = FileManipulator.loadHashSetFromFile("etc/pos.txt");
		}
		return positiveWords.contains(word);
	}
}
