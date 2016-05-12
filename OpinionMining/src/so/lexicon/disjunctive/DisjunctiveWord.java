package so.lexicon.disjunctive;

import java.io.IOException;
import java.util.HashSet;

import basic.FileManipulator;

public class DisjunctiveWord {
	private static HashSet<String> disjunctiveWords = null;

	public static boolean isDisjunctiveWord(String word) throws IOException {
		if (disjunctiveWords == null) {
			disjunctiveWords = FileManipulator
					.loadHashSetFromFile("etc/disjunctive.txt");
		}
		return disjunctiveWords.contains(word);
	}
}
