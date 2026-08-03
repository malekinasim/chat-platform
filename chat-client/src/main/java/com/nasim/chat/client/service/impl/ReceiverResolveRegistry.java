package com.nasim.chat.client.service.impl;

import com.nasim.chat.client.service.MessageReceiverService;
import com.nasim.chat.client.service.ReceiverResolver;
import com.nasim.chat.model.dto.DeliveryType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ReceiverResolveRegistry {

    private final Map<DeliveryType, ReceiverResolver> services;

    public ReceiverResolveRegistry(
            List<ReceiverResolver> services
    ) {
        this.services = services.stream()
                .collect(Collectors.toUnmodifiableMap(
                        ReceiverResolver::supportedType,
                        Function.identity()
                ));
    }

    public ReceiverResolver get(DeliveryType type) {
        ReceiverResolver service = services.get(type);

        if (service == null) {
            throw new IllegalArgumentException(
                    "Unsupported destination type: " + type
            );
        }

        return service;
    }
}