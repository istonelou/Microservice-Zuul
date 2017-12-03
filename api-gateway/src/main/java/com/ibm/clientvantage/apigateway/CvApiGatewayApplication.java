package com.ibm.clientvantage.apigateway;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@EnableZuulProxy
@SpringBootApplication
public class CvApiGatewayApplication {
	
	private static boolean mClusterActive = true;
	private static MockMvc mvc;
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(CvApiGatewayApplication.class, args);
		
		Timer timer = new Timer(); 
        timer.schedule(new RetrieveClusterServices(), 5000, 5000);//start the task after 5 seconds, and execute every 5 seconds
        
	}
	
	static class RetrieveClusterServices extends java.util.TimerTask {
        public void run(){
        		try {
					mvc.perform(MockMvcRequestBuilders.get("/refreshRoute"))
							.andDo(print());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }     
    }   
}
