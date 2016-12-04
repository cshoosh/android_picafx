package com.zerotwoonelabs.picafxfreev2.dialogviews;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zerotwoonelabs.picafxfreev2.R;

public class TipDialog extends DialogFragment {
	
	private String mMessage = "If you like this app please rate us or support us by removing the ads.";
	public static final String KEY_MESSAGE = "messageKey";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getArguments() != null) {
			String msg = getArguments().getString(KEY_MESSAGE);
			mMessage = msg != null ? msg : mMessage;
		}
		
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog diag = super.onCreateDialog(savedInstanceState);
		diag.setTitle("Tool Tip !!");
		return diag;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View ret = inflater.inflate(R.layout.tip_dialog, container, false);
		
		((TextView) ret.findViewById(R.id.txtTipMessage)).setText(mMessage);
		((Button) ret.findViewById(R.id.btnOKTip)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		return ret;
	}
}
