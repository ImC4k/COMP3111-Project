package comp3111.webscraper;

import java.net.URLEncoder;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.util.Vector;

// New imports
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Locale;

/**
 * WebScraper provide a sample code that scrape web content. After it is constructed, you can call the method scrape with a keyword, 
 * the client will go to the default url and parse the page by looking at the HTML DOM.
 * <br>
 * In this particular sample code, it access to craigslist.org. You can directly search on an entry by typing the URL
 * <br>
 * https://newyork.craigslist.org/search/sss?sort=rel&amp;query=KEYWORD
 * <br>
 * where KEYWORD is the keyword you want to search.
 * <br>
 * Assume you are working on Chrome, paste the url into your browser and press F12 to load the source code of the HTML. You might be freak
 * out if you have never seen a HTML source code before. Keep calm and move on. Press Ctrl-Shift-C (or CMD-Shift-C if you got a mac) and move your
 * mouse cursor around, different part of the HTML code and the corresponding HTML objects will be highlighted. Explore your HTML page from
 * body &rarr; section class="page-container" &rarr; form id="searchform" &rarr; div class="content" &rarr; ul class="rows" &rarr; any one of the multiple 
 * li class="result-row" &rarr; p class="result-info". You might see something like this:
 * <br>
 * <pre>
 * {@code
 *    <p class="result-info">
 *        <span class="icon icon-star" role="button" title="save this post in your favorites list">
 *           <span class="screen-reader-text">favorite this post</span>
 *       </span>
 *       <time class="result-date" datetime="2018-06-21 01:58" title="Thu 21 Jun 01:58:44 AM">Jun 21</time>
 *       <a href="https://newyork.craigslist.org/que/clt/d/green-star-polyp-gsp-on-rock/6596253604.html" data-id="6596253604" class="result-title hdrlnk">
 *       Green Star Polyp GSP on a rock frag</a>
 *       <span class="result-meta">
 *               <span class="result-price">$15</span>
 *               <span class="result-tags">
 *                   pic
 *                   <span class="maptag" data-pid="6596253604">map</span>
 *               </span>
 *               <span class="banish icon icon-trash" role="button">
 *                   <span class="screen-reader-text">hide this posting</span>
 *               </span>
 *           <span class="unbanish icon icon-trash red" role="button" aria-hidden="true"></span>
 *           <a href="#" class="restore-link">
 *               <span class="restore-narrow-text">restore</span>
 *               <span class="restore-wide-text">restore this posting</span>
 *           </a>
 *       </span>
 *   </p>
 *}
 *</pre>
 * The code 
 * <pre>
 * {@code
 * List<?> items = (List<?>) page.getByXPath("//li[@class='result-row']");
 * }
 * </pre>
 * extracts all result-row and stores the corresponding HTML elements to a list called items. Later in the loop it extracts the anchor tag 
 * &lsaquo; a &rsaquo; to retrieve the display text (by .asText()) and the link (by .getHrefAttribute()).
 */
public class WebScraper {
	private static final String DEFAULT_URL = "https://newyork.craigslist.org";
	private static final String ANOTHER_URL = "https://www.amazon.com";
	private WebClient client;

	/**
	 * Default Constructor 
	 */
	public WebScraper() {
		client = new WebClient();
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
	}

	/**
	 * A method implemented in this class, to scrape web content from the Craigslist
	 * @author awtang
	 * @param keyword the keyword you want to search
	 * @return A list of Item that has found. A zero size list is return if nothing is found. Null if any exception (e.g. no connectivity)
	 */
	public List<Item> scrape(String keyword) {
		try {
			String searchUrl = DEFAULT_URL + "/search/sss?sort=rel&query=" + URLEncoder.encode(keyword, "UTF-8");
			System.out.println(searchUrl);
			HtmlPage page = client.getPage(searchUrl);
			
			List<?> items = (List<?>) page.getByXPath("//li[@class='result-row']");
			
			Vector<Item> result = new Vector<Item>();

			for (int i = 0; i < items.size(); i++) {
				HtmlElement htmlItem = (HtmlElement) items.get(i);
				HtmlAnchor itemAnchor = ((HtmlAnchor) htmlItem.getFirstByXPath(".//p[@class='result-info']/a"));
				HtmlElement spanPrice = ((HtmlElement) htmlItem.getFirstByXPath(".//a/span[@class='result-price']"));

				// It is possible that an item doesn't have any price, we set the price to 0.0
				// in this case
				String itemPrice = spanPrice == null ? "0.0" : spanPrice.asText();

				Item item = new Item();
				item.setPortal("Craigslist"); // 
				item.setTitle(itemAnchor.asText());
				item.setUrl(itemAnchor.getHrefAttribute());

				item.setPrice(new Double(itemPrice.replace("$", "")));

				result.add(item);
			}
			client.close();
			
			// The following part is added by awtang
			// We scrape data from another website
			searchUrl = ANOTHER_URL + "/s/field-keywords=" + URLEncoder.encode(keyword, "UTF-8");
			System.out.println(searchUrl);
			page = client.getPage(searchUrl);
			
			List<?> other_items = (List<?>) page.getByXPath("//li[@class='s-result-item celwidget  AdHolder']"
					+ "|//li[@class='s-result-item celwidget  ']"
					+ "|//li[@class='s-result-item s-result-card-for-container a-declarative celwidget  AdHolder']"
					+ "|//li[@class='s-result-item s-result-card-for-container a-declarative celwidget  ']");
			
			System.out.println("No. of items found on Amazon: " + other_items.size());
			for (int i = 0; i < other_items.size(); i++) {
				HtmlElement htmlItem = (HtmlElement) other_items.get(i);
				HtmlAnchor itemAnchor = ((HtmlAnchor) htmlItem.getFirstByXPath(".//a"));
				HtmlElement item_title = ((HtmlElement) htmlItem.getFirstByXPath(".//a/h2"));
				if (item_title == null) { continue; } // The item is not standard
				HtmlElement spanPrice_whole = ((HtmlElement) htmlItem.getFirstByXPath(".//span[@class='sx-price-whole']"));
				HtmlElement spanPrice_fractional = ((HtmlElement) htmlItem.getFirstByXPath(".//sup[@class='sx-price-fractional']"));
				HtmlElement std_cell = null; // To be used

				// The item's information page has to be visited to extract the posted date
				HtmlPage info_page = null;
				if (itemAnchor.getHrefAttribute().matches("^https://.*")) {
					// Some links starts with "https://"
					System.out.println("\n" + itemAnchor.getHrefAttribute());
					info_page = client.getPage(itemAnchor.getHrefAttribute());
				} else {
					System.out.println("\n" + ANOTHER_URL + itemAnchor.getHrefAttribute());
					info_page = client.getPage(ANOTHER_URL + itemAnchor.getHrefAttribute());
				}
				List<?> table_items = (List<?>) info_page.getByXPath("//table[@id='productDetails_detailBullets_sections1']/tbody/tr");
				
				System.out.println(table_items.size());
				// Use for loop to look for the posted date
				for (int j = 0; j < table_items.size(); j++) {
					HtmlElement table_htmlItem = (HtmlElement) table_items.get(j);
					HtmlElement header_cell = ((HtmlElement) table_htmlItem.getFirstByXPath(".//th"));
					if ((header_cell.asText().matches("(.|\\r|\\n)*Date First Available(.|\\r|\\n)*")) ||
							(header_cell.asText().matches("(.|\\r|\\n)*Date first listed on Amazon(.|\\r|\\n)*"))) {
						std_cell = ((HtmlElement) table_htmlItem.getFirstByXPath(".//td"));
						System.out.println(std_cell.asText());
					}
				}
				
				// It is possible that an item doesn't have any price, we set the price to 0.0
				// in this case
				String itemPrice = spanPrice_whole == null ? "0.0" :
					spanPrice_whole.asText().replace(",", "") + "." + spanPrice_fractional.asText();
				System.out.println(itemPrice);
				
				Item item = new Item();
				item.setPortal("Amazon");
				System.out.println(item_title.asText());
				item.setTitle(item_title.asText());
				if (itemAnchor.getHrefAttribute().matches("^https://.*")) {
					// Some links starts with "https://"
					item.setUrl(itemAnchor.getHrefAttribute());
				} else {
					item.setUrl(ANOTHER_URL + itemAnchor.getHrefAttribute());
				}
				item.setPrice(new Double(itemPrice));
				DateFormat df = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
				if (std_cell != null) { item.setDate(std_cell.asText(), df); }

				result.add(item);
				client.close();
			}
			client.close();
			
			// We sort the result by price
			Collections.sort(result, new ItemComparator());
			return result;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}
}