package com.tpt.capstone_ecommerce.ecommerce.dto.response;
import com.tpt.capstone_ecommerce.ecommerce.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserProfileResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String cartId;
    private List<String> roles;

    public UserProfileResponse(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.roles = user.getRoles().stream().map(role -> role.getRole().name()).collect(Collectors.toList());
        this.cartId = user.getCart().getId();
    }
}
