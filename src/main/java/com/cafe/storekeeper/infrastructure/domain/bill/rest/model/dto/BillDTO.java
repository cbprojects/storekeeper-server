package com.cafe.storekeeper.infrastructure.domain.bill.rest.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;

import com.cafe.storekeeper.helper.enumerated.EBillStatus;
import com.cafe.storekeeper.helper.enumerated.EBillType;
import com.cafe.storekeeper.helper.enumerated.EPaymentMethod;
import com.cafe.storekeeper.infrastructure.adapter.model.AbstractDTO;
import com.cafe.storekeeper.infrastructure.domain.bill.db.pojo.CompanyPojo;
import com.cafe.storekeeper.infrastructure.domain.bill.db.pojo.ConceptBillPojo;
import com.cafe.storekeeper.infrastructure.domain.bill.db.pojo.PersonPojo;
import com.cafe.storekeeper.infrastructure.domain.bill.db.pojo.TaxPojo;
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
    private List<ConceptBillPojo> concepts;

    @JsonProperty("taxes")
    private List<TaxPojo> taxes;

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
    private CompanyPojo company;

    @JsonProperty("provider")
    private PersonPojo provider;

    @JsonProperty("client")
    private PersonPojo client;

    public BigDecimal getTotalTaxes() {
        BigDecimal totalTaxes = BigDecimal.valueOf(0);

        if (this.taxes != null && !this.taxes.isEmpty()) {
            totalTaxes = this.taxes.stream()
                    .map(TaxPojo::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        return totalTaxes;
    }

}
