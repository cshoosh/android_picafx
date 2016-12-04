package com.zerotwoonelabs.picafxfreev2.dialogviews;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zerotwoonelabs.picafxfreev2.R;

public class AskConfirmation extends DialogFragment implements OnClickListener{
	private Runnable mYesRunnable;
	private String  mMessage;
	
	public AskConfirmation() {
	}
	
	public AskConfirmation(Runnable yestask) {
		mYesRunnable = yestask;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
			mMessage = getArguments().getString("message");
		else
			mMessage = "Are you sure?";
		
		if (mYesRunnable == null)
			dismiss();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View ret = inflater.inflate(R.layout.confirmation_dialog, container, false);
		((TextView)ret.findViewById(R.id.txtMessage)).setText(mMessage);
		
		ret.findViewById(R.id.btnCancelConfirm).setOnClickListener(this);
		ret.findViewById(R.id.btnOKConfirm).setOnClickListener(this);
		
		getDialog().setTitle("Confirm !!");
		return ret;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnOKConfirm)
			mYesRunnable.run();		
		dismiss();
	}
}
