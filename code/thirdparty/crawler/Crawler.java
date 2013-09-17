package crawler;

import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class Crawler {
	
	private String Domain,FileName,StartURL;
	private int CrawlCount,MaxDepth,MaxPages;
	public ArrayList<LinkStats> BrokenLinks;
	
	public Crawler(String URL_to_Start_From)
	{
		StartURL = URL_to_Start_From;
		Date temp = new Date();
		FileName = "CrawlReport_" + temp.getTime() + ".htm";
		MaxDepth = 5;
		MaxPages = 100;
	}
	
	public Crawler(String URL_to_Start_From,String Report_File_Name)
	{
		StartURL = URL_to_Start_From;
		FileName = Report_File_Name;
		MaxDepth = 5;
		MaxPages = 100;
	}
	
	public Crawler(String URL_to_Start_From,String Report_File_Name,int Max_Depth_Level)
	{
		StartURL = URL_to_Start_From;
		FileName = Report_File_Name;
		MaxDepth =  Max_Depth_Level;
		MaxPages = 100;		
	}
	
	public Crawler(String URL_to_Start_From,String Report_File_Name,int Max_Depth_Level,int Max_Pages_to_be_crawled)
	{
		StartURL = URL_to_Start_From;
		FileName = Report_File_Name;
		MaxPages = Max_Pages_to_be_crawled;
		MaxDepth =  Max_Depth_Level;
	}
	
	public void start(){
		try{
			URL u =  new URL(StartURL);
			Domain = u.getHost();
		}catch(Exception e){
			System.out.println("Entered URL is invalid");
			return;
		}
		
		createHTML();
		crawl();
		genReport();
	}
	
	private void crawl()
	{
		
		HashMap<String,int[]> ExtLinks = new HashMap<String,int[]>();
		HashMap<String,int[]> RelLinks = new HashMap<String,int[]>();
			
		
		HashMap<String,Boolean> CrawledPages = new HashMap<String,Boolean>();
		ArrayList<String> Visited = new ArrayList<String>();
		
		
		BrokenLinks = new ArrayList<LinkStats>();
		ArrayList<LinkStats> Working = new ArrayList<LinkStats>();
		ArrayList<LinkStats> Broken = new ArrayList<LinkStats>();
		HashMap<String,Integer>LocalChecked = new HashMap<String,Integer>();
		
		
		String url = StartURL;
		String orglink,root,dir="",link,docurl=url,content_type;
		URLConnection conn=null;
		int loc,i=-1,j=1,end,level=0,c;
		int[] ret;
		URL curr,u;
		Matcher m = null;
		
		
		end=j;
		j=end;
			
		
		if (url.endsWith("/") == false && (url.lastIndexOf('/')<=6 || url.substring(url.lastIndexOf("/")).indexOf(".") == -1))
			url+="/";
		
		CrawledPages.put(url,false);
		Visited.add(url);
		
		FileWriter fout;
		try{
			fout = new FileWriter(FileName,true);
		}catch(Exception e){
			System.out.println("Error Opening output file");
			return;
		}
		

		while((i+1)<end && level<MaxDepth && (i+1)<MaxPages)
		{
			while((i+1)<j && (i+1)<MaxPages)
			{	
				i++;
				docurl = Visited.get(i);
				CrawledPages.put(docurl,true);
				CrawlCount++;
				System.out.println("\n\nCrawling : " + docurl);
				
				try{
					u=new URL(docurl);
					conn = u.openConnection();
					content_type = conn.getHeaderField("Content-Type");
					if(content_type.indexOf("text/html")==-1)
					{
						System.out.println("Content-Type of URL : " + content_type + "\nOnly text/html supported");
						continue;
					}
					InputStream inn = conn.getInputStream();
										
					InputStreamReader in = new InputStreamReader(inn);
					BufferedReader buf = new BufferedReader(in);
					StringBuffer sb = new StringBuffer();
					
					while((c=buf.read()) != -1)
						sb.append((char)c);
					StringBuffer innerHTML = sb; 
					Pattern p = Pattern.compile("<a[^>]+href[ \t\n\f\r]*=[ \t\n\f\r]*\"[ \t\n\f\r]*(.*?)[ \t\n\f\r]*\"", Pattern.MULTILINE);
					m = p.matcher(innerHTML);
				}
				catch(Exception e){
					System.out.println("\tERROR");
					continue;
				}
				
				if((loc = url.lastIndexOf("/"))<=6)	//Find the last occurence of '/'
					root = docurl;					//If '/' not found after 'http://' take the current domain as root
				else
					root = docurl.substring(0,loc);		//Else take the substring uptil(not including) the last occurence of '/' as root
				
				root+="/";
				
				
				dir=root;

				while (m.find())
				{
					root=dir;

					orglink=link=m.group(1);
					if(link.startsWith("#"))		//If the link starts with #, ignore and get the next link
						continue;

					if(link.startsWith("mailto:"))
						continue;

					if(!link.startsWith("http"))	//If the link doesnt start wid HTTP -> it is relative
					{

						if(link.startsWith("/"))	//If the link starts with '/',
							root = "http://"+Domain; //THEN set root = current domain

						else if(link.startsWith(".."))	//Else handle the '../' paths(if present)
						{
							while(link.startsWith("../"))//For every ../ occurence at the start
							{
								link=link.substring(3); //Remove ../ at the start
								root = root.substring(0,root.length()-1);
								loc = root.lastIndexOf("/");
								root=root.substring(0,loc);
							}
							root+="/";
						}

						if (link.endsWith("/") == false)
						{
							loc = (loc=link.indexOf('/'))==-1?0:loc;
							if(link.substring(loc).indexOf(".") == -1)
								link+="/";
						}
					}
					else
					{
						try{
							curr = new URL(link);
						}catch(Exception e){
							continue;
						}
						if(curr.getHost().equalsIgnoreCase(Domain))	//FULL path of same domain is given.
						{
							root="";
							if (link.endsWith("/") == false && link.indexOf('/')!=-1 && link.substring(link.lastIndexOf('/')).indexOf(".") == -1)
								link+="/";
						}
						else		//the path is from another domain.
						{
							if(ExtLinks.containsKey(link))	//If this local link is checked before. continue
							{
								if(LocalChecked.containsKey(link))	//If this local link is checked before. continue
									continue;
								ret = ExtLinks.get(link);
								System.out.println("\tChecking  :  " + orglink + " ... " + ret[1]);
							}
							else
							{
								ret=check(link,orglink);
								ExtLinks.put(link,ret);
								if(ret[0]==0)
									BrokenLinks.add(new LinkStats(orglink,ret[1], docurl));
							}
							LocalChecked.put(link,1);
							if(ret[0]==1)
								Working.add(new LinkStats(orglink,ret[1], docurl));
							else
								Broken.add(new LinkStats(orglink,ret[1], docurl));
							continue;
						}
					}

					if((loc=link.indexOf('#'))!=-1)		//To handle '#' in the link
					{
						link = link.substring(0,loc);
					}


					if(!RelLinks.containsKey(root+link))	//If the URL hasnt been checked then
					{
						ret = check(root+link,orglink);	//Add to the list of local_links,global href and store the link's working status
						RelLinks.put(root+link,ret);
						LocalChecked.put(root + link,1);
						if(ret[0]==1)
						{
							Working.add(new LinkStats(orglink,ret[1], docurl));
							Visited.add(root+link);	//add to global visited array along with its state to NOT_CRAWLED i.e. 0
							CrawledPages.put(root+link,false);
							end++;
						}
						else
						{
							Broken.add(new LinkStats(orglink,ret[1], docurl));
							BrokenLinks.add(new LinkStats(root+link,ret[1], docurl));
						}
					}
					else
					{
						if(LocalChecked.containsKey(root + link))
							continue;
						LocalChecked.put(root+link,1);
						ret = RelLinks.get(root+link);
						System.out.println("\tChecking  :  " + orglink + " ... " + ret[1]);
						if(ret[0]==1)				//If the link is working then add it to the
							Working.add(new LinkStats(orglink,ret[1], docurl));
						else
							Broken.add(new LinkStats(orglink,ret[1], docurl));
					}
				}


				Working.clear();
				Broken.clear();
				LocalChecked.clear();
			}
			level++;
			j=end;
		}
		try{fout.close();}catch(Exception e){return;}
	}

	private int[] check(String url,String Relurl){
		int ret[]={0,0};	//Index 0 contains Working or not. Index 1 contains the Status Code

		System.out.print("\tChecking  :  " + Relurl);

		try
		{
			URL u = new URL(url);
			ret[1]=Integer.parseInt(u.openConnection().getHeaderField(0).substring(9,12));
			ret[0]=ret[1]>=400?0:1;
			System.out.println(" ... " + ret[1]);
			return ret;
		}
		catch(Exception e)
		{
			System.out.println(" ... " + "ERROR");
			return ret;
		}
	}

	private void genReport(){
		RandomAccessFile fout;
		FileWriter fp;

		try{



			System.out.println("\n\n\n\nSTATISTICS : \n");
			System.out.println("Pages Crawled\t: " + CrawlCount);
			System.out.println("Broken Links\t: " + BrokenLinks.size());

			fp = new FileWriter(FileName,true);

			fp.write("<h2>Broken Links Finder | Report</h2>\r\n" +
"			<h3>Statistics</h3>\r\n" +
"			<table>\r\n" +
"			<tr><td><h4>Pages Crawled</h4></td><td><h4>:&nbsp;&nbsp;" + CrawlCount + "     </h4></td></tr>\r\n" +
"			<tr><td><h4>Broken Links Found&nbsp;&nbsp;</h4></td><td><h4>:&nbsp;&nbsp;" + BrokenLinks.size() + "       </h4></td></tr>\r\n" +
"			</table>\r\n" +
"			<table>\r\n" +
"				<tbody>\r\n" +
"");
			System.out.println("\n\nBROKEN LINKS : \n");
			for(int loc=0;loc < BrokenLinks.size();loc++)
			{
				System.out.println("\t" + BrokenLinks.get(loc).url + " ... " + BrokenLinks.get(loc).code);
				fp.write(
"\r\n" +
"						<tr><ul>" +
"								<li><strong>Broken URL:</strong> " + BrokenLinks.get(loc).url  + " (" + BrokenLinks.get(loc).code + ")</li>" +
"								<li><strong>Source Page:</strong> <a href=" + BrokenLinks.get(loc).originalURL + ">" + BrokenLinks.get(loc).originalURL + "</li>" +
"						</ul></tr>" 

				);
			}

			fp.write("\r\n" +
"				</tbody>\r\n" +
"			</table>\r\n" +
"		</div>\r\n" +
"	</div>\r\n" +
"</body>\r\n" +
"</html>");
			fp.close();
		}catch(Exception e){System.out.println("Error Opening File");}
	}

	private void createHTML(){
		PrintWriter out;
		try
		{
			out = new PrintWriter(FileName);
			out.print("<html>\r\n" +
"<head>\r\n" +
"<title>Broken Link Finder | Report</title>\r\n" +
"<meta charset=\"utf-8\">\r\n" +
"<meta name=\"authors\" content=\"JAKWorks\">\r\n" +
"<meta name=\"keywords\" content=\"Broken Link Finder\">\r\n" +
"<style type=\"text/css\">\r\n" +
".page td{padding: 5px;font-size:17px;font-weight:bold;}\r\n" +
".working td{font-size:14px;font-weight:bold;padding: 10px 0 10px 10px;}\r\n" +
".broken td{padding: 15px 0 10px 10px;font-size:14px;font-weight:bold;}\r\n" +
".col1{width:100px;text-align:center;font-size:13px;padding:4px}\r\n" +
".col2{font-size:13px;}\r\n" +
".col3{font-size:13px;}\r\n" +
"table{margin:30px 0 30px 0}\r\n" +
"#container{text-align:left;background:#fff;border:1px solid #ccc;-webkit-box-shadow:rgba(0,0,0,0.2) 0px 0px 5px;-moz-box-shadow:rgba(0,0,0,0.2) 0px 0px 5px;-o-box-shadow:rgba(0,0,0,0.2) 0px 0px 5px;box-shadow:rgba(0,0,0,0.2) 0px 0px 5px}#container{margin:30px auto 30px auto;width:440px}\r\n" +
".content{margin:40px 10px 40px 60px;padding:0 0 20px 0;position:relative;font-family:\"Lucida Grande\",\"Lucida Sans Unicode\", Tahoma, sans-serif;letter-spacing:.01em}\r\n" +
"</style>\r\n" +
"</head>\r\n" +
"<body id=\"public\" >\r\n" +
"	<div id=\"container\" class=\"ltr\" style=\"width:90%\">\r\n" +
"		<div class=\"content\" >\r\n" +
"		\r\n");
			
			out.close();
			
		}catch(Exception e){
			System.out.println("Error creating output file");
		}
	}
}
