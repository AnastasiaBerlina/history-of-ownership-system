package com.anastasia.project.serializr;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.corda.core.contracts.ContractState;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class KafkaJSONDeserializer implements Deserializer {

    @Override
    public void close() {
    }

    @Override
    public void configure(Map configs, boolean isKey) {

    }

    @Override
    public ContractState deserialize(String arg0, byte[] arg1) {
        ObjectMapper mapper = new ObjectMapper();
        ContractState user = null;
        try {
            user = mapper.readValue(arg1, ContractState.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
}
