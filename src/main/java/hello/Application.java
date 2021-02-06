package hello;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
	SpringApplication.run(Application.class, args);
    }

    @Bean
    public Docket api() {
	return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage("hello.one"))
		.paths(PathSelectors.any()).build().groupName("one").pathMapping("/one");
    }

    @Bean
    public Docket api2() {
	return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage("hello.two"))
		.paths(PathSelectors.any()).build().groupName("two").pathMapping("/two");
    }

    @Bean
    public RestTemplate restTemplate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
	TrustStrategy acceptingTrustStrategy = (chain, authType) -> true;

	SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
		.build();

	SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

	CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();

	HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

	requestFactory.setHttpClient(httpClient);
	RestTemplate restTemplate = new RestTemplate(requestFactory);
	return restTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
	System.out.println("working?");
	ResponseEntity<String> response = restTemplate().getForEntity("https://www.anapioficeandfire.com/api/books",
		String.class);
	System.out.println("respose " + response.getBody());
//		sendGET();
    }

    private static final String USER_AGENT = "Mozilla/5.0";

    private static void sendGET() throws IOException {
	URL obj = new URL("https://www.anapioficeandfire.com/api/books");
	HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	con.setRequestMethod("GET");
	con.setRequestProperty("User-Agent", USER_AGENT);
	int responseCode = con.getResponseCode();
	System.out.println("GET Response Code :: " + responseCode);
	if (responseCode == HttpURLConnection.HTTP_OK) { // success
	    StringBuilder builder = new StringBuilder();
	    builder.append(con.getResponseCode()).append(" ").append(con.getResponseMessage()).append("\n");
	    Map<String, List<String>> map = con.getHeaderFields();
	    for (Map.Entry<String, List<String>> entry : map.entrySet()) {
		if (entry.getKey() == null)
		    continue;
		builder.append(entry.getKey()).append(": ");

		List<String> headerValues = entry.getValue();
		Iterator<String> it = headerValues.iterator();
		if (it.hasNext()) {
		    builder.append(it.next());

		    while (it.hasNext()) {
			builder.append(", ").append(it.next());
		    }
		}

		builder.append("\n");
	    }
	    System.out.println(builder.toString());
	} else {
	    System.out.println("GET request not worked");
	}

    }

}
