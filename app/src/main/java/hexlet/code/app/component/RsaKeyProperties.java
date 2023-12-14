package hexlet.code.app.component;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Getter
@Component
@ConfigurationProperties(prefix = "rsa")
public class RsaKeyProperties {
    private final RSAPublicKey rsaPublicKey;
    private final RSAPrivateKey rsaPrivateKey;

    @ConstructorBinding
    public RsaKeyProperties(
            @Value(value = "${rsa.public-key}") String publicKey,
            @Value(value = "${rsa.private-key}") String privateKey
    ) throws NoSuchAlgorithmException, InvalidKeySpecException {
        var keyFactory = KeyFactory.getInstance("RSA");

        var publicKeyEncoded = Base64.getDecoder().decode(publicKey);
        var privateKeyEncoded = Base64.getDecoder().decode(privateKey);

        var publicKeySpec = new X509EncodedKeySpec(publicKeyEncoded);
        var privateKeySpec = new PKCS8EncodedKeySpec(privateKeyEncoded);

        rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
        rsaPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
    }
}
