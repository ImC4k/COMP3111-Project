package comp3111.webscraper;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Hyperlink;
import java.util.List;
import java.util.Vector;

//by ckchuad, task 6
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
//end by ckchuad, task 6

//by ckchuad, for task 4
import javafx.application.HostServices;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Date;
//end by ckchuad for task 4

//added by lyleungad for task5
import java.util.function.Predicate;
import javafx.scene.control.Button;
// end by lyleungad for task5

/**
 * This class manages GUI interaction
 * @author awtang, kevinw, ckchuad, lyleungad
 */

// Controller class that manage GUI interaction. Please see document about JavaFX for details.
public class Controller extends WebScraperApplication {
	@FXML
	private Label labelCount;
	
	@FXML
	private Label labelPrice;
	
	@FXML
	private Hyperlink labelMin;
	
	@FXML
	private Hyperlink labelLatest;
	
	@FXML
	private TextField textFieldKeyword;
	
	@FXML
	private TextArea textAreaConsole;
	
	private WebScraper scraper;
	
    // by ckchuad, task 4
    @FXML
    private TableView<Item> table;
    
    @FXML
    private TableColumn<Item, String> labelTableTitle;
    
    @FXML
    private TableColumn<Item, Double> labelTablePrice;
    
    @FXML
    private TableColumn<Item, String> labelTableURL;
    
    @FXML
    private TableColumn<Item, String> labelTableDate;
    
    private final HostServices host;
    // end by ckchuad, task 4
    
    // by lyleungad, task 5
    @FXML
    private Button refineButton;
    // end by lyleungad, task 5
    
    // by ckchuad, task 6
    @FXML
    private MenuItem labelMenuLastSearch;
    
    @FXML
    private MenuItem labelAboutTeam;
    
    private List<Item> currSearch;
    
    private List<Item> lastSearch;
    // end by ckchuad, task 6
    
	/**
	 * The number of event handlers for opening URLs in the summary tab. It should be at most 1.
	 * @author awtang
	 */
	public int event_handler_count = 0;
	/**
	 * The number of items scraped
	 * @author awtang
	 */
	public int item_count = 0;
	/**
	 * The sum of prices of scraped items to calculate the average selling price
	 * @author awtang
	 */
	public double price_sum = 0;
	/**
	 * The number of items that have non-zero price
	 * @author awtang
	 */
	public int item_count_nonzero = 0;
	/**
	 * The minimum price among all the prices of scraped items
	 * @author awtang
	 */
	public double min_price = Double.POSITIVE_INFINITY;
	/**
	 * The URL of the item with the minimum price
	 * @author awtang
	 */
	public String labelMin_url = "";
	/**
	 * The maximum date among the dates of the items
	 * @author awtang
	 */
	public Date max_date = new Date(0L); // "0L" is the number zero of type "long"
	/**
	 * The title of the latest item
	 * @author awtang
	 */
	public String labelLatest_title = "";
	/**
	 * The URL of the latest item
	 * @author awtang
	 */
	public String labelLatest_url = "";
	/**
	 * A value for unit testing
	 * @author awtang
	 */
	public int test_exit_value = 0;
	
	/**
	 * Default Controller constructor
	 * @author ckchuad
	 */
	public Controller() {
		scraper = new WebScraper();
		// by ckchuad, task 6
		labelMenuLastSearch = new MenuItem();
		// end by ckchuad, task 6
		
		// by ckchuad, task 4
		host = this.getHostServices();
		// end by ckchuad, task 4
		
		currSearch = new Vector<Item>();
	}
	
	/**
	 * Default initializer
	 * @author lyleungad, ckchuad
	 */
	@FXML
	private void initialize() {
		// added by lyleungad for task 5
		refineButton.setDisable(true);
		
		// added by ckchuad for task 6
		labelMenuLastSearch.setDisable(true);
	}
	
	/**
	 * Called when the search button is pressed. Used in task 1.
	 * @author awtang, ckchuad
	 */
	
	// (There is no @param, @return, @exception)
	@FXML
	private void actionSearch() {
		if (textFieldKeyword.getText().isEmpty()) {
			return; // Do nothing and return
		}
		
		// Fetch the result by the scraper
		System.out.println("actionSearch: " + textFieldKeyword.getText());
		List<Item> result = scraper.scrape(textFieldKeyword.getText());		
		updateUI(result);
		
		// by ckchuad, task 6
		updateSearchLists(result);
		// end by ckchuad, task 6
	}
	
	/**
	 * Used to update the UIs all at once. Used in task 2.
	 * 
	 * @author ckchuad, lyleungad
	 * @param list the list of items to be shown in console, summary and table
	 */
	private void updateUI(List<Item> list) {
		String output = "";
		for (Item item : list) {
			output += item.getPortal() + ": " + item.getTitle() + "\t" + item.getPrice() + "\t" + item.getUrl() + "\n";
		}
		textAreaConsole.setText(output);
		getItemsAndDisplay(false, list);
		
		// by lyleungad for task 5
		// set refine button disable to false after search		
    	if(list.size()>0) {
    		refineButton.setDisable(false);
    	}
		// end lyleungad for task 5
    	
		// by ckchuad, task 4
		createTable(list);
		// end by ckchuad, task 4
	}
	
	/**
	 * test method for getting scraper object
	 * @author ckchuad
	 * @return scraper
	 */
	public WebScraper getScraper() {
		return scraper;
	}
	
	/**
	 * Get the items from the object "list" and display them. Used in task 1.
	 * @author awtang
	 * @param test_mode set this to true when running unit tests
	 * @param list the list of items to display in summary tab
	 */
	public void getItemsAndDisplay(boolean test_mode, List<Item> list) {
		// Refresh the content for another search
		item_count = list.size();
		price_sum = 0;
		item_count_nonzero = 0;
		min_price = Double.POSITIVE_INFINITY;
		max_date.setTime(0L);
		
		if (event_handler_count == 0 && test_mode == false) {
			// The event handler is added once only
			labelMin.addEventHandler(ActionEvent.ACTION, (e) -> openDoc(labelMin_url));
			labelLatest.addEventHandler(ActionEvent.ACTION, (e) -> openDoc(labelLatest_url));
			event_handler_count++;
		}
		
		if(item_count >= 1) {
			labelLatest_title = list.get(0).getTitle(); // The first result
			labelLatest_url = list.get(0).getUrl(); // The first result
			
			for (Item item : list) {
				if (item.getPrice() != 0.0) {
					// Items with zero selling price is excluded in the calculations
					item_count_nonzero += 1;
					
					price_sum += item.getPrice(); // To calculate the average selling price
					
					// Find the item with lowest selling price
					if (item.getPrice() < min_price) {
						min_price = item.getPrice();
						// Set the URL for labelMin_url
						labelMin_url = item.getUrl();
					}
				}
				
				// Find the latest post
				if (item.getDate_raw() != null) {
					if (item.getDate_raw().compareTo(max_date) > 0) {
						max_date = item.getDate_raw();
						labelLatest_title = item.getTitle();
						labelLatest_url = item.getUrl();
					}
				}
			}
			
			if (item_count_nonzero != 0) { // At least one item has a price
				test_exit_value = 1;
				if (test_mode == false) {
					labelPrice.setText(Double.toString(price_sum/item_count_nonzero));
					labelMin.setText(Double.toString(min_price));
				}
			} else { // All the items have no prices
				test_exit_value = 2;
				if (test_mode == false) {
					labelMin_url = "";
					labelPrice.setText("-");
					labelMin.setText("-");
				}
			}
			if (test_mode == false) {
				labelCount.setText(Integer.toString(item_count));
				labelLatest.setText(labelLatest_title);
			}
		} else { // We cannot find a result
			test_exit_value = 3;
			if (test_mode == false) {
				labelMin_url = "";
				labelLatest_url = "";
				
				labelCount.setText("0");
				labelPrice.setText("-");
				labelMin.setText("-");
				labelLatest.setText("-");
			}
		}
	}
	
	// by ckchuad, task 4
	/**
	 * this function is for creating a list to be added into the table for task 4
	 * @author ckchuad
	 * @param items
	 * @return a list that can be put into a table view
	 */
	private ObservableList<Item> getList(List<Item> items) {
		ObservableList<Item> olist = FXCollections.observableArrayList();
		for (Item item : items) {
			olist.add(item);
		}
		return olist;
	}
	
    /**
     * public test method for getList()
     * @author ckchuad
     * @param items the list of items to display
     * @return list the list of items inside ObservableList
     */
    public ObservableList<Item> getListTest(List<Item> items){
    	return getList(items);
    }
    
    /**
     * this function puts everything in items into the table view<br>
     * requires function getList(), openDoc()
     * @author ckchuad
     * @param items the list of items to display in table view
     */
    public void createTable(List<Item> items) {
    	ObservableList<Item> tableList = getList(items);
    	labelTableTitle.setCellValueFactory(new PropertyValueFactory<Item, String>("title"));
    	labelTablePrice.setCellValueFactory(new PropertyValueFactory<Item, Double>("price"));
    	labelTableURL.setCellValueFactory(new PropertyValueFactory<Item, String>("url"));
    	labelTableURL.setCellFactory(tc -> {
    		TableCell<Item, String> cell = new TableCell<Item, String>(){
    			@Override
    			protected void updateItem(String item, boolean empty) {
    				super.updateItem(item,  empty);
    				setText(item);
    			}
    		};
    		cell.setOnMouseClicked(e -> {
    			openDoc(cell.getText());
    			System.out.println(cell.getText());
    		});
    		return cell;
    	});
    	labelTableDate.setCellValueFactory(new PropertyValueFactory<Item, String>("date"));
    	table.setItems(tableList);
    }
    // end by ckchuad, task 4
    
    // by ckchuad, task 6
    /**
     * task 6, update the searched lists upon new searches<br>
     * if it is the first search, then assign the list to currSearch<br>
     * otherwise, enable ReloadLastSearch button, assign currSearch to lastSearch, assign new list to currSearch
     * @author ckchuad
     * @param items the list of item to be updated to currSearch
     */
    private void updateSearchLists(List<Item> items) {
    	if(currSearch.isEmpty() && lastSearch == null) { // first time
    		currSearch = items;
    	}
    	else{
    		labelMenuLastSearch.setDisable(false);
    		lastSearch = currSearch;
    		currSearch = items;
    	}
    }
    
    /**
     * for testing
     * @author ckchuad
     * @param items the list of items to be updated to currSearch
     */
    public void updateSearchListsTest(List<Item> items) {
    	updateSearchLists(items);
    }
    
    /**
     * for testing
     * @author ckchuad
     * @return currSearch
     */
    public List<Item> getCurrSearch() {
    	return currSearch;
    }
    
    /**
     * for testing
     * @author ckchuad
     * @return lastSearch
     */
    public List<Item> getLastSearch() {
    	return lastSearch;
    }
    
    /**
     * task 6, show about team
     * @author ckchuad
     * @param event, e.g. onClick
     */
    @FXML
    public void showAboutTeam(ActionEvent event) {
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("About our team");
    	alert.setHeaderText("Information about our team");
    	alert.setContentText("Team member 1: Chu Cheuk Kiu\tckchuad\tImC4k\n"
    			+ "Team member 2: Leung Lai Yung\tlyleungad\tBenker-Leung\n"
    			+ "Team member 3: Tang Au Wa\tawtang\tdavidtang1006\n");
    	alert.setResizable(true);
    	alert.showAndWait();
    }
    
    /**
     * task 6, quit button
     * @author ckchuad
     * @param event, e.g. onClick
     * @return true if successful termination
     */
    @FXML
    public boolean terminateWindow(ActionEvent event) {
    	Platform.exit();
    	return true;
    }
    
    /**
     * task 6, close button
     * @author ckchuad
     */
    @FXML
    void closeWindow() {
    	lastSearch = currSearch;
    	if(lastSearch != null) {
    		labelMenuLastSearch.setDisable(false); // enable last search
    	}
    	
    	textFieldKeyword.setText("");
    	
    	// Console tab
    	textAreaConsole.setText(""); // resets console
    	
    	// Summary tab
    	labelPrice.setText("<AvgPrice>");
    	labelMin.setText("<Lowest>");
    	labelMin.setVisited(false);
    	labelLatest.setText("<Latest>");
    	labelLatest.setVisited(false);
    	
    	// Table tab
    	Vector<Item> items = new Vector<Item>();
		List<Item> items_list = items;
		ObservableList<Item> emptyList = getList(items_list);
    	table.setItems(emptyList);
    }
    
    /**
     * task 6, reload previous search state
     * @author ckchuad
     */
    @FXML
    private void reloadLastSearch() {
    	System.out.println("reloading last search");
    	labelMenuLastSearch.setDisable(true);
    	updateUI(lastSearch);
//    	String output = "";
//    	for (Item item : lastSearch) {
//    		output += item.getTitle() + "\t" + item.getPrice() + "\t" + item.getUrl() + "\n";
//    	}
//    	textAreaConsole.setText(output);
//    	updateSearchLists(lastSearch);
//    	getItemsAndDisplay(false);
//    	createTable(lastSearch);
    } 
    
    /**
     * test method, call reloadLastSearch()
     * @author ckchuad
     */
    public void testReloadLastSearch() {
    	reloadLastSearch();
    }
    
    /**
     * get texts from textFieldConsole
     * @author ckchuad
     * @return output
     */
    public String getConsoleText() {
    	return textAreaConsole.getText();
    }
    // end by ckchuad, task 6
    
    /**
     * 	This function is to handle button click, called by clicking the button "refine"
     * 
     * @author lyleungad
     */
    @FXML
    private void refineButtonClick() {
    	// if currSearch is null, return
    	if(currSearch == null) {
    		refineButton.setDisable(true);
    		return;
    	}
    	// refine the currSearch list with keyword in text field
    	refineSearch(currSearch, textFieldKeyword.getText().trim());
    	// update the UI with new items list
    	updateUI(currSearch);
    	// set the button disable
    	refineButton.setDisable(true);
    }
    
    /**
     *	This function is to filter the items in list
     *	
     * @author lyleungad
     * @param target the list of items
     * @param filter keep the Item if contains the filter
     */
    private void refineSearch(List<Item> target, String filter) {
    	// help to remove the items in the list
    	Predicate<Item> pred = p-> p.getTitle().indexOf(filter) == -1;
    	// remove if condition meet
    	target.removeIf(pred);
    }
    
    /**
     * 	This function is helper function to test refineSearch
     * 
     * @author lyleungad
     * @param items the list of items to be tested
     * @param k the keyword to be filtered
     * @return the size of currSearch after filtering
     */
    public int testRefineSearch(List<Item> items, String k) {
    	refineSearch(items, k);
    	return items.size();
    }
	
	// hyperlink helper function
	/**
	 * used in task 1<br>
	 * opens the url specified in url in a new browser window<br>
	 * call method:<br>
	 * item.getUrl().addEventHandler(ActionEvent.ACTION, (e) -&gt; openDoc(item.getUrlText()));<br>
	 * or<br>
	 * openDoc(label.getText());
	 * @author ckchuad, awtang
	 * @param url the URL string one wants to open in the browser
	 */
	public void openDoc(String url) {
		if (url != "") {
			host.showDocument(url);
		}
	}
}