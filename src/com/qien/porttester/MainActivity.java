package com.qien.porttester;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener
{

	private ThreadPortTest mTask1, mTask2, mTask3, mTask4;
	private Button button1, button2, button3, button4;
	private EditText etPort1, etPort2, etPort3, etPort4;
	private EditText etHostName;
	private TextView tvWanInfo, tvHostIP;
	private TextView tvResult1, tvResult2, tvResult3, tvResult4;

	// // onCreate //////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Get Controls
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		button4 = (Button) findViewById(R.id.button4);

		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
		button4.setOnClickListener(this);

		etPort1 = (EditText) findViewById(R.id.editText1);
		etPort2 = (EditText) findViewById(R.id.editText2);
		etPort3 = (EditText) findViewById(R.id.editText3);
		etPort4 = (EditText) findViewById(R.id.editText4);

		etPort1.setOnFocusChangeListener(editTextHostNameChanged);
		etPort2.setOnFocusChangeListener(editTextHostNameChanged);
		etPort3.setOnFocusChangeListener(editTextHostNameChanged);
		etPort4.setOnFocusChangeListener(editTextHostNameChanged);
		
		tvResult1 = (TextView) findViewById(R.id.textView1);
		tvResult2 = (TextView) findViewById(R.id.textView2);
		tvResult3 = (TextView) findViewById(R.id.textView3);
		tvResult4 = (TextView) findViewById(R.id.textView4);
		
		
		etHostName = (EditText) findViewById(R.id.editText5);
		tvHostIP = (TextView) findViewById(R.id.textViewHostIP);
		tvWanInfo = (TextView) findViewById(R.id.textViewWanIP);

		// Set listen etHostName onChange
		etHostName.setOnFocusChangeListener(editTextHostNameChanged);

		// Display Device WAN IP address
		GetWanIP getWanIp = new GetWanIP();
		getWanIp.execute();

	}

	

	// Thread for display WAN IP address
	private class GetWanIP extends AsyncTask<String, Integer, String>
	{
		private InputStream isContent = null;
		private String strURL = "http://checkip.dyndns.com/";
		private String strHTML = null;
		private String strIP = null;
		private String strPattern = null;
	
		@Override
		protected String doInBackground(String... arg0)
		{
	
			try
			{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(strURL);
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				isContent = entity.getContent();
	
			} catch (Exception e)
			{
	
				e.printStackTrace();
			}
	
			try
			{
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(isContent, "utf-8"));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null)
				{
					sb.append(line + "\n");
				}
	
				strHTML = sb.toString();
				isContent.close();
				strPattern = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}";
	
				Pattern pattern = Pattern.compile(strPattern);
				Matcher matcher = pattern.matcher(strHTML);
				if (matcher.find())
				{
					strIP = matcher.group(0);
				}
	
			} catch (Exception e)
			{
				e.printStackTrace();
			}
	
			return null;
	
		}
	
		@Override
		protected void onPostExecute(String result)
		{
			tvWanInfo.setText("Your WAN IP address is:  " + strIP);
		}
	}



	// Thread for display Host IP address ////////
	private class DisplayHostIP extends AsyncTask<Integer, Integer, String>
	{
		String strIpAddress = null;
	
		@Override
		protected String doInBackground(Integer... arg0)
		{
	
			InetSocketAddress isa = new InetSocketAddress(etHostName.getText()
					.toString().replace(" ", ""), 80);
			strIpAddress = isa.getAddress().getHostAddress();
			return null;
		}
	
		@Override
		protected void onPostExecute(String result)
		{
	
			if (!strIpAddress.equals("::1"))
			{
				tvHostIP.setText(strIpAddress);
			}
		}
	
	}



	// Display Host IP address ////////////////////////////////
	private OnFocusChangeListener editTextHostNameChanged = new OnFocusChangeListener()
	{

		@Override
		public void onFocusChange(View v, boolean hasFocus)
		{
			switch (v.getId())
			{
			case R.id.editText1:
				tvResult1.setText("");
				break;
			case R.id.editText2:
				tvResult2.setText("");
				break;
			case R.id.editText3:
				tvResult3.setText("");
				break;
			case R.id.editText4:
				tvResult4.setText("");
				break;
			case R.id.editText5:
				DisplayHostIP displayHostIP = new DisplayHostIP();
				displayHostIP.execute();
				break;
			}

		}

	};

	// /// OptionsMenu ////////////////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);

		return true;
	}

	// OptionsMenu selected
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.menu_settings:

			break;
		case R.id.menu_exit:
			// Get PID
			dialog();
			break;
		}

		return false;
	}

	// //////// Test button onClick /////////////////////
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.button1:
			if (etPort1.getText().toString().equals("")
					|| etHostName.getText().toString().equals(""))
				break;
			mTask1 = new ThreadPortTest();
			mTask1.strIndex = "1";
			mTask1.execute();

			break;
		case R.id.button2:
			if (etPort2.getText().toString().equals("")
					|| etHostName.getText().toString().equals(""))
				break;
			mTask2 = new ThreadPortTest();
			mTask2.strIndex = "2";
			mTask2.execute();
			break;
		case R.id.button3:
			if (etPort3.getText().toString().equals("")
					|| etHostName.getText().toString().equals(""))
				break;
			mTask3 = new ThreadPortTest();
			mTask3.strIndex = "3";
			mTask3.execute();
			break;
		case R.id.button4:
			if (etPort4.getText().toString().equals("")
					|| etHostName.getText().toString().equals(""))
				break;
			mTask4 = new ThreadPortTest();
			mTask4.strIndex = "4";
			mTask4.execute();
			break;

		}

	}

	// Port testing thread
	private class ThreadPortTest extends AsyncTask<Integer, Integer, String>
	{

		private String strHostName;
		private Integer iPortNumber;
		private EditText editText;
		private ProgressBar progressbar;
		private TextView textView;
		private String strResult;

		// For index control
		public String strIndex;

		@Override
		protected void onPreExecute()
		{

			editText = (EditText) findViewById(getResources().getIdentifier(
					"editText" + this.strIndex, "id", getPackageName()));

			progressbar = (ProgressBar) findViewById(getResources()
					.getIdentifier("progressBar" + this.strIndex, "id",
							getPackageName()));

			textView = (TextView) findViewById(getResources().getIdentifier(
					"textView" + this.strIndex, "id", getPackageName()));

			textView.setVisibility(View.INVISIBLE);
			progressbar.setVisibility(View.VISIBLE);

		}

		@Override
		protected String doInBackground(Integer... arg0)
		{
			// TODO Auto-generated method stub
			if (editText.getText().toString().equals("")
					|| etHostName.getText().toString().equals(""))
				return null;

			iPortNumber = Integer.parseInt(editText.getText().toString());
			strHostName = etHostName.getText().toString().replace(" ", "");

			Socket socket = null;
			socket = new Socket();
			InetSocketAddress isa = new InetSocketAddress(strHostName,
					iPortNumber);

			try
			{
				socket.connect(isa, 2000);
				strResult = "open";
				socket.close();

			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				strResult = "close";

			}

			return null;
		}

		@Override
		protected void onPostExecute(String result)
		{
			progressbar.setVisibility(View.INVISIBLE);
			if (strResult == "open")
			{
				textView.setText("OPEN");
				textView.setTextColor(Color.GREEN);
			} else
			{
				textView.setText("CLOSE");
				textView.setTextColor(Color.WHITE);
			}
			textView.setVisibility(View.VISIBLE);

			/*
			 * Toast.makeText(MainActivity.this, strHostName+
			 * Integer.toString(iPortNumber)+strResult,
			 * Toast.LENGTH_LONG).toast.show();
			 */
		}

	}

	// Get Return-Key down ////////////////
	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		//return super.onKeyDown(keyCode, event);
		if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
			this.dialog();
			return false;
			}
			return false;
			
	}
	
	// Exit Confirm Dialog ///////////////
	protected void dialog()
	{
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		builder.setMessage("Confirm exit?");
		builder.setTitle("Warnning");
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.confirm_exit,
				new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface arg0, int arg1)
					{
						// Get PID
						android.os.Process.killProcess(android.os.Process
								.myPid());
						System.exit(0);
						arg0.dismiss();

					}
				});

		builder.setNegativeButton(R.string.cancel_exit,
				new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						// TODO Auto-generated method stub
						dialog.dismiss();

					}
				});

		builder.create().show();
		
	}



}
