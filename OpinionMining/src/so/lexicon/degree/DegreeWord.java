package so.lexicon.degree;

import java.io.IOException;
import java.util.HashSet;

import basic.FileManipulator;

public class DegreeWord {
	private static HashSet<String> degreeWords = null;

	public static boolean isDegreeWord(String word) throws IOException {
		if (degreeWords == null) {
			degreeWords = FileManipulator.loadHashSetFromFile("etc/degree.txt");
		}
		return degreeWords.contains(word);
	}
}
