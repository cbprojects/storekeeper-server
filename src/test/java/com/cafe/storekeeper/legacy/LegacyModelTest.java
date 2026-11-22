package com.cafe.storekeeper.legacy;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.Test;
import com.cafe.storekeeper.infrastructure.domain.bill.db.persistence.BillEntity;
import com.cafe.storekeeper.infrastructure.domain.bill.db.pojo.TaxPojo;
import com.cafe.storekeeper.infrastructure.domain.bill.rest.model.dto.*;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardPaginationRestResponse;
class LegacyModelTest {
 @Test void taxTotalsAreExactAndIgnoreMissingAmounts(){
  var entity=new BillEntity();var dto=new BillDTO();assertEquals(BigDecimal.ZERO,entity.getTotalTaxes());assertEquals(BigDecimal.ZERO,dto.getTotalTaxes());
  entity.setTaxes(List.of());dto.setTaxes(List.of());assertEquals(BigDecimal.ZERO,entity.getTotalTaxes());assertEquals(BigDecimal.ZERO,dto.getTotalTaxes());
  entity.setTaxes(List.of(new TaxPojo("tax",new BigDecimal("0.10")),new TaxPojo("tax",new BigDecimal("0.20")),new TaxPojo("empty",null)));
  dto.setTaxes(List.of(new TaxDTO("tax",new BigDecimal("0.10")),new TaxDTO("tax",new BigDecimal("0.20")),new TaxDTO("empty",null)));
  assertEquals(new BigDecimal("0.30"),entity.getTotalTaxes());assertEquals(new BigDecimal("0.30"),dto.getTotalTaxes());
 }
 @Test void paginationSurvivesJavaSerialization() throws Exception {
  var response=new StandardPaginationRestResponse();response.setResult(new ArrayList<>(List.of("result")));var output=new ByteArrayOutputStream();
  try(var writer=new ObjectOutputStream(output)){writer.writeObject(response);}try(var reader=new ObjectInputStream(new ByteArrayInputStream(output.toByteArray()))){assertEquals(response.getResult(),((StandardPaginationRestResponse)reader.readObject()).getResult());}
 }
 @Test void EBillStatusHasStableNames(){for(var value:com.cafe.storekeeper.helper.enums.EBillStatus.values())assertNotNull(value.getNombre());}
 @Test void EBillTypeHasStableNames(){for(var value:com.cafe.storekeeper.helper.enums.EBillType.values())assertNotNull(value.getNombre());}
 @Test void EDocumentTypeHasStableNames(){for(var value:com.cafe.storekeeper.helper.enums.EDocumentType.values())assertNotNull(value.getNombre());}
 @Test void EPaymentMethodHasStableNames(){for(var value:com.cafe.storekeeper.helper.enums.EPaymentMethod.values())assertNotNull(value.getNombre());}
 @Test void EProductTypeHasStableNames(){for(var value:com.cafe.storekeeper.helper.enums.EProductType.values())assertNotNull(value.getNombre());}
 @Test void EUnitHasStableNames(){for(var value:com.cafe.storekeeper.helper.enums.EUnit.values())assertNotNull(value.getNombre());}
 @Test void EYesNoHasStableNames(){for(var value:com.cafe.storekeeper.helper.enums.EYesNo.values())assertNotNull(value.getNombre());}
}
