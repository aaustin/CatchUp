package gsb.getcatchup.catchup;

import gsb.getcatchup.catchup.FacebookContacts.ImportDoneFB;
import gsb.getcatchup.catchup.SplashActivity.ImportDone;

import java.util.Date;
import java.util.List;

import com.facebook.GraphUser;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;

public class ParseContacts {
	
	Context context;
	ContactDataAdapter cda;
	ImportDone callback;
	ImportDoneFB fbcallback;
	
	
	public ParseContacts(Context context, ImportDone callback, ImportDoneFB fbcallback) {
		this.context = context;
		this.callback = callback;
		this.fbcallback = fbcallback;
		cda = new ContactDataAdapter(context);
	}
	
	public void download_facebook_contacts(List<GraphUser> users) {
		cda.open();
		cda.begin_transactions();		

		Contact curr = new Contact();
		curr.datasource = "fb";
		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getName().length() > 0) {				
				curr = solve_names(users.get(i).getName(), curr);			
				curr.datasourceid = users.get(i).getId();
				cda.add_new_contact(curr);
			}
		}
		
		cda.end_transactions();		
		cda.close();		

		users = null;
		
		fbcallback.callback(1); // parse contacts done
	}
	
	private Contact solve_names(String name, Contact contact) {
		//Log.i(DatabaseHelper.class.getName(),"name: " + name);
		String[] split = name.split(" ");
		String firstname = "";
		String lastname = split[split.length-1];		
		for(int i = 0; i < split.length-1; i++)
			firstname = firstname + split[i] + " ";
		
		contact.firstname = firstname.trim();
		contact.lastname = lastname;
		
		contact.firstname = contact.firstname.substring(0, 1).toUpperCase() + contact.firstname.substring(1);
		contact.lastname = contact.lastname.substring(0, 1).toUpperCase() + contact.lastname.substring(1);
		contact.firstname = contact.firstname.replace("'", "''");
		contact.lastname = contact.lastname.replace("'", "''");		
				
		return contact;
	}
	
	public void faster_download_phone_contacts() {
		cda.open();
		cda.begin_transactions();
		//Log.i(DatabaseHelper.class.getName(),"STARTED PARSING");	
				
		ContentResolver contentResolver = context.getContentResolver();
		
		String[] PROJECTION = new String[] {
				ContactsContract.Data.CONTACT_ID,"data3", "data2"/*, "data1", "data15"*/
    	};
    	
    	Cursor data = contentResolver.query(ContactsContract.Data.CONTENT_URI, PROJECTION, 
    			ContactsContract.Data.MIMETYPE + "=?", 
    			new String[]{StructuredName.CONTENT_ITEM_TYPE},    					
    			null);
    	
    	boolean goodData = true;
    	
    	//Log.i(DatabaseHelper.class.getName(),"data count: " + data.getCount());	
    	Contact currContact = new Contact();
		currContact.datasource = "phone";
    	if (data.getCount() > 0) {
	   		while(data.moveToNext()) { 
	   			currContact.datasourceid = String.valueOf(data.getInt(data.getColumnIndex(StructuredName.CONTACT_ID)));
	   			currContact.firstname = data.getString(data.getColumnIndex(StructuredName.GIVEN_NAME));
	   			currContact.lastname = data.getString(data.getColumnIndex(StructuredName.FAMILY_NAME));
	   			if (currContact.firstname == null || currContact.lastname == null) {
	   				goodData = false;
	   			} else if (currContact.firstname.length() > 0 && currContact.lastname.length() > 0){
	   				//Log.i(DatabaseHelper.class.getName(),"Assigned name: " + currContact.firstname + " " + currContact.lastname);
		   			currContact.firstname = currContact.firstname.substring(0, 1).toUpperCase() + currContact.firstname.substring(1);
		   			currContact.lastname = currContact.lastname.substring(0, 1).toUpperCase() + currContact.lastname.substring(1);
		   			currContact.firstname = currContact.firstname.replace("'", "''");
		   			currContact.lastname = currContact.lastname.replace("'", "''");		   				   			  
	   			} else
	   				goodData = false;
	   			
	   			if (goodData) 	            	
	   				cda.add_new_contact(currContact); 
	   			
	   			goodData = true;
   		   	 }   		   	 
   		}            		
   	 	           	
    	data.close();
    	
		//Log.i(DatabaseHelper.class.getName(),"FINISHED PARSING");
		cda.end_transactions();		
		cda.close();		
		
		callback.callback(0); // parse contacts done
	}
	
	public void download_phone_contacts() {

		cda.open();
		cda.begin_transactions();
		
		ContentResolver contentResolver = context.getContentResolver();
		Uri contactUri = ContactsContract.Contacts.CONTENT_URI;
		//Log.i(DatabaseHelper.class.getName(),"STARTED PARSING");			
		
		String[] PROJECTION = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LAST_TIME_CONTACTED,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
		};

		Cursor contacts = contentResolver.query(contactUri, PROJECTION, null, null, null);

		Contact currContact = new Contact();
		currContact.datasource = "phone";
		if (contacts.getCount() > 0) {
            while(contacts.moveToNext()) {     
            	boolean goodData = true;
            	
            	String lastContact = contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED));
            	if (lastContact.length() > 0) {
	            	Date dt = new Date();
	            	dt.setTime(Long.valueOf(lastContact));
	            	lastContact = dt.toString();
            	}
            	currContact.lastcontact = lastContact;
            	
            	String contactId = contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts._ID));
            	
            	           	
            	PROJECTION = new String[] {
            			ContactsContract.Data.MIMETYPE, "data3", "data2"/*, "data1", "data15"*/
            	};
            	
            	Cursor data = contentResolver.query(ContactsContract.Data.CONTENT_URI, PROJECTION, 
            			ContactsContract.Data.CONTACT_ID + "=? AND (" 
            					+ ContactsContract.Data.MIMETYPE + "=?)", 
            			new String[]{String.valueOf(contactId), 
            					StructuredName.CONTENT_ITEM_TYPE}, 
            			null);
            	
            	/*Cursor data = contentResolver.query(ContactsContract.Data.CONTENT_URI, PROJECTION, 
            			ContactsContract.Data.CONTACT_ID + "=? AND (" 
            					+ ContactsContract.Data.MIMETYPE + "=? OR " 
            					+ ContactsContract.Data.MIMETYPE + "=? OR " 
            					+ ContactsContract.Data.MIMETYPE + "=? OR " 
            					+ ContactsContract.Data.MIMETYPE + "=?)", 
            			new String[]{String.valueOf(contactId), 
            					StructuredName.CONTENT_ITEM_TYPE, 
            					Phone.CONTENT_ITEM_TYPE, 
            					Email.CONTENT_ITEM_TYPE, 
            					Photo.CONTENT_ITEM_TYPE}, 
            			null);*/

            	if (data.getCount() > 0) {
            		 while(data.moveToNext()) {        			 
            				
            			 String mimetype = data.getString(data.getColumnIndex(ContactsContract.Data.MIMETYPE));
            			 	            			 
            			 if (mimetype.equals(StructuredName.CONTENT_ITEM_TYPE)) {
            				 currContact.firstname = data.getString(data.getColumnIndex(StructuredName.GIVEN_NAME));
            				 currContact.lastname = data.getString(data.getColumnIndex(StructuredName.FAMILY_NAME));
            				 if (currContact.firstname == null || currContact.lastname == null) {
            					 goodData = false;
            					 break;
            				 }         
            				 currContact.firstname = currContact.firstname.substring(0, 1).toUpperCase() + currContact.firstname.substring(1);
            				 currContact.lastname = currContact.lastname.substring(0, 1).toUpperCase() + currContact.lastname.substring(1);
            				 currContact.firstname = currContact.firstname.replace("'", "''");
            				 currContact.lastname = currContact.lastname.replace("'", "''");
            				 //Log.i(DatabaseHelper.class.getName(),"Assigned name: " + currContact.firstname + " " + currContact.lastname);
            			 } /*else if (mimetype.equals(Phone.CONTENT_ITEM_TYPE)) {
            				 currContact.phone = data.getString(data.getColumnIndex(Phone.NUMBER));
            				 currContact.phone = currContact.phone.replace("-", "");
            				 currContact.phone = currContact.phone.replace("(", "");
            				 currContact.phone = currContact.phone.replace(")", "");
            				 currContact.phone = currContact.phone.replace(" ", "");
            				 currContact.phone = currContact.phone.replace("'", "''");
            				 //Log.i(DatabaseHelper.class.getName(),"Assigned phone: " + currContact.phone);
            			 } else if (mimetype.equals(Email.CONTENT_ITEM_TYPE)) {
            				 currContact.email = data.getString(data.getColumnIndex(Email.ADDRESS));
            				 currContact.email = currContact.email.replace("'", "''");
            				 //Log.i(DatabaseHelper.class.getName(),"Assigned phone: " + currContact.phone);
            			 } else if (mimetype.equals(Photo.CONTENT_ITEM_TYPE)) {
            				currContact.portrait = data.getBlob(data.getColumnIndex(Photo.PHOTO));
            				// Log.i(DatabaseHelper.class.getName(),"Assigned portrait");
            			 }*/ 
            			 
            		 }            		
            	}
            	
            	if (goodData) {	            	
            		cda.add_new_contact(currContact);
	            	
            	}
            	           	
            	data.close();
            	
            	
            }           

		}
		//Log.i(DatabaseHelper.class.getName(),"FINISHED DOWNLOADING CONTACTS TO ARRAY LIST");
				
		
		contacts.close();
		//Log.i(DatabaseHelper.class.getName(),"FINISHED PARSING");
		cda.end_transactions();		
		cda.close();		
		
		callback.callback(0); // parse contacts done
	}
	
}
