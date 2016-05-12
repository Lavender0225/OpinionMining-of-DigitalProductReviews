package basic;

import java.io.*;
import java.util.*;

public class FileManipulator {
	public static HashSet<String> loadHashSetFromFile(String fileName)
			throws IOException {
		HashSet<String> set = new HashSet<String>();
		Scanner in = new Scanner(new BufferedInputStream(new FileInputStream(
				fileName)));
		while (in.hasNextLine()) {
			String line = in.nextLine().trim();
			if (line.length() > 0)
				set.add(line);
		}
		in.close();
		return set;
	}

	public static void outputCollectionFromFile(Collection<String> collection,
			String fileName) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
				fileName)));
		for (String element : collection) {
			out.println(element);
			out.flush();
		}
		out.close();
	}

	public static HashMap<String, HashSet<String>> loadOneToMany(
			String fileName, String separator1, String separator2)
			throws IOException {
		HashMap<String, HashSet<String>> oneToMany = new HashMap<String, HashSet<String>>();

		System.out.println("Start loading " + fileName);

		Scanner in = new Scanner(new BufferedInputStream(new FileInputStream(
				fileName)));
		while (in.hasNextLine()) {
			String line = in.nextLine().trim();
			String[] oneMany = line.split(separator1);

			if (oneMany.length != 2)
				continue;

			String one = oneMany[0];
			if (one.length() == 0)
				continue;
			if (oneMany[1].endsWith(";"))
				oneMany[1] = oneMany[1].substring(0, oneMany[1].length() - 1);
			String[] many = oneMany[1].split(separator2);

			HashSet<String> temp = new HashSet<String>();
			for (String each : many)
				temp.add(each);
			oneToMany.put(one, temp);
		}
		in.close();

		System.out.println("Finish loading " + fileName);

		return oneToMany;
	}

	public static void outputOneToMany(
			HashMap<String, HashSet<String>> oneToMany, String fileName,
			String separator1, String separator2) throws IOException {
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
				fileName)));
		for (Map.Entry<String, HashSet<String>> entry : oneToMany.entrySet()) {
			String one = entry.getKey();
			if (one.length() == 0)
				continue;
			HashSet<String> many = entry.getValue();
			StringBuilder sb = new StringBuilder();
			for (String each : many)
				sb.append(each + separator2);
			sb.deleteCharAt(sb.length() - 1);
			pw.println(one + separator1 + sb.toString());
			pw.flush();
		}
		pw.close();
		System.out.println("Finish output " + fileName);
	}
}
