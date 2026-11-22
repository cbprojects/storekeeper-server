package com.cafe.storekeeper.legacy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import jakarta.validation.UnexpectedTypeException;
import com.cafe.storekeeper.helper.enums.EMapApiErrors;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;
import com.cafe.storekeeper.infrastructure.exception.*;
class LegacyErrorsTest {
 @Test void exceptionConstructorsPreserveDiagnostics(){
  var error=new StandardErrorResponse(EMapApiErrors.ERROR_SERVER);
  assertTrue(new ModelException("test").getMessage().contains("test"));assertNotNull(new ModelException(error).getMessage());assertNotNull(new ModelException(EMapApiErrors.ERROR_SERVER).getMessage());
  assertTrue(new RequestException("test").getMessage().contains("test"));assertNotNull(new RequestException(error).getMessage());assertNotNull(new RequestException(EMapApiErrors.ERROR_SERVER).getMessage());
  assertTrue(new ApiClientException("test").getMessage().contains("test"));assertNotNull(new ApiClientException(error).getMessage());assertNotNull(new ApiClientException(EMapApiErrors.ERROR_SERVER).getMessage());
 }
 @Test void adviceMapsErrorsToExpectedHttpStatuses(){
  var handler=new ResponseExceptionHandler();assertEquals(500,handler.handleExceptions(new Exception("test")).getStatusCode().value());
  assertEquals(400,handler.handleUnexpectedTypeException(new UnexpectedTypeException("test")).getStatusCode().value());
  assertEquals(400,handler.handleBDExceptions(new ModelException("test"),null).getStatusCode().value());
  var missing=mock(ModelException.class);when(missing.getMessage()).thenReturn(ResponseExceptionHandler.BD_NOT_FOUND);assertEquals(204,handler.handleBDExceptions(missing,null).getStatusCode().value());
  assertEquals(400,handler.handleRequestExceptions(new RequestException("test")).getStatusCode().value());assertEquals(400,handler.handleApiClientExceptions(new ApiClientException("test")).getStatusCode().value());
 }
}
