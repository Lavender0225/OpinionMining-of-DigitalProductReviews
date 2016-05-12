package RunProcess;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import basic.Constant;
import so.dom4j.AllWords;
import so.word.feature.RuleBasedFilter;
import so.word.feature.RuleBasedGen;
import so.word.feature.StatisticsBasedGen;
import so.word.opinion.BasedStat;
import so.word.opinion.Rule_1;
import so.word.opinion.Rule_2;
import test.Test;

public class Process {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		Constant constant = new Constant();
		if(args.length > 0){
			switch(args[0].toLowerCase()){
			case "mp3": 
				constant.setMP3();
				System.out.println("analyzing mp3 domain reviews...");
				break;
			case "camera":
				constant.setCamera();
				System.out.println("analyzing camera domain reviews...");
				break;
			case "mobile":
				constant.setMobile();
				System.out.println("analyzing mobile domain reviews...");
				break;
			case "notebook":
				constant.setNoteBook();
				System.out.println("analyzing notebook domain reviews...");
				break;
			default:
				constant.setMP3();
				System.out.println("analyzing mp3 domain reviews...");
			}
		}
		else{
			constant.setMP3();
			System.out.println("analyzing mp3 domain reviews...");
		}
		System.out.println("output file: "+constant.PATH_PREFIX);
		AllWords.GetAllwords(constant);				// generate allwords.txt
		
		/****************** extract feature words ********************/
		System.out.println("\n*****************extract feature words ********************\n");
		
		RuleBasedGen.ruleBasedGenerate(constant);
		
		RuleBasedFilter.filter(constant);
		
		StatisticsBasedGen.staticBasedFilter(constant);
		
		/****************** extract opinion words ********************/
		System.out.println("\n*****************extract opinion words ********************\n");
		Rule_1.rule1Extract(constant);
		Rule_2.rule2Extract(constant);
		BasedStat.basedStatExtract(constant);
		Scanner in_rule1 = new Scanner(new BufferedInputStream(new FileInputStream(
				constant.FO_RULE1_FILE)));
		Scanner in_rule2 = new Scanner(new BufferedInputStream(new FileInputStream(
				constant.FO_RULE2_FILE)));
//		Scanner in_stat = new Scanner(new BufferedInputStream(new FileInputStream(
//				constant.FO_BASESTAT_FILE)));
		PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(
				constant.FO_ALL_FILE)));
		while(in_rule1.hasNextLine()){
			String line = in_rule1.nextLine().trim();
			out.println(line);
			out.flush();
		}
		while(in_rule2.hasNextLine()){
			String line = in_rule2.nextLine().trim();
			out.println(line);
			out.flush();
		}
//		while(in_stat.hasNextLine()){
//			String line = in_stat.nextLine().trim();
//			out.println(line);
//			out.flush();
//		}
		out.close();
		
		/****************** polarity and classify ********************/
		System.out.println("\n*****************polarity and classify ********************\n");
		so.pair.polarity.Polarity.pairPolarity(constant, constant.FO_ALL_FILE);
		so.sentence.polarity.Polarity.sentencePolarity(constant);
		
		/****************** calculate accuracy ********************/
		Test.statistic(constant);
	}

}
