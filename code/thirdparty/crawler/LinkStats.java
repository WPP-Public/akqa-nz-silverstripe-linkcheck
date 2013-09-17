package crawler;

public class LinkStats {

	public String url;
	public String originalURL;
	public int code;
	public boolean working;
	public String linkText;
	
	LinkStats(String URL,int HTTPCode,String OriginalURL){
		url = URL;
		originalURL = OriginalURL;
		code = HTTPCode;
	}

	LinkStats(String URL,int HTTPCode,String OriginalURL,String LinkText){
		url = URL;
		code = HTTPCode;
		linkText = LinkText;
	}
}
