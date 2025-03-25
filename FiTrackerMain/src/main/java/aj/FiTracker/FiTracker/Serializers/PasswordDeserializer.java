package aj.FiTracker.FiTracker.Serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class PasswordDeserializer extends JsonDeserializer<char[]> {

    @Override
    public char[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        String rawPassword = node.asText();
        System.out.println("PASS STARIUNG " + rawPassword);
        char[] passwordChars = rawPassword.toCharArray();
        System.out.println("PAAASSS CHARS " + passwordChars);
        rawPassword = "";
        rawPassword = null;
        return passwordChars;
    }
}
