package com.nasim.chat.auth_service.model;

import java.util.List;

public record InternalUser(String id, List<String> roles) {

}
