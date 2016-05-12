import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;


public class ProductReviewList {
	private ArrayList<String> set_mp3, set_camera, set_mobile, set_notebook;
	private String MP3Path = "digital/corpus/mp3_mp4.corpus";
	private String cameraPath = "digital/corpus/camera.corpus";
	private String mobilePath = "digital/corpus/mobile.corpus";
	private String notebookPath = "digital/corpus/notebook.corpus";
	
	public ProductReviewList(){
		set_mp3 = new ArrayList<String>();
		set_camera = new ArrayList<String>();
		set_mobile = new ArrayList<String>();
		set_notebook = new ArrayList<String>();
	}
	
	@SuppressWarnings("rawtypes")
	public HashSet<String> parserReview(String inputXML) throws IOException{
		SAXReader saxReader = new SAXReader();
		Document document;
		try {
			document = saxReader.read(inputXML);
			Node category = document.selectSingleNode("//category");
			Node item_id = document.selectSingleNode("//item_id");
			//System.out.print(inputXML);
			//System.out.print(", "+category.getText()+"\n");
			List reviewList = document.selectNodes("//review");
			Iterator reviewIter = reviewList.iterator();
			while (reviewIter.hasNext()) {
				Element review = (Element) reviewIter.next();
				Element title = review.element("title");
				Element date = review.element("date");
				Element star = review.element("star");
				Element reviewbody = review.element("reviewbody");
				Element advantage = reviewbody.element("advantage");
				Element disadvantage = reviewbody.element("disadvantage");
				Element overview = reviewbody.element("overview");
				if(item_id != null && star != null && date != null && title != null && disadvantage != null
						&& advantage != null && overview != null && !(title.getText()+advantage.getText()
						+disadvantage.getText()+overview.getText()).isEmpty()){
					String str = item_id.getText()+"**"+star.getText()+"**"+date.getText()+"**"+
								title.getText()+"。"+advantage.getText()
								+disadvantage.getText()+overview.getText();
					//str = str.replaceAll("[。.?!！？;；]", ",");
					//String content = str.split("\\*\\*")[3];
					if(category.getText().compareToIgnoreCase("MP3/MP4") == 0){
						set_mp3.add(str);
						//LTP2file(content,"digital/corpus/mp3_mp4.ltp.xml");
					}
					else if(category.getText().compareToIgnoreCase("手机") == 0){
						set_camera.add(str);
						//LTP2file(content,"digital/corpus/mobile.ltp.xml");
					}
					else if(category.getText().compareToIgnoreCase("相机") == 0){
						set_mobile.add(str);
						//LTP2file(content,"digital/corpus/camera.ltp.xml");
					}
					else if(category.getText().compareToIgnoreCase("笔记本") == 0){
						set_notebook.add(str);
						//LTP2file(content,"digital/corpus/notebook.ltp.xml");
					}
				}
			}
			
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		
		return null;
	}
	public void write2file() throws IOException {
		outputArrayListFromFile(set_mp3, MP3Path);
		outputArrayListFromFile(set_camera, cameraPath);
		outputArrayListFromFile(set_mobile, mobilePath);
		outputArrayListFromFile(set_notebook, notebookPath);
		
		
	}
	public void outputArrayListFromFile(ArrayList<String> collection,
			String fileName) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
				fileName)));
		for (String element : collection) {
			out.println(element);
			out.flush();
		}
		out.close();
	}
	
	public void LTP2file(String text, String outputPath) throws IOException{
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

        BufferedReader innet = new BufferedReader(new InputStreamReader(
                                conn.getInputStream(),
                                "utf-8"));
        String line;
        @SuppressWarnings("resource")
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
				outputPath, true)));
        while ((line = innet.readLine())!= null) {
            out.println(line);
            out.flush();
        }
        innet.close();
	}
	
	public static void main(String[] args) throws IOException {
		ProductReviewList prl = new ProductReviewList();
		for(int i = 0; i < 2911; i ++ ){
			String num = String.format("%08d", i);
			String xmlPath = "digital/review/"+num+".xml";
			prl.parserReview(xmlPath);
		}
		try {
			prl.write2file();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
