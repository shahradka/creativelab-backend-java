package top.creativelab.user.adapters.inbound.graphql;

import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.UnExecutableSchemaGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThatCode;

class UserGraphQlSchemaTest {

    @Test
    void schemaIsValid() throws Exception {
        ClassPathResource resource = new ClassPathResource("graphql/schema.graphqls");
        TypeDefinitionRegistry registry;
        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            registry = new SchemaParser().parse(reader);
        }

        assertThatCode(() -> UnExecutableSchemaGenerator.makeUnExecutableSchema(registry))
                .doesNotThrowAnyException();
    }
}
