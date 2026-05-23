package com.debuggeandoideas.erp_lite.domain.ports.messages;

import com.debuggeandoideas.erp_lite.domain.common.DomainEvent;

public interface EventPublisherPort {

    void publish(DomainEvent event);
}
