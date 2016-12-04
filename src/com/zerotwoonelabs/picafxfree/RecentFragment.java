package com.zerotwoonelabs.picafxfree;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zerotwoonelabs.picafxfree.support.BMPHandler;
import com.zerotwoonelabs.picafxfree.support.RecentStruct;
import com.zerotwoonelabs.picafxfreev2.PathArrayHandler;
import com.zerotwoonelabs.picafxfreev2.R;
import com.zerotwoonelabs.picafxfreev2.UpdateMainActivity;

public class RecentFragment extends Fragment {

public static final String RECENT_XML_FILEPATH = "recent.xml";

// DOCUMENT TAGS
public static final String TAG_ROOT = "ROOT";
public static final String TAG_RECENT = "RECENT";

// RECENT CHILD TAGS
public static final String TAG_ID = "ID";
public static final String TAG_URI = "URI";
public static final String TAG_WIDTH = "WIDTH";
public static final String TAG_HEIGHT = "HEIGHT";
public static final String TAG_CONFIG = "CONFIG";
public static final String TAG_LAST_ACTIVE = "LASTACTIVE";

public static final String TAG_PATHLIST = "PATHLIST";
public static final String TAG_PATH = "PATH";
public static final String TAG_XY_HOLDER = "XY";
public static final String TAG_X = "X";
public static final String TAG_Y = "Y";
public static final String TAG_ACTION = "ACTION";

private static  RecentListAdapter listadapter;

private Document mainRecentDoc;

@Override
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	refresh();
}

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.activity_recent, container,
			false);
	((ListView) view.findViewById(R.id.listview_recent))
			.setAdapter(listadapter);
	return view;
}

private void refresh() {
	try {
		mainRecentDoc = getDocument(getActivity());
	} catch (ParserConfigurationException e) {
		e.printStackTrace();
		Toast.makeText(getActivity(), "Cannot retrieve information..",
				Toast.LENGTH_SHORT).show();
	}

	List<RecentStruct> values = new ArrayList<RecentStruct>();

	if (mainRecentDoc != null) {
		NodeList list = mainRecentDoc.getElementsByTagName(TAG_RECENT);
		if (list.getLength() > 0) {
			for (int i = 0; i < list.getLength(); i++) {
				RecentStruct ret = new RecentStruct();
				getFromElement((Element) list.item(i), ret);
				values.add(ret);
			}
		}
	}
	listadapter = new RecentListAdapter(getActivity(), values);
}

public static void getFromElement(Element ele, RecentStruct ret) {
	NodeList list = ele.getChildNodes();
	for (int i = 0; i < list.getLength(); i++) {
		if (list.item(i).getNodeName().equals(TAG_CONFIG))
			ret.config = Config.valueOf(list.item(i).getTextContent());
		else if (list.item(i).getNodeName().equals(TAG_HEIGHT))
			ret.height = Integer.valueOf(list.item(i).getTextContent());
		else if (list.item(i).getNodeName().equals(TAG_ID))
			ret.id = Long.valueOf(list.item(i).getTextContent());
		else if (list.item(i).getNodeName().equals(TAG_LAST_ACTIVE))
			ret.lastActive = Long
					.valueOf(list.item(i).getTextContent());
		else if (list.item(i).getNodeName().equals(TAG_URI))
			ret.uri = Uri.parse(list.item(i).getTextContent());
		else if (list.item(i).getNodeName().equals(TAG_WIDTH))
			ret.width = Integer.valueOf(list.item(i).getTextContent());
		else if (list.item(i).getNodeName().equals(TAG_PATHLIST)) {
			ArrayList<PathArrayHandler> arg = new ArrayList<PathArrayHandler>();
			readArray((Element) list.item(i), arg);
			ret.list = arg;
		}
	}
}

public static void readArray(Element array,
		ArrayList<PathArrayHandler> arrayList) {
	
	arrayList.clear();
	arrayList.add(new PathArrayHandler());
	
	NodeList pathList = array.getElementsByTagName(TAG_PATH);
	for (int i = 0; i < pathList.getLength(); i++) {
		
		NodeList xyList = ((Element) pathList.item(i))
				.getElementsByTagName(TAG_XY_HOLDER);
		
		ArrayList<Float> x = new ArrayList<Float>();
		ArrayList<Float> y = new ArrayList<Float>();
		ArrayList<Integer> action = new ArrayList<Integer>(); 
		
		for (int j = 0; j < xyList.getLength(); j++) {
			
			x.add(Float.valueOf( ((Element) xyList.item (j)).getElementsByTagName(TAG_X)
					.item(0).getTextContent()) );
			y.add(Float.valueOf( ((Element) xyList.item (j)).getElementsByTagName(TAG_Y)
					.item(0).getTextContent()) );
			action.add(Integer.valueOf( ((Element) xyList.item (j)).getElementsByTagName(TAG_ACTION)
					.item(0).getTextContent()) );
		}
		PathArrayHandler value = new PathArrayHandler();
		
		value.setX(x);
		value.setY(y);
		value.setAction(action);
		
		arrayList.add(value);
	}
}

public static void writeArray(Element array,
		ArrayList<PathArrayHandler> arrayList) {
	Document doc = array.getOwnerDocument();
	
	for (PathArrayHandler holder : arrayList) {
		if (holder != null && !holder.getX().isEmpty()) {
			Element path = doc.createElement(TAG_PATH);
			if (holder.getX() != null && holder.getX().size() > 0) {
				for (int i = 0; i < holder.getX().size() ; i++) {
					Element xy = doc.createElement(TAG_XY_HOLDER);
					
					Element xEle = doc.createElement(TAG_X);
					Element yEle = doc.createElement(TAG_Y);
					Element actionEle = doc.createElement(TAG_ACTION);

					xEle.setTextContent("" + holder.getX().get(i));
					yEle.setTextContent("" + holder.getY().get(i));
					actionEle.setTextContent("" + holder.getAction().get(i));
					
					xy.appendChild(xEle);
					xy.appendChild(yEle);
					xy.appendChild(actionEle);
					
					path.appendChild(xy);
				}
				array.appendChild(path);
			}
		}
	}
}

public static Document getDocument(Context context)
		throws ParserConfigurationException {
	Document doc;
	try {
		FileInputStream input = context
				.openFileInput(RECENT_XML_FILEPATH);
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(input);
		input.close();
		return doc;
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (SAXException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} catch (ParserConfigurationException e) {
		e.printStackTrace();
	}
	return getEmptyRecentDocument();
}

public static void addProperties(Document doc,
		final RecentStruct struct, Activity cntxt) {
	Element ret = doc.createElement(TAG_RECENT);

	Element idEle, configEle, widhtEle, heightEle, uriEle, lastEle, listEle;
	idEle = doc.createElement(TAG_ID);
	configEle = doc.createElement(TAG_CONFIG);
	widhtEle = doc.createElement(TAG_WIDTH);
	heightEle = doc.createElement(TAG_HEIGHT);
	uriEle = doc.createElement(TAG_URI);
	lastEle = doc.createElement(TAG_LAST_ACTIVE);
	listEle = doc.createElement(TAG_PATHLIST);

	writeArray(listEle, struct.list);
	
	idEle.setTextContent(String.valueOf(struct.id));
	configEle.setTextContent(struct.config.toString());
	widhtEle.setTextContent(String.valueOf(struct.width));
	heightEle.setTextContent(String.valueOf(struct.height));
	uriEle.setTextContent(struct.uri.toString());
	lastEle.setTextContent(String.valueOf(struct.lastActive));

	ret.appendChild(idEle);
	ret.appendChild(configEle);
	ret.appendChild(widhtEle);
	ret.appendChild(heightEle);
	ret.appendChild(uriEle);
	ret.appendChild(lastEle);
	ret.appendChild(listEle);

	cntxt.runOnUiThread(new Runnable() {

		@Override
		public void run() {
			if (listadapter != null)
				listadapter.add(struct);
		}
	});

	doc.getElementsByTagName(TAG_ROOT).item(0).appendChild(ret);
	writeDoc(doc, cntxt);
}

public static void writeDoc(Document doc, Context cntx) {
	try {
		DOMSource xmlSource = new DOMSource(doc);
		FileOutputStream output = cntx.openFileOutput(
				RecentFragment.RECENT_XML_FILEPATH, Context.MODE_PRIVATE);

		StreamResult outputTarget = new StreamResult(output);
		TransformerFactory.newInstance().newTransformer()
				.transform(xmlSource, outputTarget);
		output.close();
	} catch (IOException e) {
		e.printStackTrace();
	} catch (TransformerConfigurationException e) {
		e.printStackTrace();
	} catch (TransformerException e) {
		e.printStackTrace();
	} catch (TransformerFactoryConfigurationError e) {
		e.printStackTrace();
	}
}

public static void appendProperties(Element ele,
		final RecentStruct struct, Activity context) {

	Element idEle, configEle, widhtEle, heightEle, uriEle, lastEle, listEle;
	idEle = (Element) ele.getElementsByTagName(TAG_ID).item(0);
	configEle = (Element) ele.getElementsByTagName(TAG_CONFIG).item(0);
	widhtEle = (Element) ele.getElementsByTagName(TAG_WIDTH).item(0);
	heightEle = (Element) ele.getElementsByTagName(TAG_HEIGHT).item(0);
	uriEle = (Element) ele.getElementsByTagName(TAG_URI).item(0);
	lastEle = (Element) ele.getElementsByTagName(TAG_LAST_ACTIVE).item(
			0);
	listEle = (Element) ele.getElementsByTagName(TAG_PATHLIST).item(0);
	
	NodeList child = listEle.getChildNodes();
	for (int i = 0; i < child.getLength(); i++)
		listEle.removeChild(child.item(i));
	
	writeArray(listEle, struct.list);

	idEle.setTextContent(String.valueOf(struct.id));
	configEle.setTextContent(struct.config.toString());
	widhtEle.setTextContent(String.valueOf(struct.width));
	heightEle.setTextContent(String.valueOf(struct.height));
	uriEle.setTextContent(struct.uri.toString());
	lastEle.setTextContent(String.valueOf(struct.lastActive));

	context.runOnUiThread(new Runnable() {

		@Override
		public void run() {
			if (listadapter != null) {
				RecentStruct rem = null;
				for (int i = 0; i < listadapter.getCount(); i++) {
					if (listadapter.getItem(i).id == struct.id) {
						rem = listadapter.getItem(i);
					}
				}
				if (rem != null) {
					listadapter.remove(rem);
					listadapter.insert(struct, 0);
				}
			}
		}
	});

	writeDoc(ele.getOwnerDocument(), context);
}



public static Document getEmptyRecentDocument()
		throws ParserConfigurationException {
	Document doc = DocumentBuilderFactory.newInstance()
			.newDocumentBuilder().newDocument();

	Element root = doc.createElement(TAG_ROOT);
	doc.appendChild(root);
	return doc;
}

public class RecentListAdapter extends ArrayAdapter<RecentStruct>
		implements OnClickListener {

	public RecentListAdapter(Context context, List<RecentStruct> values) {
		super(context, R.layout.recent_rowlayout, values);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView != null)
			return convertView;
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView;
		RecentStruct item = getItem(position);
		FileInputStream stream;
		if (BMPHandler.isValidUri(item.uri, getContext()))
			try {
				rowView = inflater.inflate(R.layout.recent_rowlayout,
						parent, false);
				stream = getContext().openFileInput(
						"TH" + String.valueOf(item.id) + ".png");
				Bitmap thumb = BitmapFactory.decodeStream(stream);
				((ImageView) rowView.findViewById(R.id.recentThumb))
						.setImageBitmap(thumb);
				((TextView) rowView.findViewById(R.id.recentLastActive))
						.setText(BMPHandler.getDate(item.lastActive));
				((TextView) rowView.findViewById(R.id.recentName))
						.setText(BMPHandler.getName(item.uri,
								getContext()));
				rowView.setTag(item);
				rowView.setOnClickListener(this);

				rowView.findViewById(R.id.recentBtnDelete).setTag(item);
				rowView.findViewById(R.id.recentBtnDelete)
						.setOnClickListener(this);
				return rowView;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		return getEmptyRowView(inflater, parent, item);
	}

	private View getEmptyRowView(LayoutInflater inflater,
			ViewGroup parent, RecentStruct id) {
		View rowView = inflater.inflate(R.layout.recent_rowlayout,
				parent, false);

		((ImageView) rowView.findViewById(R.id.recentThumb))
				.setBackgroundResource(R.drawable.ic_launcher);
		((TextView) rowView.findViewById(R.id.recentName))
				.setText("File Not Found");
		rowView.findViewById(R.id.recentBtnDelete).setTag(id);
		rowView.findViewById(R.id.recentBtnDelete).setOnClickListener(
				this);
		return rowView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.recent_rowmain:
			RecentStruct str = (RecentStruct) v.getTag();
			BMPHandler mBMPHandler;
			try {
				mBMPHandler = new BMPHandler(str, getActivity());
				Intent intent = new Intent(getActivity(),
						UpdateMainActivity.class);
				
				Bundle bundle = new Bundle();
				bundle.putParcelable(MainActivityNavigation.BMP_HANDLER_KEY, mBMPHandler);
				bundle.putParcelableArrayList(MainActivityNavigation.PATH_ARRAY_KEY, str.list);
				
				intent.putExtra(MainActivityNavigation.BUNDLE_KEY, bundle);
				
				getContext().startActivity(intent);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case R.id.recentBtnDelete:
			try {
				RecentStruct strDelete = (RecentStruct) v.getTag();
				Document doc = getDocument(getContext());
				NodeList list = doc.getElementsByTagName(TAG_RECENT);
				for (int i = 0; i < list.getLength(); i++) {
					Element id = (Element) ((Element) list.item(i))
							.getElementsByTagName(TAG_ID).item(0);
					if (id.getTextContent().equals(
							String.valueOf(strDelete.id))) {
						getContext().deleteFile(
								id.getTextContent() + ".png");
						getContext().deleteFile(
								"TH" + id.getTextContent() + ".png");
						doc.getElementsByTagName(TAG_ROOT).item(0)
								.removeChild(list.item(i));

						remove(strDelete);
						writeDoc(doc, getContext());
					}
				}
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}
}
}


