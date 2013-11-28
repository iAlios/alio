package alio.jswebview.engine.view;

import java.lang.reflect.Method;

import org.json.JSONObject;

import alio.jswebview.engine.WebInterfaceEngine;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MWebView extends WebView {

	private WebInterfaceEngine mWebInterfaceEngine;
	private boolean javascriptInterfaceBroken = false;

	public MWebView(Context context) {
		super(context);
	}

	public MWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void registerInterface(Object object) {
		if (mWebInterfaceEngine == null) {
			mWebInterfaceEngine = new WebInterfaceEngine(this);
		}
		mWebInterfaceEngine.registerInterface(object);
		resetJSSettings();
	}

	public void invokeJS(String name, JSONObject object) {
		mWebInterfaceEngine.invokeJS(name, object);
	}

	@SuppressLint("SetJavaScriptEnabled")
	public void resetJSSettings() {
		if (mWebInterfaceEngine == null) {
			mWebInterfaceEngine = new WebInterfaceEngine(this);
		}
		try {
			if (Build.VERSION.RELEASE.startsWith("2.3")) {
				javascriptInterfaceBroken = true;
			}
		} catch (Exception e) {
		}

		if (!javascriptInterfaceBroken) {
			addJavascriptInterface(mWebInterfaceEngine, "alio");
		}

		setWebChromeClient(new WebChromeClient() {
			public void onConsoleMessage(String message, int lineNumber,
					String sourceID) {
				Log.d("MyApplication", message + " -- From line " + lineNumber
						+ " of " + sourceID);
			}

			@Override
			public boolean onJsPrompt(WebView view, String url, String message,
					String defaultValue, JsPromptResult result) {
				if (!javascriptInterfaceBroken || TextUtils.isEmpty(message)
						|| !message.startsWith("alio")) {
					return false;
				}

				JSONObject jsonData;
				String functionName;
				String encodedData;

				try {
					encodedData = message.substring("alio".length());
					jsonData = new JSONObject(encodedData);
					encodedData = null;
					functionName = jsonData.getString("name");
					for (Method m : mWebInterfaceEngine.getClass().getMethods()) {
						if (m.getName().equals(functionName)) {
							Object ret = m.invoke(mWebInterfaceEngine,
									jsonData.getString("args"));
							JSONObject res = new JSONObject();
							res.put("result", ret.toString());
							result.confirm(res.toString());
							return true;
						}
					}

					throw new RuntimeException(
							"shouldOverrideUrlLoading: Could not find method '"
									+ functionName + "()'.");
				} catch (IllegalArgumentException e) {
					Log.e("GingerbreadWebViewClient",
							"shouldOverrideUrlLoading: Please ensure your JSInterface methods only have String as parameters.");
					throw new RuntimeException(e);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		});
		WebSettings mWebSettings = getSettings();
		mWebSettings.setJavaScriptEnabled(true);
		mWebSettings.setSavePassword(false);
		mWebSettings.setSaveFormData(false);
		mWebSettings.setAllowFileAccess(true);
		mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
	}

}
