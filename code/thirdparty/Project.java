import crawler.Crawler;
import java.util.Scanner;


public class Project {
	public static void main(String args[]){
		
		String url,filename;
		Scanner read = new Scanner(System.in);
		Crawler obj;
		
		if(args.length<4)
		{
			System.out.println("Note : You Use commandline arguments to set Crawling Parameters\n\tjava project [URL] [Report_File_Name] [MaxDepthLevel] [MaxPages]\n\n");
		}
		
		switch(args.length)
		{
	
		case 1:
			System.out.print("Report FileName(htm) : ");
			filename = read.next();
			obj = new Crawler(args[0],filename);
			break;
			
		case 2:
			obj = new Crawler(args[0],args[1]);
			break;
			
		case 3:
			obj = new Crawler(args[0],args[1],Integer.parseInt(args[2]));
			break;
			
		case 4:
			obj = new Crawler(args[0],args[1],Integer.parseInt(args[2]),Integer.parseInt(args[3]));
			break;
			
		default:
			if(args.length<1)
			{
				System.out.print("Enter URL : ");
				url = read.next();
				System.out.print("Report FileName(htm) : ");
				filename = read.next();
				obj = new Crawler(url,filename);
			}
			else
				obj = new Crawler(args[0],args[1],Integer.parseInt(args[2]),Integer.parseInt(args[3]));
		}
		obj.start();
	}
}