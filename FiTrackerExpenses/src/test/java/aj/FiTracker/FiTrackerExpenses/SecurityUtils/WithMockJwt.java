package aj.FiTracker.FiTrackerExpenses.SecurityUtils;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockJwtSecurityContextFactory.class)
public @interface WithMockJwt {
    String name() default "Test User";

    long sub() default 1;

    String[] authorities() default {};

    String[] scopes() default {};
}
