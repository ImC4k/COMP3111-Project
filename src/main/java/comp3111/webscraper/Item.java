package comp3111.webscraper;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javafx.scene.control.Hyperlink;

public class Item {
	private String title; 
	private double price;
	private Hyperlink url; // modified by ckchuad, from String to Hyperlink
	private Date date = null; // added by ckchuad
	private DateFormat format; // added by ckchaud
	
	/**
	 * to initialize url object
	 * @author ckchuad
	 */
	public Item() {
		url = new Hyperlink();
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	/**
	 * return value is changed from String to Hyperlink
	 * @author ckchuad
	 * @return Hyperlink
	 * 
	 */
	public Hyperlink getUrl() {
		return url;
	}
	
	/**
	 * for adding event handler (Task 4)
	 * @author ckchuad
	 * @return: String
	 */
	public String getUrlText() { 
		return url.getText();
	}
	
	/**
	 * @param url the url to be set
	 * @author ckchuad
	 */
	public void setUrl(String url) {
		this.url.setText(url);
	}
	
	/**
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
	 * @author awtang
	 * @return A "Date" object
	 */
	public Date getDate_raw() {
		return this.date;
	}
}