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




[1]: https://javaee.github.io/hk2/extensibility.html#interception
[2]: https://stackoverflow.com/q/50290398/2587435