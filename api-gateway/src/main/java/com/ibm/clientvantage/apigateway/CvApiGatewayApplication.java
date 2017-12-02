package com.ibm.clientvantage.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.ibm.clientvantage.apigateway.DemoController;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.ApiException;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;

@EnableZuulProxy
@SpringBootApplication
public class CvApiGatewayApplication {
	
	@Autowired
	private static MockMvc mvc;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(CvApiGatewayApplication.class, args);
		
		MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/refreshRoute"))
				.andExpect(handler().handlerType(DemoController.class))
	            .andReturn();  
		
		ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
        for (V1Pod item : list.getItems()) {
            System.out.println(item.getMetadata().getName());
        }
	}
}
