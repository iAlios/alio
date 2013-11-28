package wst.webview;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import alio.jswebview.engine.view.MWebView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private MWebView contentWebView = null;
	private TextView msgView = null;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		contentWebView = (MWebView) findViewById(R.id.webview);
		contentWebView.registerInterface(this);
		msgView = (TextView) findViewById(R.id.msg);
		contentWebView.loadUrl("file:///android_asset/wst.html");

		WebSettings mWebSettings = contentWebView.getSettings();
		mWebSettings.setJavaScriptEnabled(true);
		Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(btnClickListener);
	}

	OnClickListener btnClickListener = new Button.OnClickListener() {
		public void onClick(View v) {
			contentWebView.invokeJS("javacalljs", null);
		}
	};

	public JSONObject startFunction(JSONObject parmas) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				msgView.setText(msgView.getText() + "\njs调用java函数");
			}
		});
		Map<String, String> map = new HashMap<String, String>();
		map.put("hello", "world");
		return new JSONObject(map);
	}

}