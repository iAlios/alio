package alio.jswebview.engine;

import java.lang.reflect.Method;

import org.json.JSONException;
import org.json.JSONObject;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class WebInterfaceEngine {

	private WebView mWebView;

	private Object mInterfaceObject;

	public WebInterfaceEngine(WebView webView) {
		super();
		this.mWebView = webView;
	}

	public void registerInterface(Object obj) {
		mInterfaceObject = obj;
	}

	public void invokeJS(String functionName, JSONObject resultJsonObject) {
		final JSONObject result = new JSONObject();
		try {
			result.put("functionName", functionName);
			result.put("argvs", resultJsonObject);
			this.mWebView.post(new Runnable() {

				@Override
				public void run() {
					mWebView.loadUrl("javascript:browser.javaCallback('"
							+ result.toString() + "');");
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@JavascriptInterface
	public int minterface(String code) {
		try {
			JSONObject requestJsonObject = new JSONObject(code);
			if (requestJsonObject.has("functionName")) {
				Class<? extends Object> clazz = mInterfaceObject.getClass();
				try {
					Method method = clazz.getMethod(
							requestJsonObject.getString("functionName"),
							new Class[] { JSONObject.class });
					method.setAccessible(true);
					Object resultJavaObject = method.invoke(
							mInterfaceObject,
							requestJsonObject.has("argvs") ? requestJsonObject
									.getJSONObject("argvs") : null);
					if (requestJsonObject.has("callback")) {
						invokeJS(requestJsonObject.getString("callback"),
								(JSONObject) resultJavaObject);
					}
					return WConstants.SUCCESSED;
				} catch (Exception e) {
					e.printStackTrace();
					return WConstants.FAILED_NO_FUNCTION;
				}
			} else {
				return WConstants.FAILED_NO_FUNCTION;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return WConstants.FAILED_TRANS_DATA;
		}
	}

}
