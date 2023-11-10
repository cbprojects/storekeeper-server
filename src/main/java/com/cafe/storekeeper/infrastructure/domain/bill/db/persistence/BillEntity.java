package com.cafe.storekeeper.infrastructure.domain.bill.db.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import com.cafe.storekeeper.helper.constant.ConstantsTableNames;
import com.cafe.storekeeper.helper.enums.EBillStatus;
import com.cafe.storekeeper.helper.enums.EBillType;
import com.cafe.storekeeper.helper.enums.EPaymentMethod;
import com.cafe.storekeeper.infrastructure.adapter.model.AbstractEntity;
import com.cafe.storekeeper.infrastructure.domain.bill.db.pojo.CompanyBillPojo;
import com.cafe.storekeeper.infrastructure.domain.bill.db.pojo.ConceptBillPojo;
import com.cafe.storekeeper.infrastructure.domain.bill.db.pojo.PersonBillPojo;
import com.cafe.storekeeper.infrastructure.domain.bill.db.pojo.TaxPojo;

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
@Document(collection = ConstantsTableNames.BILLS)
public class BillEntity extends AbstractEntity {

    @Field("bill_id")
    @Indexed(unique = true)
    private long billId;

    @Field("status")
    private EBillStatus status;

    @Field("description")
    private String description;

    @Field("city")
    private String city;

    @Field("address")
    private String address;

    @Field("concepts")
    private List<ConceptBillPojo> concepts;

    @Field("taxes")
    private List<TaxPojo> taxes;

    @Field("bill_type")
    private EBillType billType;

    @Field("payment_method")
    private EPaymentMethod paymentMethod;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Field("bill_date")
    private LocalDateTime billDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Field("expiry_date")
    private LocalDateTime expiryDate;

    @Field("company")
    private CompanyBillPojo company;

    @Field("client")
    private PersonBillPojo client;

    @Transient
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
