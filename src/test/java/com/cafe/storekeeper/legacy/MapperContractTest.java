package com.cafe.storekeeper.legacy;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
class MapperContractTest {
 private static void nullListElements(Object value) throws ReflectiveOperationException {
  for(Class<?> current=value.getClass();current!=Object.class;current=current.getSuperclass())for(Field field:current.getDeclaredFields()){
   if(Modifier.isStatic(field.getModifiers()))continue;field.setAccessible(true);Object child=field.get(value);
   if(field.getType()==List.class && child!=null)field.set(value,Arrays.asList((Object)null));
   else if(child!=null && !child.getClass().isEnum() && child.getClass().getName().startsWith("com.cafe.storekeeper"))nullListElements(child);
  }
 }
 // Every persisted field must survive an entity/DTO round trip, including nested contacts and lists.
 private static <T> T populated(Class<T> type) throws ReflectiveOperationException {
  T value=type.getConstructor().newInstance();
  for(Class<?> current=type;current!=Object.class;current=current.getSuperclass())for(Field field:current.getDeclaredFields()){
   if(Modifier.isStatic(field.getModifiers()))continue;field.setAccessible(true);Class<?> t=field.getType();Object sample;
   if(t==String.class)sample=field.getName()+"-value";else if(t==long.class)sample=42L;else if(t==int.class)sample=42;else if(t==boolean.class)sample=true;
   else if(t==BigDecimal.class)sample=new BigDecimal("123.45");else if(t==LocalDateTime.class)sample=LocalDateTime.of(2026,10,30,10,20);
   else if(t.isEnum())sample=t.getEnumConstants()[0];
   else if(t==List.class)sample=new ArrayList<>(List.of(populated((Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0])));
   else sample=populated(t);field.set(value,sample);
  }return value;
 }
@Test void productRoundTripPreservesAllFieldsAndNullSemantics() throws Exception {
  var mapper=Mappers.getMapper(com.cafe.storekeeper.infrastructure.domain.product.mapper.ProductMapper.class);
  var entity=populated(com.cafe.storekeeper.infrastructure.domain.product.db.persistence.ProductEntity.class);
  assertThat(mapper.toEntity(mapper.toDTO(entity))).usingRecursiveComparison().isEqualTo(entity);
  nullListElements(entity);assertThat(mapper.toEntity(mapper.toDTO(entity))).usingRecursiveComparison().isEqualTo(entity);
  var dto=populated(com.cafe.storekeeper.infrastructure.domain.product.rest.model.dto.ProductDTO.class);
  assertThat(mapper.toDTO(mapper.toEntity(dto))).usingRecursiveComparison().isEqualTo(dto);
  nullListElements(dto);assertThat(mapper.toDTO(mapper.toEntity(dto))).usingRecursiveComparison().isEqualTo(dto);
  assertNull(mapper.toDTO(null));assertNull(mapper.toEntity(null));assertNull(mapper.map(null));assertTrue(mapper.map(List.of()).isEmpty());assertNull(mapper.map(Arrays.asList((com.cafe.storekeeper.infrastructure.domain.product.db.persistence.ProductEntity)null)).getFirst());
  assertEquals(1,mapper.map(List.of(entity)).size());assertNotNull(mapper.toDTO(new com.cafe.storekeeper.infrastructure.domain.product.db.persistence.ProductEntity()));assertNotNull(mapper.toEntity(new com.cafe.storekeeper.infrastructure.domain.product.rest.model.dto.ProductDTO()));
 }
@Test void productCategoryRoundTripPreservesAllFieldsAndNullSemantics() throws Exception {
  var mapper=Mappers.getMapper(com.cafe.storekeeper.infrastructure.domain.product_category.mapper.ProductCategoryMapper.class);
  var entity=populated(com.cafe.storekeeper.infrastructure.domain.product_category.db.persistence.ProductCategoryEntity.class);
  assertThat(mapper.toEntity(mapper.toDTO(entity))).usingRecursiveComparison().isEqualTo(entity);
  nullListElements(entity);assertThat(mapper.toEntity(mapper.toDTO(entity))).usingRecursiveComparison().isEqualTo(entity);
  var dto=populated(com.cafe.storekeeper.infrastructure.domain.product_category.rest.model.dto.ProductCategoryDTO.class);
  assertThat(mapper.toDTO(mapper.toEntity(dto))).usingRecursiveComparison().isEqualTo(dto);
  nullListElements(dto);assertThat(mapper.toDTO(mapper.toEntity(dto))).usingRecursiveComparison().isEqualTo(dto);
  assertNull(mapper.toDTO(null));assertNull(mapper.toEntity(null));assertNull(mapper.map(null));assertTrue(mapper.map(List.of()).isEmpty());assertNull(mapper.map(Arrays.asList((com.cafe.storekeeper.infrastructure.domain.product_category.db.persistence.ProductCategoryEntity)null)).getFirst());
  assertEquals(1,mapper.map(List.of(entity)).size());assertNotNull(mapper.toDTO(new com.cafe.storekeeper.infrastructure.domain.product_category.db.persistence.ProductCategoryEntity()));assertNotNull(mapper.toEntity(new com.cafe.storekeeper.infrastructure.domain.product_category.rest.model.dto.ProductCategoryDTO()));
 }
@Test void clientRoundTripPreservesAllFieldsAndNullSemantics() throws Exception {
  var mapper=Mappers.getMapper(com.cafe.storekeeper.infrastructure.domain.client.mapper.ClientMapper.class);
  var entity=populated(com.cafe.storekeeper.infrastructure.domain.client.db.persistence.ClientEntity.class);
  assertThat(mapper.toEntity(mapper.toDTO(entity))).usingRecursiveComparison().isEqualTo(entity);
  nullListElements(entity);assertThat(mapper.toEntity(mapper.toDTO(entity))).usingRecursiveComparison().isEqualTo(entity);
  var dto=populated(com.cafe.storekeeper.infrastructure.domain.client.rest.model.dto.ClientDTO.class);
  assertThat(mapper.toDTO(mapper.toEntity(dto))).usingRecursiveComparison().isEqualTo(dto);
  nullListElements(dto);assertThat(mapper.toDTO(mapper.toEntity(dto))).usingRecursiveComparison().isEqualTo(dto);
  assertNull(mapper.toDTO(null));assertNull(mapper.toEntity(null));assertNull(mapper.map(null));assertTrue(mapper.map(List.of()).isEmpty());assertNull(mapper.map(Arrays.asList((com.cafe.storekeeper.infrastructure.domain.client.db.persistence.ClientEntity)null)).getFirst());
  assertEquals(1,mapper.map(List.of(entity)).size());assertNotNull(mapper.toDTO(new com.cafe.storekeeper.infrastructure.domain.client.db.persistence.ClientEntity()));assertNotNull(mapper.toEntity(new com.cafe.storekeeper.infrastructure.domain.client.rest.model.dto.ClientDTO()));
 }
@Test void companyRoundTripPreservesAllFieldsAndNullSemantics() throws Exception {
  var mapper=Mappers.getMapper(com.cafe.storekeeper.infrastructure.domain.company.mapper.CompanyMapper.class);
  var entity=populated(com.cafe.storekeeper.infrastructure.domain.company.db.persistence.CompanyEntity.class);
  assertThat(mapper.toEntity(mapper.toDTO(entity))).usingRecursiveComparison().isEqualTo(entity);
  nullListElements(entity);assertThat(mapper.toEntity(mapper.toDTO(entity))).usingRecursiveComparison().isEqualTo(entity);
  var dto=populated(com.cafe.storekeeper.infrastructure.domain.company.rest.model.dto.CompanyDTO.class);
  assertThat(mapper.toDTO(mapper.toEntity(dto))).usingRecursiveComparison().isEqualTo(dto);
  nullListElements(dto);assertThat(mapper.toDTO(mapper.toEntity(dto))).usingRecursiveComparison().isEqualTo(dto);
  assertNull(mapper.toDTO(null));assertNull(mapper.toEntity(null));assertNull(mapper.map(null));assertTrue(mapper.map(List.of()).isEmpty());assertNull(mapper.map(Arrays.asList((com.cafe.storekeeper.infrastructure.domain.company.db.persistence.CompanyEntity)null)).getFirst());
  assertEquals(1,mapper.map(List.of(entity)).size());assertNotNull(mapper.toDTO(new com.cafe.storekeeper.infrastructure.domain.company.db.persistence.CompanyEntity()));assertNotNull(mapper.toEntity(new com.cafe.storekeeper.infrastructure.domain.company.rest.model.dto.CompanyDTO()));
 }
@Test void employeeRoundTripPreservesAllFieldsAndNullSemantics() throws Exception {
  var mapper=Mappers.getMapper(com.cafe.storekeeper.infrastructure.domain.employee.mapper.EmployeeMapper.class);
  var entity=populated(com.cafe.storekeeper.infrastructure.domain.employee.db.persistence.EmployeeEntity.class);
  assertThat(mapper.toEntity(mapper.toDTO(entity))).usingRecursiveComparison().isEqualTo(entity);
  nullListElements(entity);assertThat(mapper.toEntity(mapper.toDTO(entity))).usingRecursiveComparison().isEqualTo(entity);
  var dto=populated(com.cafe.storekeeper.infrastructure.domain.employee.rest.model.dto.EmployeeDTO.class);
  assertThat(mapper.toDTO(mapper.toEntity(dto))).usingRecursiveComparison().isEqualTo(dto);
  nullListElements(dto);assertThat(mapper.toDTO(mapper.toEntity(dto))).usingRecursiveComparison().isEqualTo(dto);
  assertNull(mapper.toDTO(null));assertNull(mapper.toEntity(null));assertNull(mapper.map(null));assertTrue(mapper.map(List.of()).isEmpty());assertNull(mapper.map(Arrays.asList((com.cafe.storekeeper.infrastructure.domain.employee.db.persistence.EmployeeEntity)null)).getFirst());
  assertEquals(1,mapper.map(List.of(entity)).size());assertNotNull(mapper.toDTO(new com.cafe.storekeeper.infrastructure.domain.employee.db.persistence.EmployeeEntity()));assertNotNull(mapper.toEntity(new com.cafe.storekeeper.infrastructure.domain.employee.rest.model.dto.EmployeeDTO()));
 }
@Test void providerRoundTripPreservesAllFieldsAndNullSemantics() throws Exception {
  var mapper=Mappers.getMapper(com.cafe.storekeeper.infrastructure.domain.provider.mapper.ProviderMapper.class);
  var entity=populated(com.cafe.storekeeper.infrastructure.domain.provider.db.persistence.ProviderEntity.class);
  assertThat(mapper.toEntity(mapper.toDTO(entity))).usingRecursiveComparison().isEqualTo(entity);
  nullListElements(entity);assertThat(mapper.toEntity(mapper.toDTO(entity))).usingRecursiveComparison().isEqualTo(entity);
  var dto=populated(com.cafe.storekeeper.infrastructure.domain.provider.rest.model.dto.ProviderDTO.class);
  assertThat(mapper.toDTO(mapper.toEntity(dto))).usingRecursiveComparison().isEqualTo(dto);
  nullListElements(dto);assertThat(mapper.toDTO(mapper.toEntity(dto))).usingRecursiveComparison().isEqualTo(dto);
  assertNull(mapper.toDTO(null));assertNull(mapper.toEntity(null));assertNull(mapper.map(null));assertTrue(mapper.map(List.of()).isEmpty());assertNull(mapper.map(Arrays.asList((com.cafe.storekeeper.infrastructure.domain.provider.db.persistence.ProviderEntity)null)).getFirst());
  assertEquals(1,mapper.map(List.of(entity)).size());assertNotNull(mapper.toDTO(new com.cafe.storekeeper.infrastructure.domain.provider.db.persistence.ProviderEntity()));assertNotNull(mapper.toEntity(new com.cafe.storekeeper.infrastructure.domain.provider.rest.model.dto.ProviderDTO()));
 }
@Test void billRoundTripPreservesAllFieldsAndNullSemantics() throws Exception {
  var mapper=Mappers.getMapper(com.cafe.storekeeper.infrastructure.domain.bill.mapper.BillMapper.class);
  var entity=populated(com.cafe.storekeeper.infrastructure.domain.bill.db.persistence.BillEntity.class);
  assertThat(mapper.toEntity(mapper.toDTO(entity))).usingRecursiveComparison().isEqualTo(entity);
  nullListElements(entity);assertThat(mapper.toEntity(mapper.toDTO(entity))).usingRecursiveComparison().isEqualTo(entity);
  var dto=populated(com.cafe.storekeeper.infrastructure.domain.bill.rest.model.dto.BillDTO.class);
  assertThat(mapper.toDTO(mapper.toEntity(dto))).usingRecursiveComparison().isEqualTo(dto);
  nullListElements(dto);assertThat(mapper.toDTO(mapper.toEntity(dto))).usingRecursiveComparison().isEqualTo(dto);
  assertNull(mapper.toDTO(null));assertNull(mapper.toEntity(null));assertNull(mapper.map(null));assertTrue(mapper.map(List.of()).isEmpty());assertNull(mapper.map(Arrays.asList((com.cafe.storekeeper.infrastructure.domain.bill.db.persistence.BillEntity)null)).getFirst());
  assertEquals(1,mapper.map(List.of(entity)).size());assertNotNull(mapper.toDTO(new com.cafe.storekeeper.infrastructure.domain.bill.db.persistence.BillEntity()));assertNotNull(mapper.toEntity(new com.cafe.storekeeper.infrastructure.domain.bill.rest.model.dto.BillDTO()));
 }
@Test void assignmentRoundTripPreservesAllFieldsAndNullSemantics() throws Exception {
  var mapper=Mappers.getMapper(com.cafe.storekeeper.infrastructure.domain.assignment.mapper.AssignmentMapper.class);
  var entity=populated(com.cafe.storekeeper.infrastructure.domain.assignment.db.persistence.AssignmentEntity.class);
  assertThat(mapper.toEntity(mapper.toDTO(entity))).usingRecursiveComparison().isEqualTo(entity);
  nullListElements(entity);assertThat(mapper.toEntity(mapper.toDTO(entity))).usingRecursiveComparison().isEqualTo(entity);
  var dto=populated(com.cafe.storekeeper.infrastructure.domain.assignment.rest.model.dto.AssignmentDTO.class);
  assertThat(mapper.toDTO(mapper.toEntity(dto))).usingRecursiveComparison().isEqualTo(dto);
  nullListElements(dto);assertThat(mapper.toDTO(mapper.toEntity(dto))).usingRecursiveComparison().isEqualTo(dto);
  assertNull(mapper.toDTO(null));assertNull(mapper.toEntity(null));assertNull(mapper.map(null));assertTrue(mapper.map(List.of()).isEmpty());assertNull(mapper.map(Arrays.asList((com.cafe.storekeeper.infrastructure.domain.assignment.db.persistence.AssignmentEntity)null)).getFirst());
  assertEquals(1,mapper.map(List.of(entity)).size());assertNotNull(mapper.toDTO(new com.cafe.storekeeper.infrastructure.domain.assignment.db.persistence.AssignmentEntity()));assertNotNull(mapper.toEntity(new com.cafe.storekeeper.infrastructure.domain.assignment.rest.model.dto.AssignmentDTO()));
 }
 @Test void assignmentMapsClientsWithoutContactData(){
  var mapper=Mappers.getMapper(com.cafe.storekeeper.infrastructure.domain.assignment.mapper.AssignmentMapper.class);
  var entity=new com.cafe.storekeeper.infrastructure.domain.assignment.db.persistence.AssignmentEntity();entity.setClients(List.of(new com.cafe.storekeeper.infrastructure.domain.client.db.persistence.ClientEntity()));assertNull(mapper.toDTO(entity).getClients().getFirst().getInfo());
  var dto=new com.cafe.storekeeper.infrastructure.domain.assignment.rest.model.dto.AssignmentDTO();dto.setClients(List.of(new com.cafe.storekeeper.infrastructure.domain.client.rest.model.dto.ClientDTO()));assertNull(mapper.toEntity(dto).getClients().getFirst().getInfo());
 }
}
