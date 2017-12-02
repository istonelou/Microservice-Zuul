package com.ibm.clientvantage.apigateway;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class CvApiGatewayApplication {
	
    @Autowired
    private static RefreshRouteService refreshRouteService;
    
	private static boolean mClusterActive = true;
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(CvApiGatewayApplication.class, args);
		
		Timer timer = new Timer(); 
		System.out.println(df.format(new Date()));
        timer.schedule(new RetrieveClusterServices(), 30000, 5000);//start the task after 5 seconds, and execute every 5 seconds
        
	}
	
	static class RetrieveClusterServices extends java.util.TimerTask {
        public void run(){
        		System.out.println(df.format(new Date()));
        		refreshRouteService.refreshRoute();
        }     
    }   
}
