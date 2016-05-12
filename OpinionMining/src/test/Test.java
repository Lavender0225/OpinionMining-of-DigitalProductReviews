package test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

import basic.Constant;

public class Test {
	private static HashMap<String, Double> sentencePolarity = new HashMap<String, Double>();
	private static HashMap<String, Double> testSentencePolarity = new HashMap<String, Double>();
	
	@SuppressWarnings("resource")
	public static void staticAccuracy(String record_filename) throws IOException{
		
		PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(
				record_filename, true)));
		int allPositive = 0, correctPositive = 0;
		int allNegative = 0, correctNegative = 0;
		int allNeutral = 0, correctNeutral = 0;
		for (String sent : sentencePolarity.keySet()) {
			double tp = 0.0;
			if(testSentencePolarity.containsKey(sent)){
				tp = testSentencePolarity.get(sent);
			
				double polarity = sentencePolarity.get(sent);
				
				if (tp == 4.0 || tp == 5.0) {
					allPositive++;
					
					if (polarity == 1.0)
						correctPositive++;
					else{
						out.println("positive wrong----r:"+polarity+"\t"+sent
								+ "|| t:" + tp + "\t" +sent);
					}
				} else if (tp == 1.0 || tp == 2.0) {
					allNegative++;
					if (polarity == -1.0)
						correctNegative++;
					else{
						out.println("negative wrong----r:"+polarity+"\t"+sent
								+ "|| t:" + tp + "\t" + sent);
					}
				} else if (tp == 3.0) {
					allNeutral++;
					if (polarity == 0.0 )
						correctNeutral++;
					else{
						out.println("neutral wrong----r:"+polarity+"\t"+sent
								+ "|| t:" + tp + "\t" + sent);
					
					}
				}
			}
			else{
				//System.out.println("sent not in test:"+sent);
			}
		}
		System.out.println("Accuracy:");
		System.out.println("Pos: " + correctPositive + " / " + allPositive
				+ " | " + ((double) correctPositive / allPositive));
		System.out.println("Neg: " + correctNegative + " / " + allNegative
				+ " | " + ((double) correctNegative / allNegative));
		System.out.println("Neu: " + correctNeutral + " / " + allNeutral
				+ " | " + ((double) correctNeutral / allNeutral));

		System.out
				.println("Total: "
						+ (correctPositive + correctNegative + correctNeutral)
						+ " / "
						+ (allPositive + allNegative + allNeutral)
						+ " | "
						+ ((double) (correctPositive + correctNegative + correctNeutral) / (allPositive
								+ allNegative + allNeutral)));
	}
	
	public static void staticRecall(String record_filename) throws IOException{
		int allPositive = 0, correctPositive = 0;
		int allNegative = 0, correctNegative = 0;
		int allNeutral = 0, correctNeutral = 0;
		for (String sent : sentencePolarity.keySet()) {
			double tp = 0.0;
			if(testSentencePolarity.containsKey(sent)){
				tp = testSentencePolarity.get(sent);
				double polarity = sentencePolarity.get(sent);
				
				if (polarity == 1.0) {
					allPositive++;
					
					if (tp == 4.0 || tp == 5.0)
						correctPositive++;
					
				} else if (polarity == -1.0) {
					allNegative++;
					if (tp == 1.0 || tp == 2.0)
						correctNegative++;
					
				} else if (polarity == 0.0) {
					allNeutral++;
					if (tp == 3.0 )
						correctNeutral++;
				}
			}
		}
		System.out.println("Recall:");
		System.out.println("Pos: " + correctPositive + " / " + allPositive
				+ " | " + ((double) correctPositive / allPositive));
		System.out.println("Neg: " + correctNegative + " / " + allNegative
				+ " | " + ((double) correctNegative / allNegative));
		System.out.println("Neu: " + correctNeutral + " / " + allNeutral
				+ " | " + ((double) correctNeutral / allNeutral));

		System.out
				.println("Total: "
						+ (correctPositive + correctNegative + correctNeutral)
						+ " / "
						+ (allPositive + allNegative + allNeutral)
						+ " | "
						+ ((double) (correctPositive + correctNegative + correctNeutral) / (allPositive
								+ allNegative + allNeutral)));
	}
	
	public static void statistic(Constant constant) throws IOException {
		
		Scanner in = new Scanner(new BufferedInputStream(new FileInputStream(
				constant.RESULT_FILE)));
		
		while (in.hasNextLine()) {
			String line = in.nextLine().trim();
			String[] array = line.split("\t\t");
			Double score = Double.valueOf(array[0]);
			String content = array[2].replaceAll(" ", "");
			if(!sentencePolarity.containsKey(content))
				sentencePolarity.put(content, score);
			//System.out.println("result: "+content);
		}
		in.close();
		

		in = new Scanner(new BufferedInputStream(new FileInputStream(constant.CURRENT_CORPUS.toString())));
		//int count = 0;
		while (in.hasNextLine()) {
			String line = in.nextLine().trim();
			String[] array = line.split("\\*\\*");
			String content = array[3];
			if(content.length() > 300)
				content = content.substring(0, 299);
			content = content.replaceAll(" ", "");
			Double score = Double.valueOf(array[1]);
			if(sentencePolarity.containsKey(content)){
				testSentencePolarity.put(content, score);
			}
//			else{
//				if(count < 1000)
//					System.out.println("not in result :"+content);
//			}
			//count ++;
		}
		in.close();
		//System.out.println("result size:"+sentencePolarity.size());
		System.out.println("test data size:"+testSentencePolarity.size());
		staticAccuracy(constant.RECORD_FILE);
		staticRecall(constant.RECORD_FILE);
		
	}
}
