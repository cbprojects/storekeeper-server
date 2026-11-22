package com.cafe.storekeeper.legacy;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import com.cafe.storekeeper.infrastructure.adapter.AbstractRestApiClient;
class RestApiClientTest {
 @Test void verbsHeadersAndEncodedParametersReachTheCorrectUrl(){
  var template=new RestTemplate();var server=MockRestServiceServer.bindTo(template).build();var client=new AbstractRestApiClient(template);
  server.expect(requestTo("https://example.test/items")).andExpect(method(HttpMethod.GET)).andExpect(header("Authorization","Basic token")).andRespond(withSuccess("ok",MediaType.TEXT_PLAIN));
  assertEquals("ok",client.getJSON("https://example.test/items","token",String.class).getBody());
  server.verify();server.reset();
  server.expect(requestTo("https://example.test/items?name=John%20Smith")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess("ok",MediaType.TEXT_PLAIN));
  assertEquals("ok",client.getJSON("https://example.test/items",null,Map.of("name","John Smith"),String.class).getBody());
  server.verify();server.reset();
  server.expect(requestTo("https://example.test/items")).andRespond(withSuccess("ok",MediaType.TEXT_PLAIN));client.getJSON("https://example.test/items",null,null,String.class);server.verify();server.reset();
  server.expect(requestTo("https://example.test/items")).andExpect(method(HttpMethod.POST)).andRespond(withSuccess("ok",MediaType.TEXT_PLAIN));client.postJSON("https://example.test/items","body",String.class);server.verify();server.reset();
  server.expect(requestTo("https://example.test/items")).andExpect(method(HttpMethod.PUT)).andRespond(withSuccess("ok",MediaType.TEXT_PLAIN));client.putJSON("https://example.test/items","body","token",String.class);server.verify();
  assertTrue(client.buildHttpHeaders(" ").isEmpty());assertEquals("body",client.buildHttpEntity("body",null).getBody());
 }
}
