package sinnet.jwt;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import com.microsoft.azure.spring.autoconfigure.b2c.AADB2CURL;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import lombok.Data;
import lombok.SneakyThrows;

/**
 * B2C public key handler. Source:
 * https://stackoverflow.com/questions/52829246/azure-ad-b2c-java-application
 */
public class KeyUtilHandler {

    // @Value("${b2c.key-url}")
    private String keyUrl;

    // @Value("${b2c.sign-up-or-in-user-flow}")
    private String signUpOrInUserFlow;

    // @Value("${b2c.tenant}")
    private String tenant;

    private String eValue;
    private String nValue;
    private RSAKey jwk;

    /** Fixme.
     *
     * @return fixme
     */
    public RSAKey stringPublicKey() {

        setAzureKeys();

        var modulusBytes = Base64.getUrlDecoder().decode(nValue);
        var modulusInt = new BigInteger(1, modulusBytes);

        var exponentBytes = Base64.getUrlDecoder().decode(eValue);
        var exponentInt = new BigInteger(1, exponentBytes);

        KeyFactory keyFactory;

        var publicSpec = new RSAPublicKeySpec(modulusInt, exponentInt);

        String encodedStringKey = null;

            try {
                keyFactory = KeyFactory.getInstance("RSA");

                var publicKey = (RSAPublicKey) keyFactory.generatePublic(publicSpec);

                var encodedKey = publicKey.getEncoded();

                encodedStringKey = Base64.getEncoder().encodeToString(encodedKey);

            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }

        // return String.format("-----BEGIN PUBLIC KEY-----%s-----END PUBLIC KEY-----", encodedStringKey);
        // return encodedStringKey;
        return jwk;
    }

    @SneakyThrows
    private void setAzureKeys() {
        // var resolvedKeyUrl = AADB2CURL.getTokenUrl("f5230e02-babc-4b9d-ab7f-e76af49d1e5d", "B2C_1_sign-in-or-up")
        var resolvedKeyUrl = AADB2CURL.getJwkSetUrl("onlexnet", "B2C_1_sign-in-or-up");

        var azureKeys = new RestTemplate();
        AzureKeySetDto result = azureKeys.getForObject(resolvedKeyUrl, AzureKeySetDto.class);
        var s1 = azureKeys.getForObject(resolvedKeyUrl, String.class);
        var s = JWKSet.parse(s1);
        Assert.notNull(s, "?");

        if (Objects.isNull(result.getKeys())) {
            throw new UnsupportedOperationException();
        }
        var keyMap = result.getKeys().stream()
                .findFirst()
                .get();
        eValue = keyMap.getE();
        nValue = keyMap.getN();
        jwk = (RSAKey) s.getKeys().get(0);
    }

    private static final int TWENTY_HOURS_IN_MILIS = 20 * 3600 * 1000;
    @Scheduled(fixedRate = TWENTY_HOURS_IN_MILIS)
    private void renewKeys() {
            setAzureKeys();
    }
}

/** Fixme. */
@Data
class AzureKeyDto {
    private String kid;
    private int nbf;
    private String use;
    private String kty;
    private String e;
    private String n;
}

/** Fixme. */
@Data
class AzureKeySetDto {
    private List<AzureKeyDto> keys;
}
