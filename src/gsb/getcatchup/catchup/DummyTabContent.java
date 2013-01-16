package gsb.getcatchup.catchup;

import android.content.Context;
import android.view.View;
import android.widget.TabHost.TabContentFactory;
 
public class DummyTabContent implements TabContentFactory{
    private Context mContext;
 
    public DummyTabContent(Context context){
        mContext = context;
    }
 
    public View createTabContent(String tag) {
        View v = new View(mContext);
        return v;
    }
}
