package com.joyplus.tvhelper;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {

	private WebView webView;
	private String url;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		url = getIntent().getStringExtra("url");
		webView = (WebView) findViewById(R.id.webView);
		webView.setWebViewClient(new WebViewClient()
		   {
		          @Override
		          public boolean shouldOverrideUrlLoading(WebView view, String url)
		          {
		 
		            view.loadUrl(url); // 在当前的webview中跳转到新的url
		 
		            return true;
		          }
		    });
		webView.loadUrl(url);
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
}
