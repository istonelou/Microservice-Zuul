package com.ibm.clientvantage.apigateway;

import java.util.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;

@EnableZuulProxy
@SpringBootApplication
public class CvApiGatewayApplication implements CommandLineRunner{
	
	private static boolean mClusterActive = true;
	
	@Autowired
	RefreshRouteService mRefreshService;
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(CvApiGatewayApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		Timer timer = new Timer(); 
        timer.schedule(new RetrieveClusterServices(mRefreshService), 10000, 5000);//start the task after 5 seconds, and execute every 5 seconds
	}
}

class RetrieveClusterServices extends java.util.TimerTask{

	private RefreshRouteService mService;
	
	public RetrieveClusterServices(RefreshRouteService service) {
		super();
		this.mService = service;
	}
	
	@Override  
	public void run() {
		mService.refreshRoute();
		System.out.println("now start to retrieve the services");
	}  
}  
