package ma.lsia.certis.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark controller methods that require the user to have an organization.
 * Methods annotated with this will throw an exception if the authenticated user
 * does not have an associated organization.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresOrganization {
}
