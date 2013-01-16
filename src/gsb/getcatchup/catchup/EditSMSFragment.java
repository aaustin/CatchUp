package gsb.getcatchup.catchup;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditSMSFragment extends Fragment {
	FragmentJuggler FJ;
	PrefHelper pHelper;
	Button cmdSave;
	TextView txtLength;
	EditText txtContent;
	public EditSMSFragment(FragmentJuggler fj, Button save) {
		this.FJ = fj;
		this.cmdSave = save;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.edit_sms_view, container, false);
		txtContent = (EditText) v.findViewById(R.id.txtSMSContent);
		txtLength = (TextView) v.findViewById(R.id.txtLength);
		pHelper = new PrefHelper(getActivity());
		
		txtContent.setText(pHelper.get_sms_content());
		txtLength.setText(txtContent.getText().toString().length() + "/160");
		
		txtContent.addTextChangedListener(new TextWatcher() {	

			public void afterTextChanged(Editable s) {
				txtLength.setText(txtContent.getText().toString().length() + "/160");				
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {			
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}			
		});
		
		cmdSave.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {		
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
					    Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
				pHelper.set_sms_content(txtContent.getText().toString());
				FJ.go_back();
			}			
		});
		
		return v;
	}
}
