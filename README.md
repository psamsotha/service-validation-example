# Bean Validation at the Service Level with Jersey and HK2

(Inspired by Stack Overflow question [https://stackoverflow.com/q/50290398/2587435][2])

One limitation with the bean validation in Jersey is that it does not support
validation at the service level with service method parameters. For example

```java
public static class Service {

    public String getValue(@Valid Model model) {
        return model.value;
    }
}
```

What we would like is that when the `getValue()` method is called with an
invalid `Model` bean, a validation exception should be thrown.

What this project is is a POC to show how this could be done. It uses
[HK2 interception][1] framework to intercept methods and perform validation on
method parameters that are annotated with `@Valid`. To make it work, you need to
do three things:

1. Annotate the service with `@Validated`.
2. Make the service an HK2 service by binding it with the registry.
3. Register the `ValidationFeature` with your Jersey application.

A complete example can be seen in the integration test `ServiceValidationTest`.

>**Note:** If you look at implementation of the `ValidationMethodInterceptor`,
you will see that when there are constraint violations, I only throw 
an `IllegalArgumentException`. I thought about throwing `ConstraintViolationExceotion`,
but I don't think this is the best solution as the `ConstraintViolationException`
with Jersey is tied to client request beans and the constraint violations are
mapped to 400 Bad Request, meaning a client error. If we are talking
about the service level, it's possible that the error is not a client error,
but a problem internally. If you want you can just throw a `ConstraintViolationException`,
it's up to you.




[1]: https://javaee.github.io/hk2/extensibility.html#interception
[2]: https://stackoverflow.com/q/50290398/2587435