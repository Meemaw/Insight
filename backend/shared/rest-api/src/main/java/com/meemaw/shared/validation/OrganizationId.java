package com.meemaw.shared.validation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.meemaw.shared.auth.Organization;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NotBlank(message = "Organization ID is required")
@Size(
    min = Organization.ORG_ID_LENGTH,
    max = Organization.ORG_ID_LENGTH,
    message = "Organization ID must be " + Organization.ORG_ID_LENGTH + " characters long")
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = {})
public @interface OrganizationId {

  String message() default "Organization ID is invalid";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
