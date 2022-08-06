package io.intuitdemo.data.dto;

import io.intuitdemo.data.TransactionStatics;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionStatsDTO {
        private List<TransactionStatics> stats;
}
