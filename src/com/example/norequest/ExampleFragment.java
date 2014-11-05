package com.example.norequest;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class ExampleFragment extends Fragment implements View.OnClickListener{
	
	private RelativeLayout rootView;
	private ImageView imageView;
	private EditText editText;
	
	private static final String SERVER = "https://www.google.com/search?&tbm=isch&q=";
	
	public ExampleFragment(){}
	
	private View $(int id){
		return rootView.findViewById(id);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_main, container, false);
		imageView = (ImageView) $(R.id.imageView);
		editText = (EditText) $(R.id.editText);
		((Button) $(R.id.button)).setOnClickListener(this);
		return rootView;
	}
	
	@Override
	public void onClick(View v) {
		final String url = SERVER + editText.getText().toString();
		new AsyncTask<Object, Integer, Bitmap>(){

			@Override
			protected Bitmap doInBackground(Object... params) {
				try{
					HttpResponse httpResponse = new DefaultHttpClient().execute(new HttpGet(url.trim().replace(" ", "%20")));
					HttpEntity en = httpResponse.getEntity();
					String html = EntityUtils.toString(en);
					httpResponse.getEntity().consumeContent();
					html = html.substring(html.indexOf("<img"));
					html = html.substring(html.indexOf("src=") + 5);
					html = html.substring(0, html.indexOf("\""));
		            URLConnection conn = new URL(html).openConnection(); 
		            conn.connect();
		            InputStream is = conn.getInputStream();
		            BufferedInputStream bis = new BufferedInputStream(is);
		            Bitmap bitmap = BitmapFactory.decodeStream(bis);
		            bis.close();
		            is.close();
		            return bitmap;
				}catch(Exception e){}
				return null;
			}
			
			@Override
			protected void onPostExecute(Bitmap bitmap){
				super.onPostExecute(bitmap);
				try{
		            imageView.setImageBitmap(bitmap);
				}catch (Exception e) {}
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

}