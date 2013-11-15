package com.joyplus.tvhelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

public class WebViewActivity extends Activity implements OnClickListener {

	private WebView webView;
	private String url;
	private TextView url_text;
	private Button close_Button;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		url = getIntent().getStringExtra("url");
		webView = (WebView) findViewById(R.id.webView);
		url_text = (TextView) findViewById(R.id.web_url);
		close_Button = (Button) findViewById(R.id.close_btn);
		webView.setWebViewClient(new WebViewClient()
		   {
		          @Override
		          public boolean shouldOverrideUrlLoading(WebView view, String url)
		          {
		 
		            view.loadUrl(url); // 在当前的webview中跳转到新的url
		            url_text.setText(url);
		            return true;
		          }
		    });
		webView.loadUrl(url);
		url_text.setText(url);
		close_Button.setOnClickListener(this);
	}
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		this.finish();
	}
}
