package com.cafe.storekeeper.legacy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.bson.types.ObjectId;
import com.cafe.storekeeper.infrastructure.domain.assignment.service.AssignmentService;
import com.cafe.storekeeper.infrastructure.domain.assignment.mapper.AssignmentMapper;
import com.cafe.storekeeper.infrastructure.domain.assignment.db.repository.AssignmentRepository;
import com.cafe.storekeeper.infrastructure.domain.assignment.db.persistence.AssignmentEntity;
import com.cafe.storekeeper.infrastructure.domain.assignment.rest.model.dto.AssignmentDTO;
import com.cafe.storekeeper.infrastructure.domain.assignment.rest.controller.v1.AssignmentRestController;
import com.cafe.storekeeper.infrastructure.exception.ModelException;
class AssignmentServiceTest {
 @Test void employeeLookupAuditedSaveValidationAndFailures() throws Exception {
  var mapper=mock(AssignmentMapper.class);var repo=mock(AssignmentRepository.class);var service=new AssignmentService(mapper,repo);
  String id="012345678901234567890123";var entity=new AssignmentEntity();var dto=new AssignmentDTO();
  when(repo.findByEmployeeId(new ObjectId(id))).thenReturn(Optional.empty());assertNull(service.findByEmployeeId(id));
  when(repo.findByEmployeeId(new ObjectId(id))).thenReturn(Optional.of(entity));when(mapper.toDTO(entity)).thenReturn(dto);assertSame(dto,service.findByEmployeeId(id));
  assertThrows(ModelException.class,()->service.findByEmployeeId("invalid"));assertThrows(ModelException.class,()->service.save(null));
  when(mapper.toEntity(dto)).thenReturn(entity);when(repo.save(entity)).thenReturn(entity);assertSame(dto,service.save(dto));assertNotNull(entity.getCreateDate());assertEquals(entity.getCreateDate(),entity.getUpdateDate());
  when(repo.save(entity)).thenThrow(new IllegalStateException("database"));assertThrows(ModelException.class,()->service.save(dto));
 }
 @Test void controllerDelegatesLookupAndSave() throws Exception {
  var service=mock(com.cafe.storekeeper.infrastructure.adapter.IAssignmentService.class);var controller=new AssignmentRestController(service);var dto=new AssignmentDTO();
  when(service.findByEmployeeId("id")).thenReturn(dto);assertSame(dto,controller.findByEmployeeId("id").getBody());when(service.save(dto)).thenReturn(dto);assertSame(dto,controller.save(dto).getBody());
 }
}
