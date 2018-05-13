package com.example;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Parameter;
import java.util.Set;


public class ValidationMethodInterceptor implements MethodInterceptor {

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        for (int i = 0; i < args.length; i++) {
            Parameter parameter = invocation.getMethod().getParameters()[i];
            if (parameter.getAnnotation(Valid.class) != null) {
                handleValidation(args[i]);
            }
        }
        return invocation.proceed();
    }

    private void handleValidation(Object arg) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(arg);

        if (!constraintViolations.isEmpty()) {
            throw new IllegalArgumentException("constraint violations in bean argument");
        }
    }
}
