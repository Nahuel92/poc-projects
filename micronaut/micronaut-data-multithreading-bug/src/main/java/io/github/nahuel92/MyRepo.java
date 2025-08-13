package io.github.nahuel92;

import io.micronaut.aop.Around;
import io.micronaut.aop.InterceptorBean;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Log
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MyRepo extends CrudRepository<MyEntity, Integer> {
}

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Around
@interface Log {
}

@Singleton
@InterceptorBean(Log.class)
class LogInterceptor implements MethodInterceptor<Object, Object> {
    private static final Logger log = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public Object intercept(final MethodInvocationContext<Object, Object> context) {
        log.info("executing operation...");
        final var result = context.proceed();
        log.info("returning result (this shouldn't be logged when Datasource is set as read-only)");
        return result;
    }
}