package com.zerotwoonelabs.picafxfreev2.tools;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zerotwoonelabs.picafxfreev2.R;
import com.zerotwoonelabs.picafxfreev2.Static;

public class ToolsMenu extends Fragment implements OnClickListener , OnFocusChangeListener{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.frag_tools_menu, container, false);
		
		LinearLayout toolsmenu = (LinearLayout) v.findViewById(R.id.layToolsMenu);
		
		for(int i = 0; i < toolsmenu.getChildCount(); i++){
			toolsmenu.getChildAt(i).setOnClickListener(this);
			toolsmenu.getChildAt(i).setOnFocusChangeListener(this);
		}
		
		return v;
	}
	
	/*public enum ToolList{
		PenTool,
		EffectsTool,
		CropTool,
		PreEffects,
		ShareTool,
		SaveTool,
		TextTool,
		PropertiesTool,
		PathHistroryTool,
		InsertTool
	}*/

	@Override
	public void onClick(View v) {
		
		Fragment frag = null; 
		switch (v.getId()) {
		case R.id.txtPenTool:
			frag = new PenToolMenu();
			break;
		case R.id.txtEffectsTool:
			frag = new EffectToolMenu();
			break;
		case R.id.txtCropTool:
			frag = new CropToolMenu();
			break;
		case R.id.txtPathHistory:
			frag = new PathHistoryToolMenu();
			break;
		case R.id.txtPreEffects:
			frag = new PreEffectToolMenu();
			break;
		case R.id.txtSaveTool:
			frag = new SaveTool();
			break;
		/*case R.id.txtTextTool:
			frag = new TextToolMenu();
			break;*/
		//case R.id.txtProperties:
			//frag = new PropertiesTool();
		//	break;
		default:
			break;
		}
		if (frag != null)
			Static.getTransaction(getActivity()).replace(R.id.layoutFrameBottom, frag).commit();
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		
		if (hasFocus && !getActivity().isChangingConfigurations()){
			v.performClick();
		}
	}	
}
