package com.fa.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class BondSearchParams {
    private String  maturityDateFrom;
    private  Double couponRateMin;
}
