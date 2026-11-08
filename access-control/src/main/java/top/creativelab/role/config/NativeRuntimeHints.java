package top.creativelab.role.config;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

/**
 * Reflection hints required by Hibernate when running as a GraalVM native image.
 *
 * <p>Hibernate's multi-id entity loader instantiates {@code java.util.UUID[]}
 * reflectively. Without this hint the native image aborts during startup with
 * {@code MissingReflectionRegistrationError}.</p>
 */
public class NativeRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerType(TypeReference.of("java.util.UUID[]"));
    }
}
