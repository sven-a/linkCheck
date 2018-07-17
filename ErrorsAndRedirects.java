package linkCheck;

import java.util.Hashtable;

public class ErrorsAndRedirects {
	Hashtable<String, Integer> errorPages = new Hashtable<String, Integer>();
	Hashtable<String, String> redirectPages = new Hashtable<String, String>();
	
	ErrorsAndRedirects(Hashtable<String, Integer> errorPages ,	Hashtable<String, String> redirectPages) {
		this.errorPages = errorPages;
		this.redirectPages = redirectPages;
	}
	
	public Hashtable<String, Integer> getErrorPages() {
		return errorPages;
	}

	public Hashtable<String, String> getRedirectPages() {
		return redirectPages;
	}
}