package comp3111.webscraper;

import javafx.scene.control.Hyperlink;

// New imports, by ckchuad
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * This class stores the details for scraped items
 * @author awtang
 */
public class Item {
	private String title;
	private double price;
	private Hyperlink url; // modified by ckchuad, from String to Hyperlink
	
	private String portal; // added by awtang
	private Date date = null; // added by ckchuad
	private DateFormat format; // added by ckchuad
	
	/**
	 * to initialize url object<br>
	 * used in task 2
	 * @author ckchuad
	 */
	public Item() {
		url = new Hyperlink();
	}
	
	/**
	 * the default constructor is overloaded
	 * @author awtang
	 * @param test_mode set this value to true when performing the unit tests
	 */
	public Item(boolean test_mode) {
		if (test_mode == false) {
			url = new Hyperlink();
		}
	}
	
	/**
	 * to get the portal name<br>
	 * used in task 1, 2
	 * @author awtang
	 * @return the name of the portal
	 */
	public String getPortal() {
		return portal;
	}
	
	/**
	 * to set the portal<br>
	 * used in task 2
	 * @author awtang
	 * @param portal the name of the portal
	 */
	public void setPortal(String portal) {
		this.portal = portal;
	}
	
	/**
	 * used in task 1
	 * @return the title of the item
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * used in task 2
	 * @param title the title of the item
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * used in task 1
	 * @return the price of the item
	 */
	public double getPrice() {
		return price;
	}
	
	/**
	 * used in task 2
	 * @param price the price of the item
	 */
	public void setPrice(double price) {
		this.price = price;
	}
	
	/**
	 * return value is changed from String to Hyperlink
	 * @author ckchuad
	 * @return Hyperlink
	 */
	public Hyperlink getUrl() {
		return url;
	}
	
	/**
	 * for adding event handler (task 4)<br>
	 * used in task 1 as well
	 * @author ckchuad
	 * @return: String
	 */
	public String getUrlText() { 
		return url.getText();
	}
	
	/**
	 * used in task 2
	 * @param url the url to be set
	 * @author ckchuad
	 */
	public void setUrl(String url) {
		this.url.setText(url);
	}
	
	/**
	 * used in task 2
	 * @param date the date to be set
	 * @param format the format of the date
	 * @author ckchuad
	 */
	public void setDate(String date, DateFormat format){
		try{
			this.format = format;
			this.date = format.parse(date);
		}catch(ParseException e) {
			System.out.println(e.toString());
			this.date = null;
		}
	}

	/**
	 * if date is null, then return "No Date"
	 * @author ckchuad
	 * @return String
	 */
	public String getDate() {
		if(date != null) {
			return format.format(date);
		}
		return "No Date";
	}
	
	/**
	 * for getting the date
	 * used in task 1
	 * @author awtang
	 * @return the posted date of the item (a Date object)
	 */
	public Date getDate_raw() {
		return this.date;
	}
}