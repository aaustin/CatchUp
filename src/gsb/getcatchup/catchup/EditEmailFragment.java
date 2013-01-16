package gsb.getcatchup.catchup;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class EditEmailFragment extends Fragment {

	FragmentJuggler FJ;
	
	PrefHelper pHelper;
	
	EditText txtSubject;
	EditText txtContent;
	Button cmdSave;
	
	public EditEmailFragment(FragmentJuggler fj, Button save) {
		this.FJ = fj;
		this.cmdSave = save;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.edit_email_view, container, false);
		
		txtSubject = (EditText) v.findViewById(R.id.txtEmailSubject);
		txtContent = (EditText) v.findViewById(R.id.txtEmailContent);
		
		pHelper = new PrefHelper(getActivity());
		
		txtSubject.setText(pHelper.get_email_subject());
		txtContent.setText(pHelper.get_email_content());
		
		cmdSave.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
					    Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
				pHelper.set_email_subject(txtSubject.getText().toString());				
				pHelper.set_email_content(txtContent.getText().toString());
				FJ.go_back();
			}			
		});
		
		return v;
	}
	
}
