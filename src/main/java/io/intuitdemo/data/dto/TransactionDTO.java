package io.intuitdemo.data.dto;


import io.intuitdemo.data.Product;
import io.intuitdemo.data.Severity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "transaction")
public class TransactionDTO {

    @Id
    public String id;

    private double latitude;
    private double longitude;
    private boolean pass;
    private boolean review;
    private Product product;
    private Severity severity;

    @Indexed(name = "transactionEpochSeconds_index")
    private long transactionEpochSeconds;


    @Indexed(name = "country_index")
    private String country;
}
