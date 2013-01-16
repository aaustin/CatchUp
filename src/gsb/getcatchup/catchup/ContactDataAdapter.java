package gsb.getcatchup.catchup;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ContactDataAdapter {

	 private SQLiteDatabase db;
	 private DatabaseHelper dbHelper;
	 private String[] contactColumns;
	 private String[] subscriptionColumns;
	 
	 public ContactDataAdapter(Context context) {
		 dbHelper = new DatabaseHelper(context);
		 contactColumns = new String[]{
			DatabaseHelper.COLUMN_ID,
			DatabaseHelper.COLUMN_FIRSTNAME,
			DatabaseHelper.COLUMN_LASTNAME,
			DatabaseHelper.COLUMN_EMAIL,
			DatabaseHelper.COLUMN_PHONE,
			DatabaseHelper.COLUMN_BIRTHDAY,
			DatabaseHelper.COLUMN_LASTCONTACT,
			DatabaseHelper.COLUMN_SOURCE,
			DatabaseHelper.COLUMN_SOURCE_ID,
			DatabaseHelper.COLUMN_PORTRAIT,
			DatabaseHelper.COLUMN_SUBINDEX
		 };
		 
		 subscriptionColumns = new String[]{
			DatabaseHelper.COLUMN_ID,
			DatabaseHelper.COLUMN_CONTACTID,
			DatabaseHelper.COLUMN_CATCHUPDATE,
			DatabaseHelper.COLUMN_CATCHUPINTERVAL
		 };		 
	 }
	 
	 public void open() throws SQLException {
		 db = dbHelper.getWritableDatabase();
	 }
	 
	 public void close() {		
		 db.close();
	 }
	 
	 public void begin_transactions() {
		 db.beginTransaction();
	 }
	 
	 public void end_transactions() {
		 db.setTransactionSuccessful();
		 db.endTransaction();
	 }
	 
	 public int add_new_contact(Contact contact) {
		 if (!db.isOpen())
			 open();
		 
		 Cursor cursor = db.query(DatabaseHelper.TABLE_CONTACTS,
			        contactColumns, 
			        DatabaseHelper.COLUMN_FIRSTNAME + "='" + contact.firstname + "' AND " 
			        + DatabaseHelper.COLUMN_LASTNAME + "='" + contact.lastname + "'", 
			        null, null, null, null);
		 int id = -1;
		 if(cursor.moveToFirst()) { // contact exists
			 id = cursor.getInt(0);
			 String filter = "_id=" + id;
			 ContentValues args = new ContentValues();
			 
			 // add in a new source
			 String sourcestr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SOURCE));
			 if (!sourcestr.contains(contact.datasource))
				 args.put(DatabaseHelper.COLUMN_SOURCE, sourcestr + ',' + contact.datasource);

			 // update email if not present
			 String emailstr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL));
			 if (!contact.email.equals("none")) 
				 if (emailstr.equals("none"))
					 args.put(DatabaseHelper.COLUMN_EMAIL, contact.email);
				 else
					 args.put(DatabaseHelper.COLUMN_EMAIL, emailstr + ',' + contact.email);
			 	 
			 
			 // update phone if not present
			 String phonestr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE));
			 if (!contact.phone.equals("none"))
				 if (phonestr.equals("none") )
					 args.put(DatabaseHelper.COLUMN_PHONE, contact.phone);
				 else
					 args.put(DatabaseHelper.COLUMN_PHONE, phonestr + "," + contact.phone);
			 
			 // update birthday if not present
			 if (cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BIRTHDAY)).equals("none") 
					 && !contact.birthday.equals("none")) 
				 args.put(DatabaseHelper.COLUMN_BIRTHDAY, contact.birthday);
			 
			 // update last contact if not present
			 if (cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LASTCONTACT)).equals("none") 
					 && !contact.lastcontact.equals("none")) 
				 args.put(DatabaseHelper.COLUMN_LASTCONTACT, contact.lastcontact);
			 
			 if (cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COLUMN_PORTRAIT)) == null 
					 && contact.portrait != null) 
				 args.put(DatabaseHelper.COLUMN_PORTRAIT, contact.portrait);
			 
			 if (cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SOURCE_ID)).equals("none")
					 && contact.datasourceid != "none" || contact.datasource.equals("fb")) 
				 args.put(DatabaseHelper.COLUMN_SOURCE_ID, contact.datasourceid);
			 
			 if (args.size() > 0)
				 db.update(DatabaseHelper.TABLE_CONTACTS, args, filter, null);
			 
		 } else { // contact does not exist
			 ContentValues values = new ContentValues();
			 values.put(DatabaseHelper.COLUMN_FIRSTNAME, contact.firstname);
			 values.put(DatabaseHelper.COLUMN_LASTNAME, contact.lastname);
			 values.put(DatabaseHelper.COLUMN_EMAIL, contact.email);
			 values.put(DatabaseHelper.COLUMN_PHONE, contact.phone);
			 values.put(DatabaseHelper.COLUMN_BIRTHDAY, contact.birthday);
			 values.put(DatabaseHelper.COLUMN_LASTCONTACT, contact.lastcontact);
			 values.put(DatabaseHelper.COLUMN_PORTRAIT, contact.portrait);
			 values.put(DatabaseHelper.COLUMN_SOURCE, contact.datasource);
			 values.put(DatabaseHelper.COLUMN_SOURCE_ID, contact.datasourceid);
			 values.put(DatabaseHelper.COLUMN_SUBINDEX, contact.subindex);
			 id = (int) db.insert(DatabaseHelper.TABLE_CONTACTS, null, values);
		 }
		 
		 cursor.close();
		 
		 return id;
	 }
	 
	 public void subscribe(Contact contact) {
		 //Log.i(DatabaseHelper.class.getName(),"subscribing index " + contact.subindex +" for " + contact.firstname + " " + contact.lastname);
		 
		 Cursor cursor = db.query(DatabaseHelper.TABLE_SUBSCRIPTIONS,
			        subscriptionColumns, 
			        DatabaseHelper.COLUMN_CONTACTID + "=" + contact.index, 
			        null, null, null, null);
		 
		 if (cursor.moveToFirst()) {
			 int id = cursor.getInt(0);
			 String filter = "_id=" + id;
			 ContentValues args = new ContentValues();
			 args.put(DatabaseHelper.COLUMN_CATCHUPDATE, contact.catchupdate.getTimeInMillis());
			 args.put(DatabaseHelper.COLUMN_CATCHUPINTERVAL, contact.catchupinterval);
			 db.update(DatabaseHelper.TABLE_SUBSCRIPTIONS, args, filter, null);
		 } else {
			 ContentValues values = new ContentValues();
			 values.put(DatabaseHelper.COLUMN_CONTACTID, contact.index);
			 values.put(DatabaseHelper.COLUMN_CATCHUPDATE, contact.catchupdate.getTimeInMillis());			
			 values.put(DatabaseHelper.COLUMN_CATCHUPINTERVAL, contact.catchupinterval);
			 db.insert(DatabaseHelper.TABLE_SUBSCRIPTIONS, null, values);
		 }
		 
		 cursor = db.query(DatabaseHelper.TABLE_CONTACTS,
				 contactColumns, 
			        DatabaseHelper.COLUMN_ID + "=" + contact.index, 
			        null, null, null, null);
		 String indexfilter = "_id=" + contact.index;
		 ContentValues updateargs = new ContentValues();
		 updateargs.put(DatabaseHelper.COLUMN_SUBINDEX, contact.subindex);
		 db.update(DatabaseHelper.TABLE_CONTACTS, updateargs, indexfilter, null);
		 
		 cursor.close();
	 }
	 
	 public void unsubscribe(Contact contact) {
		 //Log.i(DatabaseHelper.class.getName(),"unsubscribing index " + contact.subindex +" for " + contact.firstname + " " + contact.lastname);
		 
		 db.delete(DatabaseHelper.TABLE_SUBSCRIPTIONS, 
				 DatabaseHelper.COLUMN_CONTACTID + "=" + contact.index, null);
		 
		 String filter = "_id=" + contact.index;
		 ContentValues args = new ContentValues();
		 args.put(DatabaseHelper.COLUMN_SUBINDEX, -1);
		 db.update(DatabaseHelper.TABLE_CONTACTS, args, filter, null);
	 }
	 	 	 
	 public ArrayList<Contact> get_all_subscriptions() {
		 if (!db.isOpen())
			 open();
		 
		 ArrayList<Contact> subs = new ArrayList<Contact>();
		 Contact currContact;
		 Cursor cursor = db.query(DatabaseHelper.TABLE_SUBSCRIPTIONS,
			        subscriptionColumns, null, null, null, null, null); 
		 
		 if(cursor.moveToFirst()) { 
			 Cursor currcursor;
			 do {
				 currContact = new Contact();
				 currContact.index = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CONTACTID));
				 currContact.catchupdate = currContact.convert_to_calendar(
						 cursor.getString(
								 cursor.getColumnIndex(
										 DatabaseHelper.COLUMN_CATCHUPDATE)));
				 currContact.catchupinterval = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATCHUPINTERVAL));
				 
				 currcursor = db.query(DatabaseHelper.TABLE_CONTACTS,
					        contactColumns, DatabaseHelper.COLUMN_ID + "=" + currContact.index, null, null, null, null); 
				 currcursor.moveToFirst();
				 currContact.firstname = currcursor.getString(currcursor.getColumnIndex(DatabaseHelper.COLUMN_FIRSTNAME));
				 currContact.lastname = currcursor.getString(currcursor.getColumnIndex(DatabaseHelper.COLUMN_LASTNAME));
				 currContact.email = currcursor.getString(currcursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL));
				 currContact.phone = currcursor.getString(currcursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE));
				 currContact.birthday = currcursor.getString(currcursor.getColumnIndex(DatabaseHelper.COLUMN_BIRTHDAY));
				 currContact.lastcontact = currcursor.getString(currcursor.getColumnIndex(DatabaseHelper.COLUMN_LASTCONTACT));
				 currContact.datasource = currcursor.getString(currcursor.getColumnIndex(DatabaseHelper.COLUMN_SOURCE));
				 currContact.datasourceid = currcursor.getString(currcursor.getColumnIndex(DatabaseHelper.COLUMN_SOURCE_ID));
				 currContact.portrait = currcursor.getBlob(currcursor.getColumnIndex(DatabaseHelper.COLUMN_PORTRAIT));
				 currContact.subindex = currcursor.getInt(currcursor.getColumnIndex(DatabaseHelper.COLUMN_SUBINDEX));
				 
				 subs.add(currContact);
			 } while (cursor.moveToNext());
			 currcursor.close();
		 }
		 cursor.close();
		 return subs;
	 }
	 
	 public ArrayList<Contact> get_all_contacts(String filter) {
		 if (!db.isOpen())
			 open();
		 
		 ArrayList<Contact> cons = new ArrayList<Contact>();
		 Contact currContact;
		 Cursor cursor = null;
		 if (filter.equals("everyone"))
			 cursor = db.query(DatabaseHelper.TABLE_CONTACTS,
					 contactColumns, DatabaseHelper.COLUMN_SOURCE + " LIKE '%" + filter + "%' OR '%custom%'", null, null, null, DatabaseHelper.COLUMN_FIRSTNAME + ", " + DatabaseHelper.COLUMN_LASTNAME);
		 else
			 cursor = db.query(DatabaseHelper.TABLE_CONTACTS,
				 contactColumns, DatabaseHelper.COLUMN_SOURCE + " LIKE '%" + filter + "%'", null, null, null, DatabaseHelper.COLUMN_FIRSTNAME + ", " + DatabaseHelper.COLUMN_LASTNAME); 
		 
		 if(cursor.moveToFirst()) { 
			 do {
				 currContact = new Contact();
				 currContact.index = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));				 
				 currContact.firstname = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_FIRSTNAME));
				 currContact.lastname = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LASTNAME));
				 currContact.email = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL));
				 currContact.phone = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE));
				 currContact.birthday = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BIRTHDAY));
				 currContact.lastcontact = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LASTCONTACT));
				 currContact.datasource = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SOURCE));
				 currContact.datasourceid = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SOURCE_ID));
				 currContact.portrait = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COLUMN_PORTRAIT));
				 currContact.subindex = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_SUBINDEX));
				 currContact.catchupdate = null;
				 
				 cons.add(currContact);
			 } while (cursor.moveToNext());
		 }
		 
		 cursor.close();
		 return cons;
	 }
	 
	 public Contact get_contact(int index) {
		 
		 if (!db.isOpen())
			 open();
		 
		 Contact currContact = new Contact();
		 
		 Cursor currcursor = db.query(DatabaseHelper.TABLE_CONTACTS,
			        contactColumns, DatabaseHelper.COLUMN_ID + "=" + index, null, null, null, null); 
		 currcursor.moveToFirst();
		 currContact.index = index;
		 currContact.firstname = currcursor.getString(currcursor.getColumnIndex(DatabaseHelper.COLUMN_FIRSTNAME));
		 currContact.lastname = currcursor.getString(currcursor.getColumnIndex(DatabaseHelper.COLUMN_LASTNAME));
		 currContact.email = currcursor.getString(currcursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL));
		 currContact.phone = currcursor.getString(currcursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE));
		 currContact.birthday = currcursor.getString(currcursor.getColumnIndex(DatabaseHelper.COLUMN_BIRTHDAY));
		 currContact.lastcontact = currcursor.getString(currcursor.getColumnIndex(DatabaseHelper.COLUMN_LASTCONTACT));
		 currContact.datasource = currcursor.getString(currcursor.getColumnIndex(DatabaseHelper.COLUMN_SOURCE));
		 currContact.datasourceid = currcursor.getString(currcursor.getColumnIndex(DatabaseHelper.COLUMN_SOURCE_ID));
		 currContact.portrait = currcursor.getBlob(currcursor.getColumnIndex(DatabaseHelper.COLUMN_PORTRAIT));
		 currContact.subindex = currcursor.getInt(currcursor.getColumnIndex(DatabaseHelper.COLUMN_SUBINDEX));
		 
		 currcursor = db.query(DatabaseHelper.TABLE_SUBSCRIPTIONS,
			        subscriptionColumns, DatabaseHelper.COLUMN_CONTACTID + "=" + index, null, null, null, null); 
		 if(currcursor.moveToFirst()) { 
			 currContact.catchupdate = currContact.convert_to_calendar(
					 currcursor.getString(
							 currcursor.getColumnIndex(
									 DatabaseHelper.COLUMN_CATCHUPDATE)));
			 currContact.catchupinterval = currcursor.getInt(currcursor.getColumnIndex(DatabaseHelper.COLUMN_CATCHUPINTERVAL));
		 }
		 currcursor.close();
		 
		 return currContact;
	 }
}

