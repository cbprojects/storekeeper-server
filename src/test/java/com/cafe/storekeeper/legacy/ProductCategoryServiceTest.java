package com.cafe.storekeeper.legacy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import com.cafe.storekeeper.infrastructure.domain.product_category.service.ProductCategoryService;
import com.cafe.storekeeper.infrastructure.domain.product_category.mapper.ProductCategoryMapper;
import com.cafe.storekeeper.infrastructure.domain.product_category.db.repository.ProductCategoryRepository;
import com.cafe.storekeeper.infrastructure.domain.product_category.db.persistence.ProductCategoryEntity;
import com.cafe.storekeeper.infrastructure.domain.product_category.rest.model.dto.ProductCategoryDTO;
import com.cafe.storekeeper.infrastructure.exception.ModelException;
class ProductCategoryServiceTest {
 final ProductCategoryMapper mapper=mock(ProductCategoryMapper.class);
 final ProductCategoryRepository repository=mock(ProductCategoryRepository.class);
 final ProductCategoryService service=new ProductCategoryService(mapper,repository);
 final ProductCategoryEntity entity=new ProductCategoryEntity();final ProductCategoryDTO dto=new ProductCategoryDTO();
 {dto.setCode("C");dto.setName("Category");dto.setDescription("Description");}
 @Test void lookupReturnsMappedValueOrNullAndWrapsDatabaseFailure() throws Exception {
  when(repository.findById("id")).thenReturn(Optional.of(entity));when(mapper.toDTO(entity)).thenReturn(dto);
  assertSame(dto,service.findById("id"));when(repository.findById("id")).thenReturn(Optional.empty());assertNull(service.findById("id"));
  when(repository.findById("id")).thenThrow(new IllegalStateException("database"));assertThrows(ModelException.class,()->service.findById("id"));
 }
 @Test void listHandlesEmptyResultsMappingAndDatabaseFailure() throws Exception {
  when(repository.findAll()).thenReturn(List.of());assertTrue(service.find(Map.of()).isEmpty());
  when(repository.findAll()).thenReturn(List.of(entity));when(mapper.map(List.of(entity))).thenReturn(List.of(dto));assertEquals(List.of(dto),service.find(Map.of()));
  when(repository.findAll()).thenThrow(new IllegalStateException("database"));assertThrows(ModelException.class,()->service.find(Map.of()));
 }
 @Test void saveRejectsNullPersistsAuditAndWrapsDatabaseFailure() throws Exception {
  assertThrows(ModelException.class,()->service.save(null));verifyNoInteractions(repository);
  when(mapper.toEntity(dto)).thenReturn(entity);when(repository.save(entity)).thenReturn(entity);when(mapper.toDTO(entity)).thenReturn(dto);
  assertSame(dto,service.save(dto));assertNotNull(entity.getCreateDate());assertEquals(entity.getCreateDate(),entity.getUpdateDate());verify(repository).save(entity);
  when(repository.save(entity)).thenThrow(new IllegalStateException("database"));assertThrows(ModelException.class,()->service.save(dto));
 }
 @Test void deleteRequiresExistingRecordAndWrapsFailures() throws Exception {
  when(repository.findById("id")).thenReturn(Optional.empty());assertThrows(ModelException.class,()->service.delete("id"));verify(repository,never()).delete(any());
  when(repository.findById("id")).thenReturn(Optional.of(entity));assertTrue(service.delete("id"));verify(repository).delete(entity);
  doThrow(new IllegalStateException("database")).when(repository).delete(entity);assertThrows(ModelException.class,()->service.delete("id"));
 }

 @Test void eachRequiredCategoryFieldIsValidated(){
 var invalid=new ProductCategoryDTO();assertNotNull(com.cafe.storekeeper.infrastructure.domain.product_category.validator.ProductCategoryValidator.isValidProductCategoryDTO(invalid));invalid.setCode("C");assertNotNull(com.cafe.storekeeper.infrastructure.domain.product_category.validator.ProductCategoryValidator.isValidProductCategoryDTO(invalid));invalid.setName("Name");assertNotNull(com.cafe.storekeeper.infrastructure.domain.product_category.validator.ProductCategoryValidator.isValidProductCategoryDTO(invalid));invalid.setDescription("Description");assertNull(com.cafe.storekeeper.infrastructure.domain.product_category.validator.ProductCategoryValidator.isValidProductCategoryDTO(invalid));
 }
}
