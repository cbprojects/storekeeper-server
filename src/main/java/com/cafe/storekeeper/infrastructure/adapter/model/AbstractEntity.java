package com.cafe.storekeeper.infrastructure.adapter.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AbstractEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("create_user")
    private String createUser;

    @Field("update_user")
    private String updateUser;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Field("create_date")
    private LocalDateTime createDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Field("update_date")
    private LocalDateTime updateDate;
}
