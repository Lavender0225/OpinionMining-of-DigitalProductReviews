package so.lexicon.conjunctive;

import java.io.IOException;
import java.util.HashSet;

import basic.FileManipulator;

public class ConjunctiveWord {
	private static HashSet<String> conjunctiveWords = null;

	public static boolean isConjunctiveWord(String word) throws IOException {
		if (conjunctiveWords == null) {
			conjunctiveWords = FileManipulator
					.loadHashSetFromFile("etc/conjunctive.txt");
		}
		return conjunctiveWords.contains(word);
	}
}
