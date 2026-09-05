package com.nasim.chat.auth_service.service;

import com.nasim.chat.auth_service.model.dto.GeneralUserDetails;
import com.nasim.chat.auth_service.model.entity.AppUser;
import com.nasim.chat.auth_service.model.entity.Role;
import com.nasim.chat.auth_service.repository.AppUserRepository;
import com.nasim.chat.auth_service.service.impl.AppUserServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppUserServiceImplUserDetailsTest {
    @Test
    void retrievesUserDetailsWithOneRepositoryBatchQuery() {
        AppUserRepository repository = mock(AppUserRepository.class);
        AppUserServiceImpl service = new AppUserServiceImpl(
                repository, mock(RoleService.class), mock(UserIdentityService.class));
        AppUser user = new AppUser();
        user.setId(2L);
        user.setDisplayName("alice");
        user.setAvatarUrl("avatar");
        user.setEmail("alice@example.com");
        user.setPhoneNumber("1234");
        Role role = new Role();
        role.setName("USER");
        user.setRoles(Set.of(role));
        when(repository.findAllByIdInAndActiveTrue(List.of(1L, 2L))).thenReturn(List.of(user));

        List<GeneralUserDetails> result = service.findUserDetails(List.of(1L, 2L));

        assertThat(result).containsExactly(new GeneralUserDetails(
                "2", "alice", "avatar", "alice@example.com", "1234", null, Set.of("USER")));
        verify(repository).findAllByIdInAndActiveTrue(List.of(1L, 2L));
    }
}
