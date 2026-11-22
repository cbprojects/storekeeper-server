package com.cafe.storekeeper;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionTemplate;
import com.cafe.storekeeper.configuration.*;
import com.cafe.storekeeper.platform.commerce.configuration.CommerceConfiguration;
class ApplicationConfigurationTest {
 @Test void bootstrapUsesApplicationAndBuildsHttpClient(){
  var args=new String[]{"--server.port=0"};try(var spring=mockStatic(SpringApplication.class)){Application.main(args);spring.verify(()->SpringApplication.run(Application.class,args));}
  var application=new Application();var builder=new SpringApplicationBuilder();assertSame(builder,application.configure(builder));assertNotNull(application.restTemplate(new RestTemplateBuilder()));
 }
 @Test void openApiPublishesConfiguredMetadata(){
  var props=new OpenApiProperties();props.setTitle("Store Keeper");props.setDescription("API");props.setVersion("1");props.setTerms("https://example.test/terms");props.setLicence("Licence");props.setLicenceUrl("https://example.test/license");
  var contact=new OpenApiContactProperties();contact.setName("Support");contact.setUrl("https://example.test");contact.setEmail("support@example.test");props.setContact(contact);
  var info=new OpenApiConfig(props).getOpenApi().getInfo();assertEquals("Store Keeper",info.getTitle());assertEquals("1",info.getVersion());assertEquals("support@example.test",info.getContact().getEmail());assertEquals(props.getLicenceUrl(),info.getLicense().getUrl());
 }
 @Test void commerceRequiresMongoTransactions(){
  TransactionTemplate template=ReflectionTestUtils.invokeMethod(new CommerceConfiguration(),"commerceTransactions",mock(MongoDatabaseFactory.class));assertNotNull(template);assertInstanceOf(org.springframework.data.mongodb.MongoTransactionManager.class,template.getTransactionManager());
 }
}
