package com.nasim.chat.auth_service.controller;

import com.nasim.chat.auth_service.service.AppUserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class InternalUserControllerValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final InternalUserController controller =
            new InternalUserController(mock(AppUserService.class));

    @Test
    void rejectsUserDetailsBatchLargerThanConfiguredLimit() throws NoSuchMethodException {
        List<Long> ids = Collections.nCopies(
                InternalUserController.MAX_USER_DETAILS_BATCH_SIZE + 1,
                1L
        );

        Set<ConstraintViolation<InternalUserController>> violations = validate(ids);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("at most " + InternalUserController.MAX_USER_DETAILS_BATCH_SIZE
                        + " user IDs may be requested");
    }

    @Test
    void acceptsUserDetailsBatchAtConfiguredLimit() throws NoSuchMethodException {
        List<Long> ids = Collections.nCopies(
                InternalUserController.MAX_USER_DETAILS_BATCH_SIZE,
                1L
        );

        assertThat(validate(ids)).isEmpty();
    }

    private Set<ConstraintViolation<InternalUserController>> validate(List<Long> ids)
            throws NoSuchMethodException {
        Method method = InternalUserController.class.getMethod("findUserDetails", List.class);
        return validator.forExecutables().validateParameters(controller, method, new Object[]{ids});
    }
}
