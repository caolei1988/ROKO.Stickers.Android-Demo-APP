package com.rokolabs.app.common.cache;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.rokolabs.app.common.http.AsyncHttpClient;
import com.rokolabs.app.common.http.HttpUtils;
import com.rokolabs.app.common.http.RequestParams;
import com.rokolabs.app.common.util.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HttpApiHelper
{
	public static enum Method{
		GET, POST, DELETE, PUT
	}
	
    public static enum CallbackType
    {
        /**
         * If there is cache data, return it right away. And possible callback
         * if data is changed after network op is done. It also have a 15 minutes refresh limit.
         */
        CacheFirst,

        /**
         * Only return data after network op is done
         */
        ForceUpdate,

        /**
         * only return data after network op is done and data is changed
         */
        CallbackOnChange,

        /**
         * do not use any cache data
         */
        NoCache
    }

    private static final String KEY_TIME_PREFIX     = "_time@@";
    private static final long   MIN_UPDATE_INTERVAL = 15 * 60 * 1000l;

    /**
     * API response callback
     * 
     * @author Zexu
     * 
     */
    public interface StringResponseListener
    {
        public void onResponse(String content, boolean isCacheData);

        public void onBackgrounResponse(String content, boolean isCacheData);
    }

    public static abstract class ResponseProxy
    {
        public final void onResponse(String content, boolean isCacheData, StringResponseListener listener)
        {
            if (handleResponse(content, isCacheData))
                content = null;
            if (listener != null)
            {
                listener.onResponse(content, isCacheData);
            }
        }

        /**
         * 
         * @param content
         * @param isCacheData
         * @return true if you don't want original listener to receive the data
         */
        public abstract boolean handleResponse(String content, boolean isCacheData);
    }

    private static ResponseProxy sProxy;

    public static void setGlobalResponseProxy(ResponseProxy proxy)
    {
        sProxy = proxy;
    }

    public static AsyncHttpClient  mClient = new AsyncHttpClient();

    private static ExecutorService mPool   = Executors.newCachedThreadPool();

    public static Future<?> callAPI(final Context context, String url, StringResponseListener listener)
    {
        return callAPI(context, CallbackType.ForceUpdate, Method.GET, url, null, null, listener);
    }
    
    public static Future<?> callAPI(final Context context, String url, RequestParams params, StringResponseListener listener)
    {
        return callAPI(context, CallbackType.ForceUpdate, Method.GET, url, params, null, listener);
    }
    
    public static Future<?> callAPI(final Context context, final CallbackType callbackType, final String url, final RequestParams params, final StringResponseListener listener){
    	return callAPI(context, callbackType, Method.GET, url, params, null, listener);
    }
    
    public static Future<?> postToAPI(Context context, String url, HttpEntity content, StringResponseListener listener){
    	return callAPI(context, CallbackType.NoCache, Method.POST, url, null, content, listener);
    }
    
    public static Future<?> deleteToAPI(Context context, String url, StringResponseListener listener){
    	return callAPI(context, CallbackType.NoCache, Method.DELETE, url, null, null, listener);
    }
    
    /**
     * get data from http in background thread and receive data on callback on
     * UI thread
     * 
     * @param context
     *            app context
     * @param callbackType
     *            callback strategy
     * @param url
     *            base url
     * @param params
     *            additional parameters, can be null
     * @param listener
     *            callback listener
     * @return
     */
    public static Future<?> callAPI(final Context context, final CallbackType callbackType, final Method method, final String url, final RequestParams params, final HttpEntity content, final StringResponseListener listener)
    {
        final Handler uiHandler = new Handler(Looper.getMainLooper());
        return mPool.submit(new Runnable()
        {
            public void run()
            {
                if (url != null && url.startsWith("asset://"))
                {
                    String path = Uri.parse(url).getEncodedPath();
                    if (path.startsWith("/"))
                        path = path.substring(1);
                    postResult(getAssetContent(context, path), true);
                    return;
                }
                String result = null;
                final SimpleCache cache = new SimpleCache(context);
                String contentString = null;
                if(content != null){
                	try {
						contentString = EntityUtils.toString(content);
	                	if(contentString.length() > 4000)
	                		contentString = contentString.substring(0, 4000);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
				final String key = method + "@"
						+ AsyncHttpClient.getUrlWithQueryString(url, params)
						+ "@" + contentString + "@"
						+ AsyncHttpClient.HEADER_API_KEY_CONTENT + "@"
						+ AsyncHttpClient.HEADER_AUTH_SESSION;
                Logger.d("callAPI "+key);
                // boolean online = isOnline(context);
                final String cacheData = cache.getCacheData(key);
                long updateTime = 0;
                try
                {
                    updateTime = Long.parseLong(cache.getCacheData(KEY_TIME_PREFIX + key));
                } catch (Exception e)
                {
                    //ignore
                }
                if (callbackType == CallbackType.CacheFirst && cacheData != null)
                {
                    if (listener != null)
                    {
                        postResult(cacheData, true);
                    }
                    if (System.currentTimeMillis() - updateTime < MIN_UPDATE_INTERVAL)
                        return;
                }

                try
                {
                    SimpleResult sr = getResponseFrom(method, AsyncHttpClient.getUrlWithQueryString(url, params), content);
                    result = sr.result;
                    // result = mClient.syncGet(url, params);
                    // if (result == null)
                    // throw new IOException("Result is null");

                    if (callbackType == CallbackType.CallbackOnChange)
                    {
                        if (cacheData != null && cacheData.equals(result))
                            return;
                    }
                    if(result != null){
                    	postResult(result, false);
                    	if(!sr.error){
		                    cache.addCacheData(key, result);
		                    cache.addCacheData(KEY_TIME_PREFIX + key, System.currentTimeMillis() + "");
                    	} else {
                    		cache.clearCacheData(key);
                    		cache.clearCacheData(KEY_TIME_PREFIX + key);
                    	}
                    } else {
                    	postResult(callbackType == CallbackType.ForceUpdate ? cache.getCacheData(key) : null, true);
                    }

                } catch (Exception e)
                {
                    Logger.e(e.getMessage(), e);
                    e.printStackTrace();
                    postResult(callbackType == CallbackType.ForceUpdate ? cache.getCacheData(key) : null, true);
                }
            }

            void postResult(final String result, final boolean isCacheData)
            {
                if (listener == null)
                    return;
                listener.onBackgrounResponse(result, isCacheData);
                uiHandler.post(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        try
                        {
                            if (sProxy != null)
                            {
                                if (sProxy.handleResponse(result, isCacheData))
                                    return;
                            }
                            listener.onResponse(result, isCacheData);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                });
            }
        });

    }

    public static String getAssetContent(Context ctx, String filename)
    {
        try
        {
            InputStream is = ctx.getAssets().open(filename);
            String content = new String(IS2ByteArray(is));
            is.close();
            return content;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] IS2ByteArray(InputStream is) throws IOException
    {
        BufferedInputStream bis = new BufferedInputStream(is);
        byte[] buf = new byte[8192];
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        int len;
        while ((len = bis.read(buf)) != -1)
        {
            bao.write(buf, 0, len);
        }
        return bao.toByteArray();
    }

    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    static class SimpleResult{
    	public String result = null;
    	public boolean error = true;
    }
    
    public static SimpleResult getResponseFrom(Method method, final String url, HttpEntity content) throws MalformedURLException, UnknownHostException, IOException, UnsupportedEncodingException
    {
        URL fullUrl = new URL(url);
        SimpleResult result = new SimpleResult();
        // do a dns lookup first
        Logger.d("dns lookup for: " + fullUrl.getHost());
        if (!HttpUtils.checkDNSResolve(fullUrl.getHost(), 3000))
        {
            throw new UnknownHostException("Faile to resolve " + fullUrl.getHost() + " in 3 seconds");
        }
        Logger.d("connect to: " + url);
        HttpUriRequest request;
        switch(method){
        case GET:
        	request = new HttpGet(url);
        	break;
		case DELETE:
			request = new HttpDelete(url);
			break;
		case POST:
			request = new HttpPost(url);
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Accept", "application/json");
			mClient.addEntityToRequestBase((HttpEntityEnclosingRequestBase) request, content);
			break;
		case PUT:
			request = new HttpPut(url);
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Accept", "application/json");
			mClient.addEntityToRequestBase((HttpEntityEnclosingRequestBase) request, content);
			break;
		default:
			return null;
        	
        }
        HttpResponse resp = mClient.syncExec(request);
		result.error = resp.getStatusLine().getStatusCode() >= 300;
        result.result = AsyncHttpClient.parseResponse(resp, "UTF-8", true);
        
        return result;
    }
    
    
}
