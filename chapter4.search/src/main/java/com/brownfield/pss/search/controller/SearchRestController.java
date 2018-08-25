package com.brownfield.pss.search.controller;

import com.brownfield.pss.search.component.SearchComponent;
import com.brownfield.pss.search.entity.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RefreshScope
@CrossOrigin
@RestController
@RequestMapping("/search")
class SearchRestController {

    @Value("${originairports.shutdown}")
    private String originShutdownAirports;
    private SearchComponent searchComponent;

    @Autowired
    public SearchRestController(SearchComponent searchComponent) {
        this.searchComponent = searchComponent;
    }

    @RequestMapping(value = "/get", method = RequestMethod.POST)
    List<Flight> search(@RequestBody SearchQuery query) {
        System.out.println("Input : " + query);
        if (Arrays.asList(originShutdownAirports.split(",")).contains(query.getOrigin())) {
            System.out.println("The origin airport is in shutdown state.");
            return new ArrayList<>();
        }
        return searchComponent.search(query);
    }

}
