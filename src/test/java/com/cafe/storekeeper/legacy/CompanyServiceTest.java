package com.cafe.storekeeper.legacy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import com.cafe.storekeeper.infrastructure.domain.company.service.CompanyService;
import com.cafe.storekeeper.infrastructure.domain.company.mapper.CompanyMapper;
import com.cafe.storekeeper.infrastructure.domain.company.db.repository.CompanyRepository;
import com.cafe.storekeeper.infrastructure.domain.company.db.persistence.CompanyEntity;
import com.cafe.storekeeper.infrastructure.domain.company.rest.model.dto.CompanyDTO;
import com.cafe.storekeeper.infrastructure.exception.ModelException;
class CompanyServiceTest {
 final CompanyMapper mapper=mock(CompanyMapper.class);
 final CompanyRepository repository=mock(CompanyRepository.class);
 final CompanyService service=new CompanyService(mapper,repository);
 final CompanyEntity entity=new CompanyEntity();final CompanyDTO dto=new CompanyDTO();
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

}
