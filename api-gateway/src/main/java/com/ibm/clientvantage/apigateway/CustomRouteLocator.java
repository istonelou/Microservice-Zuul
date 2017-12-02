package com.ibm.clientvantage.apigateway;

import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;

import org.springframework.util.StringUtils;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.models.V1Service;
import io.kubernetes.client.models.V1ServiceList;
import io.kubernetes.client.util.Config;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomRouteLocator extends SimpleRouteLocator implements RefreshableRouteLocator{


    private ZuulProperties properties;


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
        //从application.properties中加载路由信息
        routesMap.putAll(super.locateRoutes());
        //从db中加载路由信息
        locateRoutesFromAPIServer();
        //routesMap.putAll(locateRoutesFromDB());
        //routesMap.putAll(locateRoutesFromDB());
        //优化一下配置
        LinkedHashMap<String, ZuulRoute> values = new LinkedHashMap<>();
//        for (Map.Entry<String, ZuulRoute> entry : routesMap.entrySet()) {
//            String path = entry.getKey();
//            // Prepend with slash if not already present.
//            if (!path.startsWith("/")) {
//                path = "/" + path;
//            }
//            if (StringUtils.hasText(this.properties.getPrefix())) {
//                path = this.properties.getPrefix() + path;
//                if (!path.startsWith("/")) {
//                    path = "/" + path;
//                }
//            }
//            values.put(path, entry.getValue());
//        }
        return values;
    }

    private Map<String, ZuulRoute> locateRoutesFromAPIServer(){
    	
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
        	 	System.out.println("name" + item.getMetadata().getName());
        	 	System.out.println("Labels" + item.getMetadata().getLabels());
        	 	System.out.println("Ports" + item.getSpec().getPorts());
        }
        Map<String, ZuulRoute> routes = new LinkedHashMap<>();
//        //List<ZuulRouteVO> results = jdbcTemplate.query("select * from gateway_api_define where enabled = true ",new BeanPropertyRowMapper<>(ZuulRouteVO.class));
//        for (ZuulRouteVO result : results) {
//            ZuulRoute zuulRoute = new ZuulRoute();
//            try {
//                org.springframework.beans.BeanUtils.copyProperties(result,zuulRoute);
//            } catch (Exception e) {
//            }
//            routes.put(zuulRoute.getPath(),zuulRoute);
//        }
        return routes;
    }

    public static class ZuulRouteVO {

        /**
         * The ID of the route (the same as its map key by default).
         */
        private String id;

        /**
         * The path (pattern) for the route, e.g. /foo/**.
         */
        private String path;

        /**
         * The service ID (if any) to map to this route. You can specify a physical URL or
         * a service, but not both.
         */
        private String serviceId;

        /**
         * A full physical URL to map to the route. An alternative is to use a service ID
         * and service discovery to find the physical address.
         */
        private String url;

        /**
         * Flag to determine whether the prefix for this route (the path, minus pattern
         * patcher) should be stripped before forwarding.
         */
        private boolean stripPrefix = true;

        /**
         * Flag to indicate that this route should be retryable (if supported). Generally
         * retry requires a service ID and ribbon.
         */
        private Boolean retryable;

        private String apiName;

        private Boolean enabled;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getServiceId() {
            return serviceId;
        }

        public void setServiceId(String serviceId) {
            this.serviceId = serviceId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isStripPrefix() {
            return stripPrefix;
        }

        public void setStripPrefix(boolean stripPrefix) {
            this.stripPrefix = stripPrefix;
        }

        public Boolean getRetryable() {
            return retryable;
        }

        public void setRetryable(Boolean retryable) {
            this.retryable = retryable;
        }

        public String getApiName() {
            return apiName;
        }

        public void setApiName(String apiName) {
            this.apiName = apiName;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }
}
