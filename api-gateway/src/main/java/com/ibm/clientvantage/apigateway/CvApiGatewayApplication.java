package com.ibm.clientvantage.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.beans.factory.annotation.Autowired;

@EnableZuulProxy
@SpringBootApplication
@AutoConfigureMockMvc
public class CvApiGatewayApplication {
	
	@Autowired
	private static MockMvc mvc;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(CvApiGatewayApplication.class, args);
		
		mvc.perform(MockMvcRequestBuilders.get("/refreshRoute"))
				.andDo(print()) 
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
