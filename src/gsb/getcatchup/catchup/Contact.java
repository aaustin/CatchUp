package gsb.getcatchup.catchup;

import java.util.Calendar;
import java.util.Date;

public class Contact {
	public int index;
	public byte[] portrait;
	public String firstname;
	public String lastname;
	public String email;
	public String phone;
	public String birthday;
	public String lastcontact;
	public String datasource;
	public String datasourceid;
	public int subindex;
	public int catchupinterval;
	public Calendar catchupdate;
	
	public Contact() {
		this.index = -1;
		this.portrait = null;
		this.firstname = "none";
		this.lastname = "none";
		this.email = "none";
		this.phone = "none";
		this.birthday = "none";
		this.lastcontact = "none";
		this.datasource = "none";
		this.datasourceid = "none";
		this.subindex = -1;
		this.catchupinterval = -1;
		this.catchupdate = null;
	}
	
	public Contact(int index, byte[] portrait, String firstname, String lastname, String email, String phone, String birthday, String lastcontact, String datasource, String datasourceid, int subindex, int catchupinterval, String catchupdate) {
		this.index = index;
		this.portrait = portrait;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.phone = phone;
		this.birthday = birthday;
		this.lastcontact = lastcontact;
		this.datasource = datasource;
		this.datasourceid = datasourceid;
		this.subindex = subindex;
		this.catchupinterval = catchupinterval;
		this.catchupdate = convert_to_calendar(catchupdate);
	}
	
	public Contact clone() {
		Contact con = new Contact();
		con.index = this.index;
		con.portrait = this.portrait.clone();
		con.firstname = this.firstname;
		con.lastname = this.lastname;
		con.email = this.email;
		con.phone = this.phone;
		con.birthday = this.birthday;
		con.datasource = this.datasource;
		con.datasourceid = this.datasourceid;
		con.subindex = this.subindex;
		con.catchupdate = (Calendar) this.catchupdate.clone();
		con.catchupinterval = this.catchupinterval;
		con.lastcontact = this.lastcontact;
		return con;
	}
	
	public Calendar convert_to_calendar(String date) {
		Calendar ret = Calendar.getInstance();
		Date dt;
		try {
			dt = new Date(Long.valueOf(date));
			ret.setTime(dt);
		} catch (Exception e) {
			ret = Calendar.getInstance();
			e.printStackTrace();
		}
		return ret;
	}
	
}
