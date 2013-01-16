package gsb.getcatchup.catchup;

import android.app.ProgressDialog;
import android.content.Context;

public class NetworkProgressBar {

	ProgressDialog progressBar;
	
	Context ctx;
	
	public NetworkProgressBar(Context context) {		
		ctx = context;		
		
		progressBar = new ProgressDialog(ctx);
		progressBar.setCancelable(false);
		progressBar.setProgressStyle(ProgressDialog.THEME_HOLO_LIGHT);
	}
	
	public boolean is_open() {
		return progressBar.isShowing();
	}
	
	public void show(String message) {
		progressBar.setMessage(message);
		progressBar.show();
	}
	
	public void change_message(String message) {
		progressBar.setMessage(message);
	}
	
	public void hide() {
		progressBar.dismiss();
	}
	
}
