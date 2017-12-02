package com.ibm.clientvantage.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import com.ibm.clientvantage.apigateway.DemoController;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.ApiException;
import java.io.IOException;

@EnableZuulProxy
@SpringBootApplication
public class CvApiGatewayApplication {

	public static void main(String[] args) throws IOException, ApiException {
		SpringApplication.run(CvApiGatewayApplication.class, args);
		
		DemoController controller = new DemoController();
		controller.refreshRoute();
		
		ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
        for (V1Pod item : list.getItems()) {
            System.out.println(item.getMetadata().getName());
        }
	}
}
