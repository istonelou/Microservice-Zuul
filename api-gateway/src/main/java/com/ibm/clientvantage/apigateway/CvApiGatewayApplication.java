package com.ibm.clientvantage.apigateway;

import java.util.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;

@EnableZuulProxy
@SpringBootApplication
@ComponentScan(basePackages = {"com.ibm.clientvantage.apigateway"})
public class CvApiGatewayApplication {
	
	private static boolean mClusterActive = true;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(CvApiGatewayApplication.class, args);
		
		Timer timer = new Timer(); 
        timer.schedule(new RetrieveClusterServices(), 10000, 5000);//start the task after 5 seconds, and execute every 5 seconds
	}  
}

class RetrieveClusterServices extends java.util.TimerTask{
	
	@Autowired
	RefreshRouteService mRefreshService;
	
	@Override  
	public void run() {
		mRefreshService.refreshRoute();
		System.out.println("now start to retrieve the services");
	}  
}  
