package sinnet.infra.adapters.fun;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@ConfigurationProperties("app.report1.function")
@Validated
record Report1FunctionProperties(
    @NotBlank String baseUrl,
    @NotBlank String zipPath) {
}
