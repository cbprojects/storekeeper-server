package com.cafe.storekeeper.infrastructure.domain.bill.rest.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.cafe.storekeeper.helper.constant.ConstantsMessage;
import com.cafe.storekeeper.helper.enums.EBillStatus;
import com.cafe.storekeeper.helper.enums.EBillType;
import com.cafe.storekeeper.helper.enums.EPaymentMethod;
import com.cafe.storekeeper.infrastructure.adapter.model.AbstractDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Valid
    @NotNull(message = ConstantsMessage.BILL_NO_STATUS_MESSAGE)
    @JsonProperty("status")
    private EBillStatus status;

    @JsonProperty("description")
    private String description;

    @Valid
    @NotBlank(message = ConstantsMessage.BILL_NO_CITY_MESSAGE)
    @JsonProperty("city")
    private String city;

    @Valid
    @NotBlank(message = ConstantsMessage.BILL_NO_ADDRESS_MESSAGE)
    @JsonProperty("address")
    private String address;

    @Valid
    @NotEmpty(message = ConstantsMessage.BILL_EMPTY_CONCEPTS_MESSAGE)
    @JsonProperty("concepts")
    private List<ConceptBillDTO> concepts;

    @JsonProperty("taxes")
    private List<TaxDTO> taxes;

    @Valid
    @NotNull(message = ConstantsMessage.BILL_NO_TYPE_MESSAGE)
    @JsonProperty("bill_type")
    private EBillType billType;

    @Valid
    @NotNull(message = ConstantsMessage.BILL_NO_PAYMENT_METHOD_MESSAGE)
    @JsonProperty("payment_method")
    private EPaymentMethod paymentMethod;

    @Valid
    @NotNull(message = ConstantsMessage.BILL_NO_DATE_MESSAGE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonProperty("bill_date")
    private LocalDateTime billDate;

    @Valid
    @NotNull(message = ConstantsMessage.BILL_NO_EXPIRY_DATE_MESSAGE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonProperty("expiry_date")
    private LocalDateTime expiryDate;

    // TODO: Cuando se haga la parte de login y creación de usuarios y empresas
    // @Valid
    // @NotNull(message = ConstantsMessage.BILL_NO_COMPANY_MESSAGE)
    @JsonProperty("company")
    private CompanyBillDTO company;

    @Valid
    @NotNull(message = ConstantsMessage.BILL_NO_CLIENT_MESSAGE)
    @JsonProperty("client")
    private PersonBillDTO client;

    @JsonIgnore
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
