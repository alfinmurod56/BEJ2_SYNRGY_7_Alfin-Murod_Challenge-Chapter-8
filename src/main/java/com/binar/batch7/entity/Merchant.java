package com.binar.batch7.entity;


import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.binar.batch7.entity.base.BaseDate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Entity
@AllArgsConstructor
@Table(name = "merchant")
@NoArgsConstructor
@Data
@Where(clause = "deleted_date is null")
public class Merchant extends BaseDate {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "merchant_name")
    private String name;

    @Column(name = "merchant_location")
    private String location;

    @Column(name = "open")
    private Boolean isOpen;
}
