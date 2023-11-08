package com.cafe.storekeeper.infrastructure.domain.bill.rest.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;

import com.cafe.storekeeper.helper.enums.EBillStatus;
import com.cafe.storekeeper.helper.enums.EBillType;
import com.cafe.storekeeper.helper.enums.EPaymentMethod;
import com.cafe.storekeeper.infrastructure.adapter.model.AbstractDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillDTO extends AbstractDTO {
    private static final long serialVersionUID = 1L;

    @JsonProperty("bill_id")
    private long billId;

    @JsonProperty("status")
    private EBillStatus status;

    @JsonProperty("description")
    private String description;

    @JsonProperty("city")
    private String city;

    @JsonProperty("address")
    private String address;

    @JsonProperty("concepts")
    private List<ConceptBillDTO> concepts;

    @JsonProperty("taxes")
    private List<TaxDTO> taxes;

    @JsonProperty("bill_type")
    private EBillType billType;

    @JsonProperty("payment_method")
    private EPaymentMethod paymentMethod;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonProperty("bill_date")
    private LocalDateTime billDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonProperty("expiry_date")
    private LocalDateTime expiryDate;

    @JsonProperty("company")
    private CompanyBillDTO company;

    @JsonProperty("provider")
    private PersonDTO provider;

    @JsonProperty("client")
    private PersonDTO client;

    public BigDecimal getTotalTaxes() {
        BigDecimal totalTaxes = BigDecimal.valueOf(0);

        if (this.taxes != null && !this.taxes.isEmpty()) {
            totalTaxes = this.taxes.stream()
                    .map(TaxDTO::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        return totalTaxes;
    }

}
