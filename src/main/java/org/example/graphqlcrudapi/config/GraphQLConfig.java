package org.example.graphqlcrudapi.config;

import graphql.scalars.ExtendedScalars;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(customDateTimeScalar())
                .scalar(ExtendedScalars.GraphQLLong);
    }

    @Bean
    public GraphQLScalarType customDateTimeScalar() {
        return GraphQLScalarType.newScalar()
                .name("DateTime")
                .description("Custom LocalDateTime scalar")
                .coercing(new Coercing<LocalDateTime, String>() {
                    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

                    @Override
                    public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
                        if (dataFetcherResult instanceof LocalDateTime) {
                            return ((LocalDateTime) dataFetcherResult).format(formatter);
                        }
                        throw new CoercingSerializeException("Expected LocalDateTime but was: " +
                                (dataFetcherResult != null ? dataFetcherResult.getClass().getSimpleName() : "null"));
                    }

                    @Override
                    public LocalDateTime parseValue(Object input) throws CoercingParseValueException {
                        if (input instanceof String) {
                            try {
                                return LocalDateTime.parse((String) input, formatter);
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseValueException("Invalid DateTime format. Expected: yyyy-MM-dd'T'HH:mm:ss, but was: " + input, e);
                            }
                        }
                        throw new CoercingParseValueException("Expected String but was: " +
                                (input != null ? input.getClass().getSimpleName() : "null"));
                    }

                    @Override
                    public LocalDateTime parseLiteral(Object input) throws CoercingParseLiteralException {
                        if (input instanceof String) {
                            try {
                                return LocalDateTime.parse((String) input, formatter);
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseLiteralException("Invalid DateTime format. Expected: yyyy-MM-dd'T'HH:mm:ss, but was: " + input, e);
                            }
                        }
                        throw new CoercingParseLiteralException("Expected String but was: " +
                                (input != null ? input.getClass().getSimpleName() : "null"));
                    }
                })
                .build();
    }
}