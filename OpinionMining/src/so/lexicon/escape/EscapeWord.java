package so.lexicon.escape;

import java.io.IOException;
import java.util.HashSet;

import basic.FileManipulator;

public class EscapeWord {
	private static HashSet<String> escapeWords = null;

	public static boolean isEscapeWord(String word) throws IOException {
		if (escapeWords == null) {
			escapeWords = FileManipulator.loadHashSetFromFile("etc/escape.txt");
		}
		return escapeWords.contains(word);
	}
}
