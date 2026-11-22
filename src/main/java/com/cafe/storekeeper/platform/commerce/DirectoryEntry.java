package com.cafe.storekeeper.platform.commerce;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;

@Document("business_directory")
@CompoundIndex(name="business_directory_kind",def="{'businessId':1,'kind':1,'name':1}")
public record DirectoryEntry(@Id String id,String businessId,String kind,String name,String email,
        String phone,String role,boolean active,long version) {}
