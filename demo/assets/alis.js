/* encode=utf-8 */

var browser = {
	versions : function() {
		var u = navigator.userAgent, app = navigator.appVersion;
		return {//移动终端浏览器版本信息
			trident : u.indexOf('Trident') > -1, //IE内核
			presto : u.indexOf('Presto') > -1, //opera内核
			webKit : u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
			gecko : u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1, //火狐内核
			mobile : !!u.match(/AppleWebKit.*Mobile.*/), //是否为移动终端
			ios : !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
			android : u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或uc浏览器
			iPhone : u.indexOf('iPhone') > -1, //是否为iPhone或者QQHD浏览器
			iPad : u.indexOf('iPad') > -1, //是否iPad
			webApp : u.indexOf('Safari') == -1 //是否web应该程序，没有头部与底部
		};
	}(),
	isAndroid2_3X : function() {
		return navigator.appVersion.indexOf('Android 2.3') > -1;
	}(),
	language : (navigator.browserLanguage || navigator.language).toLowerCase(),
	hasAlio : function() {
		return !(window.alio == null || typeof (window.alio) == 'undefined');
	}(),
	javaCallback : function(code) {
		var result = JSON.parse(code);
		var functionName = result['functionName'];
		if (result.hasOwnProperty("argvs")) {
			eval(functionName + "('" + JSON.stringify(result.argvs) + "')");
		} else {
			eval(functionName + "('')");
		}
	},
	invokeJava : function(name, callback, argvs) {
		var code = {};
		code.functionName = name;
		code.argvs = argvs;
		code.callback = callback;
		if(this.hasAlio) {
			window.alio.minterface(JSON.stringify(code));
		} else if(this.isAndroid2_3X) {
			var data = { name: "minterface", args: JSON.stringify(code)};
			var json = JSON.stringify(data);
			prompt("alio" + json);
		}
	},
};
