package com.cafe.storekeeper.legacy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import com.cafe.storekeeper.infrastructure.domain.bill.service.BillService;
import com.cafe.storekeeper.infrastructure.domain.bill.mapper.BillMapper;
import com.cafe.storekeeper.infrastructure.domain.bill.db.repository.BillRepository;
import com.cafe.storekeeper.infrastructure.domain.bill.db.persistence.BillEntity;
import com.cafe.storekeeper.infrastructure.domain.bill.rest.model.dto.BillDTO;
import com.cafe.storekeeper.infrastructure.exception.ModelException;
class BillServiceTest {
 final BillMapper mapper=mock(BillMapper.class);
 final BillRepository repository=mock(BillRepository.class);
 final BillService service=new BillService(mapper,repository);
 final BillEntity entity=new BillEntity();final BillDTO dto=new BillDTO();
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
 @Test void statusUpdatesPersistAuditAndHandleMissingInvalidAndFailure() throws Exception {
    var request=new com.cafe.storekeeper.infrastructure.domain.bill.rest.model.request.StatusBillRequest();request.setId("id");request.setStatus(com.cafe.storekeeper.helper.enums.EBillStatus.values()[0]);
    assertThrows(ModelException.class,()->service.updateStatus(null));when(repository.findById("id")).thenReturn(Optional.empty());assertNull(service.updateStatus(request));
    when(repository.findById("id")).thenReturn(Optional.of(entity));when(repository.save(entity)).thenReturn(entity);when(mapper.toDTO(entity)).thenReturn(dto);
    assertSame(dto,service.updateStatus(request));assertEquals(request.getStatus(),entity.getStatus());assertNotNull(entity.getUpdateDate());
    when(repository.save(entity)).thenThrow(new IllegalStateException("database"));assertThrows(ModelException.class,()->service.updateStatus(request));
 }
}
