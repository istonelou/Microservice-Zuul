package com.ibm.clientvantage.apigateway;

import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;

import org.springframework.util.StringUtils;

import com.ibm.clientvantage.apigateway.model.ZuulRouteV0;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Service;
import io.kubernetes.client.models.V1ServiceList;
import io.kubernetes.client.models.V1ServicePort;
import io.kubernetes.client.util.Config;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomRouteLocator extends SimpleRouteLocator implements RefreshableRouteLocator{


    private ZuulProperties properties;
    
	private StringBuilder sb = new StringBuilder();


    public CustomRouteLocator(String servletPath, ZuulProperties properties) {
        super(servletPath, properties);
        this.properties = properties;
    }

    //父类已经提供了这个方法，这里写出来只是为了说明这一个方法很重要！！！
//    @Override
//    protected void doRefresh() {
//        super.doRefresh();
//    }


    @Override
    public void refresh() {
        doRefresh();
    }

    @Override
    protected Map<String, ZuulRoute> locateRoutes() {
        LinkedHashMap<String, ZuulRoute> routesMap = new LinkedHashMap<String, ZuulRoute>();
        routesMap.putAll(locateRoutesFromAPIServer());
        
        LinkedHashMap<String, ZuulRoute> values = new LinkedHashMap<>();
        for (Map.Entry<String, ZuulRoute> entry : routesMap.entrySet()) {
            String path = entry.getKey();
            // Prepend with slash if not already present.
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            if (StringUtils.hasText(this.properties.getPrefix())) {
                path = this.properties.getPrefix() + path;
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
            }
            values.put(path, entry.getValue());
        }
        return values;
    }

    private Map<String, ZuulRoute> locateRoutesFromAPIServer(){
     	Map<String, ZuulRoute> routes = new LinkedHashMap<>();
        ApiClient client = null;
		try {
			client = Config.defaultClient();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        V1ServiceList list = null;
		try {
			list = api.listServiceForAllNamespaces(null, null, null, "prePath", null, null, null, null, null);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        for (V1Service item : list.getItems()) {
        	 	String svcName = item.getMetadata().getName();
        	 	Map<String, String> labels = item.getMetadata().getLabels();
        	 	List<V1ServicePort> ports = item.getSpec().getPorts();
        	 	if (!svcName.isEmpty() && labels.size() > 0 && ports.size() == 1) {
        	 		boolean hasKey = labels.containsKey("prePath");
        	 		if (hasKey) {
        	 			//1. process the path
        	 			String prePath = labels.get("prePath");
        	 			getStringBuilder().append("/").append(prePath).append("/**");
        	 			String path = sb.toString();
        	 			System.out.print("current route path is: "+ path);
        	 			
        	 			//2. process the port
        	 			V1ServicePort port = ports.get(0);
        	 			String servicePort = port.getPort().toString();
        	 			System.out.print("current route port is: "+ servicePort);
        	 			
        	 			//3. process the route path
        	 			getStringBuilder().append("http://").append(svcName).append(":").append(servicePort);
        	 			String url = sb.toString();
        	 			System.out.print("current route url is: "+ url);
        	 			
        	 			//4. construct the Route
        	 			ZuulRouteV0 customeizedRoute = new ZuulRouteV0(prePath,path,null,false,true,url,true);
        	 			ZuulRoute zuulRoute = new ZuulRoute();
        	            try {
                      org.springframework.beans.BeanUtils.copyProperties(customeizedRoute,zuulRoute);
        	            } catch (Exception e) {
        	            		//TODO:
        	            }
        	            
        	           //5. add the new route into routes
        	            routes.put(zuulRoute.getPath(),zuulRoute);
        	 		}
        	 	}
        }
        return routes;
    }
    
    public StringBuilder getStringBuilder() {
        sb.setLength(0);
        return sb;
    }
}
