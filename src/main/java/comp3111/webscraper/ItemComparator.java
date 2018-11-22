package comp3111.webscraper;

// New imports, by awtang
import java.util.Comparator;

/**
 * This class enables us to compare two items so that we can show the items in console tab in the right order
 * @author awtang
 */
public class ItemComparator implements Comparator<Item> {
	/**
	 * Compare two items. Used in task 2.
	 * @author awtang
	 * @param i1 the first item
	 * @param i2 the second item
	 * @return -1 if i1 &lt; i2, 0 if i1 = i2, 1 if i1 &gt; i2
	 */
	@Override
	public int compare(Item i1, Item i2){
		int priceCmp = Double.compare(i1.getPrice(),i2.getPrice());
		if (priceCmp != 0) {
			return priceCmp;
		}
		if ((i1.getPortal() == "Amazon") && (i2.getPortal() == "Craigslist")) {
			return 1;
		} else if ((i1.getPortal() == "Craigslist") && (i2.getPortal() == "Amazon")) {
			// To play safe
			return -1;
		}
		return 0;
	}
}
