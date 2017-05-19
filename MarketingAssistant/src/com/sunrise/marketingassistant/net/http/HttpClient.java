package com.sunrise.marketingassistant.net.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import com.sunrise.javascript.utils.Base64;
import com.sunrise.marketingassistant.exception.http.HttpAuthException;
import com.sunrise.marketingassistant.exception.http.HttpException;
import com.sunrise.marketingassistant.exception.http.HttpRefusedException;
import com.sunrise.marketingassistant.exception.http.HttpServerException;
import com.sunrise.marketingassistant.exception.http.ResponseException;
import com.sunrise.marketingassistant.exception.logic.BusinessException;
import com.sunrise.marketingassistant.exception.logic.ServerInterfaceException;
import com.sunrise.marketingassistant.net.http.utils.UrlUtils;
import com.sunrise.javascript.utils.LogUtlis;
import com.sunrise.marketingassistant.utils.ZipUtils;

import android.util.Log;

public class HttpClient {
	private static final String TAG = "HttpClient";
	private static final int CONNECTION_TIMEOUT_MS = 30 * 1000;
	private static final int SOCKET_TIMEOUT_MS = 30 * 1000;
	public static final int RETRIED_TIME = 3;
	private static HttpClient Instance;
	private DefaultHttpClient mClient;

	/** OK: Success! */
	public static final int OK = 200;
	/** Not Modified: There was no new data to return. */
	public static final int NOT_MODIFIED = 304;
	/**
	 * Bad Request: The request was invalid. An accompanying error message will
	 * explain why. This is the status code will be returned during rate
	 * limiting.
	 */
	public static final int BAD_REQUEST = 400;
	/** Not Authorized: Authentication credentials were missing or incorrect. */
	public static final int NOT_AUTHORIZED = 401;
	/**
	 * Forbidden: The request is understood, but it has been refused. An
	 * accompanying error message will explain why.
	 */
	public static final int FORBIDDEN = 403;
	/**
	 * Not Found: The URI requested is invalid or the resource requested, such
	 * as a user, does not exists.
	 */
	public static final int NOT_FOUND = 404;
	/**
	 * Not Acceptable: Returned by the Search API when an invalid format is
	 * specified in the request.
	 */
	public static final int NOT_ACCEPTABLE = 406;
	/**
	 * Internal Server Error: Something is broken. Please post to the group so
	 * the Weibo team can investigate.
	 */
	public static final int INTERNAL_SERVER_ERROR = 500;
	/** Bad Gateway: server is down or being upgraded. */
	public static final int BAD_GATEWAY = 502;
	/**
	 * Service Unavailable: The servers are up, but overloaded with requests.
	 * Try again later. The search and trend methods use this to indicate when
	 * you are being rate limited.
	 */
	public static final int SERVICE_UNAVAILABLE = 503;

	/** 处理成功 */
	public static final int BUSINESS_OK = 0;
	/** 帐号或密码不正确 */
	public static final int BUSINESS_USERNAME_OR_PASSWORD = 1;
	/** 系统错误 */
	public static final int BUSINESS_SYSTEM_ERROR = 999;
	/** 会话超时，请重新登录 */
	public static final int BUSINESS_CONVERSATION_TIME_OUT = 2;
	public static final int BUSINESS_AUTHENTICATION_TIME_OUT = -1;

	private HttpClient() {
		prepareHttpClient();
	}

	public static HttpClient getInstance() {
		if (Instance == null) {
			Instance = new HttpClient();
		}
		return Instance;
	}

	/**
	 * Setup DefaultHttpClient
	 * 
	 * Use ThreadSafeClientConnManager.
	 * 
	 */
	private void prepareHttpClient() {
		BasicHttpParams params = new BasicHttpParams();
		params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15 * 1000);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
		schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		mClient = new DefaultHttpClient(cm, params);
	}

	/**
	 * Execute the DefaultHttpClient
	 * 
	 * @param url
	 *            target
	 * @param requestParams
	 * @param authenticated
	 *            need or not
	 * @return Response from server
	 * @throws HttpException
	 *             此异常包装了�?��列底层异�?<br />
	 * <br />
	 *             1. 底层异常, 可使用getCause()查看: <br />
	 *             <li>URISyntaxException, 由`new URI` 引发�?</li> <li>IOException,
	 *             由`createMultipartEntity` �?`UrlEncodedFormEntity` 引发�?</li>
	 *             <li>IOException和ClientProtocolException,
	 *             由`HttpClient.execute` 引发�?</li><br />
	 * 
	 *             2. 当响应码不为200时报出的各种子类异常: <li>HttpRequestException,
	 *             通常发生在请求的错误,如请求错误了 网址导致404�? 抛出此异�? 首先�?��request log,
	 *             确认不是人为错误导致请求失败</li> <li>HttpAuthException, 通常发生在Auth失败,
	 *             �?��用于验证登录的用户名/密码/KEY�?/li> <li>HttpRefusedException,
	 *             通常发生在服务器接受到请�? 但拒绝请�? 可是多种原因, 具体原因 服务器会返回拒绝理由,
	 *             调用HttpRefusedException#getError#getMessage查看</li> <li>
	 *             HttpServerException, 通常发生在服务器发生错误�? �?��服务器端是否在正常提供服�?/li>
	 *             <li>HttpException, 其他未知错误.</li>
	 * @throws BusinessException
	 */
	public String httpRequest(String url, List<NameValuePair> pairs) throws HttpException, BusinessException {
		return httpRequest(url, pairs, true);
	}

	public String httpRequest(String url, List<NameValuePair> pairs, boolean isEncode) throws HttpException, BusinessException {
		URI uri = convertRequestUrl(url, pairs, isEncode);
		LogUtlis.w(TAG, "uri " + uri.toString());
		return httpRequest(uri);
	}

	public String httpRequest(URI uri) throws HttpException, BusinessException {
		String result = httpRequestForString(uri);
		result = ZipUtils.decodeAES(result);
		handleBusinessResultCode(result);
		return result;
	}

	/**
	 * 访问接口平台服务
	 * 
	 * @param uri
	 * @return
	 * @throws HttpException
	 * @throws BusinessException
	 */
	public String httpRequestForInterfacePlatform(String url, List<NameValuePair> pairs) throws HttpException, BusinessException {
		URI uri = convertRequestUrlWithoutParam(url, pairs, true);
		String result = httpRequestForString(uri);
		result = ZipUtils.decodeBase64(result);
		return result;
	}

	private String httpRequestForString(URI uri) throws HttpException, BusinessException {
		HttpResponse response = null;
		Response res = null;
		// Create GET METHOD
		HttpUriRequest method = new HttpGet(uri);

		// Setup ConnectionParams
		setupHTTPConnectionParams(method);

		// Execute Request
		try {
			response = mClient.execute(method);
			res = new Response(response);
			if (response != null) {
				int statusCode = response.getStatusLine().getStatusCode();
				handleResponseStatusCode(statusCode);
			}
			String json = res.asString();
			LogUtlis.e(TAG, json);
			return json;
		} catch (ClientProtocolException e) {
			throw new HttpException("客户协议错误", e);
		} catch (IOException ioe) {
			if (ioe instanceof HttpHostConnectException) {
				throw new HttpException("访问超时，请检查网络连接", ioe);
			} else {
				throw new HttpException("服务器繁忙，请稍后再试", ioe);
			}
		} finally {
			releaseConnection(method);
		}
	}

	public byte[] httpRequestForBytes(URI uri) throws HttpException, BusinessException {
		HttpResponse response = null;
		Response res = null;
		// Create GET METHOD
		HttpUriRequest method = new HttpGet(uri);

		// Setup ConnectionParams
		setupHTTPConnectionParams(method);

		// Execute Request
		try {
			response = mClient.execute(method);
			res = new Response(response);
			if (response != null) {
				int statusCode = response.getStatusLine().getStatusCode();
				handleResponseStatusCode(statusCode);
			}
			return res.asBytes();
		} catch (ClientProtocolException e) {
			throw new HttpException("客户协议错误", e);
		} catch (IOException ioe) {
			if (ioe instanceof HttpHostConnectException) {
				throw new HttpException("访问超时，请检查网络连接", ioe);
			} else {
				throw new HttpException("服务器繁忙，请稍后再试", ioe);
			}
		} finally {
			releaseConnection(method);
		}
	}

	private void releaseConnection(HttpUriRequest method) {
		if (method != null)
			method.abort();
	}

	private String decodeJson(Response response) throws ResponseException {
		String jsonStr = response.asString();
		String decodeJsonStr = ZipUtils.decodeAES(jsonStr);
		return decodeJsonStr;
	}

	private URI convertRequestUrl(String url, List<NameValuePair> pairs, boolean isEncode) throws HttpException {
		if (pairs == null || pairs.isEmpty())
			return createURI(url);

		String requestParamsStr = UrlUtils.buildQueryString(pairs);
		LogUtlis.w(TAG, "requestParamsStr:" + url + "?" + requestParamsStr);
		String baseUrl = url;
		if (isEncode) {
			requestParamsStr = ZipUtils.encodeAES(requestParamsStr);
			HashMap<String, String> requestPas = new HashMap<String, String>();
			requestPas.put("is_new_version", "1");// 2014年12月23日17:50:53 add by
													// fuheng
			requestPas.put("param", requestParamsStr);
			baseUrl = UrlUtils.buildUrlByQueryStringMapAndBaseUrl(url, requestPas);
		} else {
			baseUrl = UrlUtils.buildUrlByQueryStringAndBaseUrl(url, URLEncodedUtils.format(pairs, "UTF-8"));
		}
		URI uri = createURI(baseUrl);
		return uri;
	}

	public URI convertRequestUrlWithoutParam(String url, List<NameValuePair> pairs, boolean isEncode) throws HttpException {
		if (pairs == null || pairs.isEmpty())
			return createURI(url);

		String requestParamsStr = UrlUtils.buildQueryString(pairs);
		LogUtlis.w(TAG, "requestParamsStr:" + url + "?" + requestParamsStr);
		String baseUrl = url;
		if (isEncode) {
			requestParamsStr = ZipUtils.encodeBase64(requestParamsStr);
			// requestParamsStr = ZipUtils.encodeAES(requestParamsStr);
			// requestParamsStr += "&is_new_version=1";
			baseUrl = UrlUtils.buildUrlByQueryStringAndBaseUrl(url, requestParamsStr);

		} else {
			baseUrl = UrlUtils.buildUrlByQueryStringAndBaseUrl(url, URLEncodedUtils.format(pairs, "UTF-8"));
		}
		URI uri = createURI(baseUrl);
		return uri;
	}

	/**
	 * CreateURI from URL string
	 * 
	 * @param url
	 * @return request URI
	 * @throws HttpException
	 *             Cause by URISyntaxException
	 */
	private URI createURI(String url) throws HttpException {
		URI uri;

		try {
			LogUtlis.w(TAG, "url: " + url);
			uri = new URI(url);
		} catch (URISyntaxException e) {
			LogUtlis.e(TAG, e.getMessage(), e);
			throw new HttpException("Invalid URL.");
		}

		return uri;
	}

	/**
	 * Setup HTTPConncetionParams
	 * 
	 * @param method
	 */
	private void setupHTTPConnectionParams(HttpUriRequest method) {
		HttpConnectionParams.setConnectionTimeout(method.getParams(), CONNECTION_TIMEOUT_MS);
		HttpConnectionParams.setSoTimeout(method.getParams(), SOCKET_TIMEOUT_MS);
		mClient.setHttpRequestRetryHandler(requestRetryHandler);
	}

	/**
	 * 异常自动恢复处理, 使用HttpRequestRetryHandler接口实现请求的异常恢复
	 */
	private static HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {
		// 自定义的恢复策略
		public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
			// 设置恢复策略，在发生异常时候将自动重试N次
			if (executionCount >= RETRIED_TIME) {
				// Do not retry if over max retry count
				return false;
			}
			if (exception instanceof NoHttpResponseException) {
				// Retry if the server dropped connection on us
				return true;
			}
			if (exception instanceof SSLHandshakeException) {
				// Do not retry on SSL handshake exception
				return false;
			}
			HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
			boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
			if (!idempotent) {
				// Retry if the request is considered idempotent
				return true;
			}
			return false;
		}
	};

	/**
	 * Handle Status code
	 * 
	 * @param statusCode
	 *            响应的状态码
	 * @param res
	 *            服务器响应
	 * @throws HttpException
	 *             当响应码不为200时都会报出此异常:<br />
	 *             <li>HttpRequestException, 通常发生在请求的错误,如请求错误了 网址导致404等, 抛出此异常,
	 *             首先检查request log, 确认不是人为错误导致请求失败</li> <li>HttpAuthException,
	 *             通常发生在Auth失败, 检查用于验证登录的用户名/密码/KEY等</li> <li>
	 *             HttpRefusedException, 通常发生在服务器接受到请求, 但拒绝请求, 可是多种原因, 具体原因
	 *             服务器会返回拒绝理由, 调用HttpRefusedException#getError#getMessage查看</li>
	 *             <li>HttpServerException, 通常发生在服务器发生错误时, 检查服务器端是否在正常提供服务</li>
	 *             <li>HttpException, 其他未知错误.</li>
	 */
	private void handleResponseStatusCode(int statusCode) throws HttpException {
		String msg = getCause(statusCode) + "\n";

		switch (statusCode) {
		case OK:
			break;

		// Mine mistake, Check the LogUtlis
		case NOT_MODIFIED:
		case BAD_REQUEST:
		case NOT_FOUND:
		case NOT_ACCEPTABLE:
			throw new HttpException(msg, statusCode);

			// UserName/Password incorrect
		case NOT_AUTHORIZED:
			throw new HttpAuthException(msg, statusCode);

			// Server will return a error message, use
			// HttpRefusedException#getError() to see.
		case FORBIDDEN:
			throw new HttpRefusedException(msg, statusCode);

			// Something wrong with server
		case INTERNAL_SERVER_ERROR:
		case BAD_GATEWAY:
		case SERVICE_UNAVAILABLE:
			throw new HttpServerException(msg, statusCode);

			// Others
		default:
			throw new HttpException(msg, statusCode);
		}
	}

	/** Handle business result code */
	private void handleBusinessResultCode(String resultStr) throws HttpException, BusinessException {
		try {
			JSONObject jsonObject = new JSONObject(resultStr);
			int businessResultCode = jsonObject.getInt("resultCode");
			LogUtlis.d(TAG, "" + jsonObject.toString());
			if (businessResultCode != BUSINESS_OK) {
				String businessResultMesage = jsonObject.getString("resultMessage");
				throw new BusinessException(businessResultMesage, businessResultCode);
			}
		} catch (JSONException e) {
			throw new ResponseException("服务器繁忙，请稍后再试", e);
		}
	}

	// private String handleBase64ResultCode(String resultStr) throws
	// HttpException, BusinessException {
	// try {
	// JSONObject jsonObject = new
	// JSONObject(resultStr).getJSONObject("RETURN");
	// int businessResultCode =
	// Integer.parseInt(jsonObject.getString("RETURN_CODE"));
	// LogUtlis.d(TAG, "" + jsonObject.toString());
	// if (businessResultCode != BUSINESS_OK) {
	// String businessResultMesage = jsonObject.getString("RETURN_MESSAGE");
	// throw new BusinessException(businessResultMesage, businessResultCode);
	// }
	//
	// return jsonObject.toString();
	// } catch (JSONException e) {
	// throw new ResponseException("服务器繁忙，请稍后再试", e);
	// }
	// }

	public String handleResultCode(String resultStr, String returnCode, String returnMsg) throws HttpException, BusinessException {
		try {
			JSONObject jsonObject = new JSONObject(resultStr);
			int businessResultCode = Integer.parseInt(jsonObject.getString(returnCode));
			LogUtlis.d(TAG, "" + jsonObject.toString());
			if (businessResultCode != BUSINESS_OK) {
				String businessResultMesage = jsonObject.getString(returnMsg);
				throw new BusinessException(businessResultMesage, businessResultCode);
			}

			return jsonObject.toString();
		} catch (JSONException e) {
			throw new ResponseException("服务器繁忙，请稍后再试", e);
		}
	}

	/**
	 * 解析HTTP错误码
	 * 
	 * @param statusCode
	 * @return
	 */
	private static String getCause(int statusCode) {
		String cause = "服务器繁忙，请稍后再试";
		/*
		 * switch (statusCode) { case NOT_MODIFIED: break; case BAD_REQUEST:
		 * cause =
		 * "The request was invalid.  An accompanying error message will explain why. This is the status code will be returned during rate limiting."
		 * ; break; case NOT_AUTHORIZED: cause =
		 * "Authentication credentials were missing or incorrect."; break; case
		 * FORBIDDEN: cause =
		 * "The request is understood, but it has been refused.  An accompanying error message will explain why."
		 * ; break; case NOT_FOUND: cause =
		 * "The URI requested is invalid or the resource requested, such as a user, does not exists."
		 * ; break; case NOT_ACCEPTABLE: cause =
		 * "Returned by the Search API when an invalid format is specified in the request."
		 * ; break; case INTERNAL_SERVER_ERROR: cause =
		 * "Something is broken.  Please post to the group so the Weibo team can investigate."
		 * ; break; case BAD_GATEWAY: cause =
		 * "server is down or being upgraded."; break; case SERVICE_UNAVAILABLE:
		 * cause =
		 * "Service Unavailable: The servers are up, but overloaded with requests. Try again later. The search and trend methods use this to indicate when you are being rate limited."
		 * ; break; default: cause = ""; }
		 */
		return cause;
	}

	public String httpRequestStrPost(String url, List<NameValuePair> pairs) throws HttpException, ServerInterfaceException {
		Log.d(TAG, "post:" + url);
		HttpResponse response = null;
		Response res = null;
		// Create HttpPost
		HttpPost httppost = new HttpPost(url);

		// Execute Request
		try {
			httppost.setEntity((HttpEntity) new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
			httppost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			response = mClient.execute(httppost);
			res = new Response(response);
			if (response != null) {
				int statusCode = response.getStatusLine().getStatusCode();
				handleResponseStatusCode(statusCode);
			}
			return res.asString();
		} catch (ClientProtocolException e) {
			throw new HttpException("客户协议错误", e);
		} catch (IOException ioe) {
			if (ioe instanceof HttpHostConnectException || ioe instanceof UnknownHostException) {
				throw new HttpException("访问超时，请检查网络连接", ioe);
			} else {
				throw new HttpException("服务器繁忙，请稍后再试", ioe);
			}
		} finally {
			releaseConnection(httppost);
		}
	}
}
