package com.nasim.chat.auth_service.model.dto;

public record AuthenticationResolution(InternalUser internalUser,
                                       PendingRegistration pendingRegistration,
                                       AuthenticationStatus authenticationStatus) {

}
