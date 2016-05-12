import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LTPProcess {
	

	public void getText(String inputPath, String outputPath) throws IOException, InterruptedException{
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
				outputPath, true)));
		Scanner in = new Scanner(new BufferedInputStream(new FileInputStream(inputPath)));
		List<String> textList = new ArrayList<String>();
		int count= 0;
		while (in.hasNextLine()) {
			String line = in.nextLine().trim();
			if (line.length() > 0){
				if(count < 1000){
					String [] templist = line.split("\\*\\*");
					String text = templist[3];
					if(text.length() > 300)
						text = text.substring(0, 299);
					//if(count % 100 == 0)
					//	System.out.println(count+"| "+templist[3]);
					//text = templist[3];
					try{
						if(textList.contains(text))
							System.out.println("repeat: "+text);
						else{
							if(LTP2file(text,inputPath,outputPath, out)){
								count ++;
								textList.add(text);
							}
							System.out.println(count+"| "+text);
						}
					}
					catch(IOException e){
						e.printStackTrace();
						//String[] conlist = templist[3].split("。");
						//LTP2file(conlist[0],inputPath,outputPath, out);
					}
					//System.out.println(inputPath+", count:"+count);
				}
			}
		}
		in.close();
		//System.out.println(inputPath+" getText finish, size:"+text.length());
		//LTP2file(text,inputPath,outputPath, out);
	}
	
	public boolean LTP2file(String text, String inputPath, String outputPath, PrintWriter out) throws IOException, InterruptedException{
		//System.out.println("LTP request");
		Thread.sleep(5);
		String api_key = "H6k4C6g7XlYNGCmZtiStdD2ytrtmBsTGZTFSFFBY";
        String pattern = "pos";
        String format  = "xml";
        text = URLEncoder.encode(text, "utf-8");

        URL url = new URL("http://api.ltp-cloud.com/analysis/?"
                              + "api_key=" + api_key + "&"
                              + "text="    + text    + "&"
                              + "format="  + format  + "&"
                              + "pattern=" + pattern);
        URLConnection conn = url.openConnection();
        conn.connect();
        try{
	        BufferedReader innet = new BufferedReader(new InputStreamReader(
	                                conn.getInputStream(),
	                                "utf-8"));
	        String line;
	        
	        while ((line = innet.readLine())!= null) {
	        	if(line.matches("^<html>"))
	        		break;
	            out.println(line);
	            out.flush();
	        }
	        innet.close();
	        return true;
        }catch(java.net.SocketException e){
        	e.printStackTrace();
        	return false;
        }
        //System.out.println("LTP complete");
	}
	public void formatXML(String inputpath, String outputpath){
		InputStream is = null;
		OutputStream os = null;
		try {
		    is = new FileInputStream(inputpath);
		    os = new FileOutputStream(outputpath);
		    @SuppressWarnings("resource")
		    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
		    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"), 512);
		    String line = null;
		    writer.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<xml4nlp>\n");
		    while((line = reader.readLine())!=null){
		    	if(line.matches("^<html>"))
		    		break;
		        if(line.matches("^<.*xml.*>") || line.matches((".*<note.*>"))){
		        }
		        else{
		        	writer.write(line+"\n");
		        }
		    }
		    writer.write("</xml4nlp>");
		    writer.close();
		}catch (FileNotFoundException fnfe){
		    fnfe.printStackTrace();
		}catch (IOException ioe){
		    ioe.printStackTrace();
		} finally {
		    try {
		        if (is != null) {
		            is.close();
		            is = null;
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}  
	}
	public static void main(String[] args) throws IOException, InterruptedException {
		LTPProcess ltp = new LTPProcess();
		ltp.getText("digital/corpus/mp3_mp4.corpus","digital/corpus/mp3_mp4.ltp.xml");
		ltp.formatXML("digital/corpus/mp3_mp4.ltp.xml", "digital/corpus/mp3_mp4.ltp.format.xml");
		System.out.println("mp3/mp4 done");
		ltp.getText("digital/corpus/camera.corpus","digital/corpus/camera.ltp.xml");
		ltp.formatXML("digital/corpus/camera.ltp.xml", "digital/corpus/camera.ltp.format.xml");
		System.out.println("camera done");
		ltp.getText("digital/corpus/mobile.corpus","digital/corpus/mobile.ltp.xml");
		ltp.formatXML("digital/corpus/mobile.ltp.xml", "digital/corpus/mobile.ltp.format.xml");
		System.out.println("mobile done");
		ltp.getText("digital/corpus/notebook.corpus","digital/corpus/notebook.ltp.xml");
		ltp.formatXML("digital/corpus/notebook.ltp.xml", "digital/corpus/notebook.ltp.format.xml");
		System.out.println("notebook done");
	}

}
