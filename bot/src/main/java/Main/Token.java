package Main;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Token {
    private String token;
    public Token() throws IOException {
        token = Files.readString(Paths.get("src/main/token.txt"), StandardCharsets.UTF_8);
    }

    public String getToken() {
        return token;
    }


}