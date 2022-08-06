package io.intuitdemo.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatics {

    private String country;
    private double passPercentage;
    private double reviewPercentage;
    private long totalEventCount;

}
