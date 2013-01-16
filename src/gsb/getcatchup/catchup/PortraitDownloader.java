package gsb.getcatchup.catchup;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Stack;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Photo;

public class PortraitDownloader {
	final static int MAX_ALLOWED_TASKS = 20;
	ArrayList<String> inProgress;
	ArrayList<Contact> contacts;
	Stack<String> queue;	
	Stack<Integer> queueIndex;
	Stack<String> queueName;	
	int runningCount = 0;
	
	ContactDataAdapter cda;
	
	Context context;
	
	public PortraitDownloader(Context context, ArrayList<Contact> contacts) {
		this.contacts = contacts;
		inProgress = new ArrayList<String>();
		queue = new Stack<String>();
		queueIndex = new Stack<Integer>();
		queueName = new Stack<String>();
		this.context = context;
		
		cda = new ContactDataAdapter(context);
	}
	
	
	public void get_image(int index) {
		Contact contact = contacts.get(index);
		//Log.i(DatabaseHelper.class.getName(),"datasource " + contact.datasource);
		
        if (!inProgress.contains(contact.datasourceid) && contact.portrait == null) {
        	inProgress.add(contact.datasourceid);
        	
	        if (contact.datasource.contains("fb")) {
	        
				if (runningCount >= MAX_ALLOWED_TASKS) {
		            queue.push(contact.datasourceid);
		            queueName.push("fb");
		            queueIndex.push(index);
		        } else {
		        	//Log.i(DatabaseHelper.class.getName(),"downloading " + contact.firstname + " " + contact.lastname);
		        	//Log.i(DatabaseHelper.class.getName(),"datasource " + contact.datasourceid);

		            runningCount++;
		            new GetProfilePicAsyncTask().execute(contact.datasourceid, index);
		        }
			} else if (contact.datasource.contains("phone")) {
			
				if (runningCount >= MAX_ALLOWED_TASKS) {
		            queue.push(contact.datasourceid);
		            queueName.push("phone");
		            queueIndex.push(index);
		        } else {
		        	//Log.i(DatabaseHelper.class.getName(),"downloading " + contact.firstname + " " + contact.lastname);
		        	//Log.i(DatabaseHelper.class.getName(),"datasource " + contact.datasourceid);
		        	runningCount++;
		            new GetLocalPicAsyncTask().execute(contact.datasourceid, index);
		        }   			
	        } 
        }
	}
	
	public void getNextImage() {
        if (!queue.isEmpty()) {
        	runningCount++;
            String item = queue.pop();
            if (queueName.pop().equals("fb"))
            	new GetProfilePicAsyncTask().execute(item, queueIndex.pop());
            else
            	new GetLocalPicAsyncTask().execute(item, queueIndex.pop());
        }
    }
	
	private Bitmap get_local_image(String id) {
		String[] PROJECTION = new String[] {
    			"data15"
    	};
		
		byte[] portrait = null;
		ContentResolver contentResolver = context.getContentResolver();
		Cursor data = contentResolver.query(ContactsContract.Data.CONTENT_URI, PROJECTION, 
    			ContactsContract.Data.CONTACT_ID + "=? AND (" 
    					+ ContactsContract.Data.MIMETYPE + "=?)", 
    			new String[]{String.valueOf(id), 
					Photo.CONTENT_ITEM_TYPE}, 
    			null);
		
		if (data.getCount() > 0) {
	   		while(data.moveToNext()) {
	   			portrait = data.getBlob(data.getColumnIndex(Photo.PHOTO));
	   		}
		}		
		
		data.close();
		
		Bitmap img = null;
		if (portrait != null) 
			img = BitmapFactory.decodeByteArray(portrait, 0, portrait.length);
		else
			img = BitmapFactory.decodeResource(context.getResources(), R.drawable.blank_portrait);
		
		return img;
	}
	
	private void save_contact(int index) {
		cda.open();
		cda.add_new_contact(contacts.get(index));
		cda.close();		
	}
	
	private class GetProfilePicAsyncTask extends AsyncTask<Object, Void, Bitmap> {
        String uid;
        int index;
        
        @Override
        protected Bitmap doInBackground(Object... params) {
            this.uid = (String) params[0];
            this.index = (Integer) params[1];
            
            String url = "http://graph.facebook.com/";
            url = url + uid.toString();
            url = url + "/picture";
            
            Bitmap bm = null;
            try {
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.setConnectTimeout(500);
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(new FlushedInputStream(is));
                bis.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            } 
        
            //Log.i(DatabaseHelper.class.getName(),"download finished, setting image");
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            runningCount--;
            if (result != null) {
            	getNextImage();
            	ByteArrayOutputStream stream = new ByteArrayOutputStream();
            	result.compress(Bitmap.CompressFormat.PNG, 100, stream);
            	contacts.get(index).portrait = stream.toByteArray();
            	inProgress.remove(uid);       	
            	save_contact(index);
            	//Log.i(DatabaseHelper.class.getName(),"datasource " + uid);
            }
        }
    }

	
	private class GetLocalPicAsyncTask extends AsyncTask<Object, Void, Bitmap> {
        String uid;
        int index;
        
        @Override
        protected Bitmap doInBackground(Object... params) {
            this.uid = (String) params[0];           
            this.index = (Integer) params[1];
            
            return get_local_image(this.uid);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            runningCount--;            
            if (result != null) {
            	getNextImage();
            	ByteArrayOutputStream stream = new ByteArrayOutputStream();
            	result.compress(Bitmap.CompressFormat.PNG, 100, stream);
            	contacts.get(index).portrait = stream.toByteArray();
            	save_contact(index);
            	inProgress.remove(uid);         
            }
        }
    }
	
	static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break; // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }
}
