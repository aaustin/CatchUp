package gsb.getcatchup.catchup;

import java.util.Comparator;

public class ContactComparator implements Comparator<Contact> {

	public int compare(Contact lhs, Contact rhs) {	
		if (lhs.catchupdate.before(rhs.catchupdate))
			return -1;
		else if (lhs.catchupdate.after(rhs.catchupdate))
			return 1;
		else
			return 0;
	}

}
