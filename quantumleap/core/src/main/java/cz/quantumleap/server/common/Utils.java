package cz.quantumleap.server.common;

import com.google.common.io.CharStreams;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;

public class Utils {

    public static String loadResourceToString(Resource resource) {
        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {
            return CharStreams.toString(reader);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
