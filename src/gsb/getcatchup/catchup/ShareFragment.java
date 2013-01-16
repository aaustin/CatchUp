package gsb.getcatchup.catchup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class ShareFragment extends Fragment {
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.share_view, container, false);
				
		ImageView imgWebsite = (ImageView) v.findViewById(R.id.imgHeading2);
		ImageView imgComments = (ImageView) v.findViewById(R.id.imgHeading3);
		Button cmdShareFacebook = (Button) v.findViewById(R.id.cmdShareFacebook);
		Button cmdShareTwitter = (Button) v.findViewById(R.id.cmdShareTwitter);
		
		
		imgComments.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent view = new Intent(Intent.ACTION_VIEW);
	    		StringBuilder uri = new StringBuilder("mailto:");
	    		uri.append("ideas@trycatchup.com");
	    		uri.append("?subject=").append("CatchUp Feedback");
	    		uri.append("&body=").append("");
	    		view.setData(Uri.parse(uri.toString()));
	    		getActivity().startActivity(view);
			}			
		});
		
		imgWebsite.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.trycatchup.com"));
				startActivity(browserIntent);
			}			
		});
		
		OnClickListener like = new OnClickListener() {
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/CatchUpMobile"));
				startActivity(browserIntent);
			}
		};
		OnClickListener follow = new OnClickListener() {
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.twitter.com/CatchUpMobile"));
				startActivity(browserIntent);
			}
		};
		
		cmdShareFacebook.setOnClickListener(like);
		cmdShareTwitter.setOnClickListener(follow);
		
        return v;
    }

}
