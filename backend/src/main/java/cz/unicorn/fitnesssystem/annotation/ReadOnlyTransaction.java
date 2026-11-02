package cz.unicorn.fitnesssystem.annotation;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Transactional(readOnly = true)
public @interface ReadOnlyTransaction {
}
