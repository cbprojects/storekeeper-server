package com.cafe.storekeeper.platform.commerce;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.cafe.storekeeper.platform.business.*;

class CommerceHttpTest {
    @Test void moneyIsSerializedAsDecimalTextWithoutJavascriptPrecisionLoss() throws Exception {
        BusinessService businesses=mock(BusinessService.class);CommerceService commerce=mock(CommerceService.class);BusinessScope scope=mock(BusinessScope.class);
        when(businesses.authorize("owner","business")).thenReturn(scope);
        BigDecimal total=new BigDecimal("9007199254740993.01");
        when(commerce.sale(eq(scope),anyString(),any())).thenReturn(new CommerceModels.Sale("sale","business","MXN",List.of(),total,BigDecimal.ZERO,BigDecimal.ZERO,total,BigDecimal.ZERO,"CONFIRMED",0,Instant.now(),"hash"));
        var mvc=MockMvcBuilders.standaloneSetup(new CommerceController(businesses,commerce,mock(com.cafe.storekeeper.platform.identity.IdentityService.class))).build();
        mvc.perform(post("/api/v1/businesses/business/commerce/sales").principal(UsernamePasswordAuthenticationToken.unauthenticated("owner",null))
            .header("Idempotency-Key","abcdefghijklmnop").contentType("application/json")
            .content("{\"lines\":[{\"productId\":\"product\",\"quantity\":1}],\"taxPercent\":\"0\",\"discount\":\"0\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.total").value("9007199254740993.01"));
    }
}
