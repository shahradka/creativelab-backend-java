package top.creativelab.user.domain.user.vo;

import lombok.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

@Value
public class Password {
    String hash;

    private Password(String hash){
        this.hash = hash;
    }

    public static Password fromHash(String hash){
        return new Password(hash);
    }

    public static Password fromRaw(String rawPassword, PasswordEncoder encoder){
        validate(rawPassword);
        String hashed = encoder.encode(rawPassword);
        return new Password(hashed);
    }

    private static void validate(String raw){
        if(raw == null  || !raw.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")){
            throw new IllegalStateException("Wick Password");

        }
    }

}
