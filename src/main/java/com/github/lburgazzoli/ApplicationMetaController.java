package com.github.lburgazzoli;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.ws.rs.QueryParam;

import org.apache.camel.CamelContext;
import org.apache.camel.component.extension.MetaDataExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationMetaController {
    @Autowired
    private CamelContext camelContext;

    @GetMapping("/table/{tableName}")
    public Object meta(@PathVariable("tableName") String tableName, @QueryParam("fields") String fields) {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("objectType", "table");
        parameters.put("objectName", tableName);

        if (Objects.nonNull(fields)) {
            parameters.put("object." + tableName + ".fields", fields);
        }

        return camelContext.getComponent("servicenow")
            .getExtension(MetaDataExtension.class)
            .map(e -> e.meta(parameters))
            .filter(Optional::isPresent)
            .map(o -> o.get().getPayload())
            .orElseThrow(IllegalArgumentException::new);
    }
}
