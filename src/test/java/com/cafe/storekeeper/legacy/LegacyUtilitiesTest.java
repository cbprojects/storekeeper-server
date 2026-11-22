package com.cafe.storekeeper.legacy;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mock.web.MockHttpServletRequest;
import com.cafe.storekeeper.helper.util.*;
import com.cafe.storekeeper.helper.constant.ConstantsRest;
import com.cafe.storekeeper.helper.enums.EMapApiErrors;
import com.cafe.storekeeper.infrastructure.exception.RequestException;

class LegacyUtilitiesTest {
 @Test void numericParsingAndDateFormatting(){
  assertTrue(Util.isNumeric("42"));assertTrue(Util.isNumeric("1.5"));assertFalse(Util.isNumeric("no"));
  assertTrue(Util.isLong("9223372036854775807"));assertFalse(Util.isLong("too big"));assertFalse(Util.isInteger("1.2"));
  assertTrue(Util.isDate("2026-10-30",DateTimeFormatter.ISO_LOCAL_DATE));assertFalse(Util.isDate("invalid",DateTimeFormatter.ISO_LOCAL_DATE));
  assertTrue(Util.isDateTime("2026-10-30T10:00:00",DateTimeFormatter.ISO_LOCAL_DATE_TIME));assertFalse(Util.isDateTime("invalid",DateTimeFormatter.ISO_LOCAL_DATE_TIME));
  assertEquals("2026-10-30",Util.formatDate("uuuu-MM-dd",LocalDate.of(2026,10,30)));assertEquals("",Util.formatDate("[",null));
  assertEquals("2026",Util.formatDateTime("uuuu",LocalDateTime.of(2026,10,30,10,0)));assertEquals("",Util.formatDateTime(null,null));
  assertNull(Util.getDecimalFormatSymbols(null,',','.'));
  assertEquals("1,234.50",Util.formatCurrencyBigDecimal("$",',','.',new BigDecimal("1234.5")));assertEquals("",Util.formatCurrencyBigDecimal("$",',','.',null));
  assertEquals("",Util.concatenate(null));assertEquals("",Util.concatenate(List.of()));assertEquals("ab",Util.concatenate(List.of("a","b")));
  assertFalse(Util.isPresentFirstElement(null));assertFalse(Util.isPresentFirstElement(List.of()));assertFalse(Util.isPresentFirstElement(Arrays.asList((String)null)));assertTrue(Util.isPresentFirstElement(List.of("a")));
 }
 @Test void paginationAndSortingPreserveQueries(){
  assertNull(Util.paginateResults(null,null));var query=new Query();assertSame(query,Util.paginateResults(query,null));Util.paginateResults(query,Map.of());
  Util.paginateResults(query,Map.of(ConstantsRest.COUNT,"5",ConstantsRest.SKIP,"10"));assertEquals(5,query.getLimit());assertEquals(10,query.getSkip());
  Util.paginateResults(query,Map.of(ConstantsRest.COUNT,"bad",ConstantsRest.SKIP," "));assertEquals(5,query.getLimit());
  assertNotNull(Util.addSort(null,null));assertTrue(Util.addSort(new Query(),Map.of()).getSortObject().isEmpty());
  assertEquals(1,Util.addSort(new Query(),Map.of(ConstantsRest.SORT,"name")).getSortObject().get("name"));
  assertEquals(-1,Util.addSort(new Query(),Map.of(ConstantsRest.SORT,"name",ConstantsRest.TYPE_SORT,"DESC")).getSortObject().get("name"));
  assertEquals(1,Util.addSort(new Query(),Map.of(ConstantsRest.SORT,"name",ConstantsRest.TYPE_SORT," ")).getSortObject().get("name"));
  assertTrue(Util.addSort(new Query(),Map.of(ConstantsRest.SORT," ")).getSortObject().isEmpty());
 }
 @Test void requestCriteriaAndErrorResponses() throws Exception {
  var request=new MockHttpServletRequest();assertTrue(UtilResponse.buildCriteriaParams(request).isEmpty());request.setQueryString(" ");assertTrue(UtilResponse.buildCriteriaParams(request).isEmpty());
  request.setQueryString("name=John%20Smith&count=2");assertEquals("John Smith",UtilResponse.buildCriteriaParams(request).get("name"));request.setQueryString("broken");assertThrows(RequestException.class,()->UtilResponse.buildCriteriaParams(request));
  assertNull(UtilResponse.fillResponsePagination(null,0,0).getResult());var response=UtilResponse.fillResponsePagination(List.of("a"),1,2);assertEquals(2,response.getPagination().getTotalCount());
  for(var error:EMapApiErrors.values()){assertEquals(error.getCode(),UtilResponse.buildApiError(error.getCode()).getCode());assertNotNull(error.getDetail());assertNotNull(error.getMessage());}
  assertEquals("ERROR_SERVER",UtilResponse.buildApiError("unknown").getCode());assertEquals("detail",UtilResponse.buildApiError("ERROR_SERVER","detail").getDetail());assertNotNull(UtilResponse.buildApiError("ERROR_SERVER",(String)null));assertNotNull(UtilResponse.buildApiError("ERROR_SERVER",""));
  assertNotNull(UtilResponse.buildApiError((Exception)null,"code"));assertTrue(UtilResponse.buildApiError(new IllegalStateException("fail"),"code").getDetail().contains("fail"));
  assertNotNull(UtilResponse.buildApiError((RequestException)null,"code"));assertNotNull(UtilResponse.buildApiError(new RequestException("fail"),"code").getMessage());
 }
 @Test void aggregationFiltersHandleMissingAndPopulatedParameters(){
  assertNull(FilterDb.getLimitOperation(null));assertNull(FilterDb.getLimitOperation(Map.of()));assertNull(FilterDb.getLimitOperation(Map.of(ConstantsRest.COUNT,"bad")));assertNull(FilterDb.getLimitOperation(Map.of(ConstantsRest.COUNT,"0")));
  assertNotNull(FilterDb.getLimitOperation(Map.of(ConstantsRest.COUNT,"5")));assertNotNull(FilterDb.getSkipOperation(null));assertNotNull(FilterDb.getSkipOperation(Map.of()));assertNotNull(FilterDb.getSkipOperation(Map.of(ConstantsRest.SKIP,"-5")));assertNotNull(FilterDb.getSkipOperation(Map.of(ConstantsRest.SKIP,"10")));assertNotNull(FilterDb.getSkipOperation(Map.of(ConstantsRest.SKIP,"bad")));
  assertNotNull(FilterDb.getSortOperationField(null));assertNotNull(FilterDb.getSortOperationField(Map.of()));assertNotNull(FilterDb.getSortOperationField(Map.of(ConstantsRest.SORT,"name")));assertNotNull(FilterDb.getSortOperationField(Map.of(ConstantsRest.SORT,"name",ConstantsRest.TYPE_SORT,"DESC")));assertNotNull(FilterDb.getSortOperationField(Map.of(ConstantsRest.SORT," ")));
  for(Map<String,String> params:Arrays.asList(null,Map.<String,String>of(),Map.of("other","value"))){assertNull(FilterDb.getCriteriaLike(params));assertTrue(FilterDb.getCriteriaWhere(params).isEmpty());assertTrue(FilterDb.getCriteriaWhereNot(params).isEmpty());assertFalse(FilterDb.existsFilterDateUser(params));assertNotNull(FilterDb.getCriteriaFilter(params));}
  var params=Map.of(ConstantsRest.LIKE,"name:John:Jane,broken",ConstantsRest.WHERE,"status:active,broken",ConstantsRest.WHERE_NOT,"deleted:true,broken",ConstantsRest.FROM,"0",ConstantsRest.TO,"100000",ConstantsRest.COUNT,"5");
  assertTrue(FilterDb.existsFilterDateUser(params));assertEquals(2,FilterDb.getCriteriaBetweenDate(params,"createdAt").size());assertFalse(FilterDb.getCriteriaFilter(params).getCriteriaObject().isEmpty());assertNotNull(FilterDb.getAggregationFilter(params));assertNotNull(FilterDb.getAggregationFilter(null));
  assertFalse(FilterDb.existsFilterDateUser(Map.of(ConstantsRest.FROM,"bad",ConstantsRest.TO,"2")));assertFalse(FilterDb.existsFilterDateUser(Map.of(ConstantsRest.FROM,"2")));assertFalse(FilterDb.existsFilterDateUser(Map.of(ConstantsRest.FROM,"2",ConstantsRest.TO,"")));
  assertNotNull(FilterDb.getCriteriaLike(Map.of(ConstantsRest.LIKE,"broken")));assertNotNull(FilterDb.getCriteriaWhere(Map.of(ConstantsRest.WHERE,"broken")));assertNotNull(FilterDb.getCriteriaWhereNot(Map.of(ConstantsRest.WHERE_NOT,"broken")));
 }
}
