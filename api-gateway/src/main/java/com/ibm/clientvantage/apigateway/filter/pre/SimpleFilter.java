package com.ibm.clientvantage.apigateway.filter.pre;

import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import com.netflix.zuul.context.RequestContext;

import com.ibm.clientvantage.apigateway.util.HttpUtil;

import com.netflix.zuul.ZuulFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleFilter extends ZuulFilter {

  private static Logger log = LoggerFactory.getLogger(SimpleFilter.class);

  @Override
  public String filterType() {
    return "pre";
  }

  @Override
  public int filterOrder() {
    return 1;
  }

  @Override
  public boolean shouldFilter() {
    return true;
  }

  @Override
  public Object run() {
    RequestContext ctx = RequestContext.getCurrentContext();
    ctx.addZuulRequestHeader("preZuul", "yes");
    HttpServletRequest request = ctx.getRequest();
    log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
    /*
    String userName = request.getParameter("username");
    String password = request.getParameter("password");
    String getparam = "";
    String token = null;
    Enumeration<String> headers = request.getHeaderNames();
    if (request.getHeader("X_ACCESS_TOKEN") !=null)
    	token = request.getHeader("X_ACCESS_TOKEN").toString();

	String validUrl = "https://cas.cv.com:8443/cas/oauth2.0/profile";
	String getTokenUrl = "https://cas.cv.com:8443/cas/oauth2.0/accessToken";
	String responseStr = null;
    
    if (token !=null)
    {
    	getparam = "access_token=" + token;
		
		responseStr = HttpUtil.sendGet(validUrl, getparam);
		if ("REFRESHTOKEN_EXPIRED_W3ID_RETURN".equals(responseStr)) {
			ctx.setSendZuulResponse(false);
			ctx.setResponseBody("invalid access token");
		}
		return null;
    }
    else if (userName !=null && password !=null )
    {
    	getparam = "grant_type=password&client_id=clientid&username=" + userName + "&password=" + password;
    	responseStr = HttpUtil.sendPost(getTokenUrl, getparam);
    	if (responseStr !=null)
    	{
    		String[] results = responseStr.split("&");
    		if (results !=null && results.length>0)
    			for (String result : results)
    			{
    				if (!result.startsWith("access_token"))
    					continue;
    				String[] values = result.split("=");
    				if (values !=null && values.length>1)
    				{
    					token = values[1];
    				}
    				
    			}
    	}
    	if (token !=null)
    	{
    		ctx.addZuulRequestHeader("access_token", token);
    	}
    	return null;
    }
	*/
    return null;
  }

}
