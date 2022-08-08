package io.intuitdemo.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.BaseStream;

import static java.lang.Integer.parseInt;
import static java.nio.file.Files.lines;
import static java.util.Arrays.asList;

@Service
public class MapCodeToCountryNameService {


    private static final String COUNTRIES_CSV = "countries.csv";
    private static final String SPLITERATOR = ",";
    private Map<Integer, String> countriesNumberToName;

    @PostConstruct
    public void initialize() {
        countriesNumberToName = new HashMap<>();
        Flux.using(() -> lines(Path.of(Objects.requireNonNull(getClass().getClassLoader().getResource(COUNTRIES_CSV)).toURI())),
                        Flux::fromStream,
                        BaseStream::close)
                .subscribeOn(Schedulers.parallel())
                .subscribe(this::insertStringToMap);
    }

    private void insertStringToMap(String line) {
        List<String> countryList = asList(line.split(SPLITERATOR));
        countriesNumberToName.put(parseInt(countryList.get(2)), countryList.get(1));
    }

    public String getNameFromCode(int code) {
        return countriesNumberToName.get(code);
    }
}
