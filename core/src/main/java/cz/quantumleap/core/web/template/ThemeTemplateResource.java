package cz.quantumleap.core.web.template;

import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.thymeleaf.templateresource.ITemplateResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class ThemeTemplateResource implements ITemplateResource {

    private final Resource resource;
    private final String characterEncoding;

    ThemeTemplateResource(Resource resource, String characterEncoding) {
        this.resource = resource;
        this.characterEncoding = characterEncoding;
    }

    @Override
    public String getDescription() {
        return this.resource.getDescription();
    }

    @Override
    public String getBaseName() {
        return this.resource.getFilename();
    }

    @Override
    public boolean exists() {
        return resource.exists();
    }

    @Override
    public Reader reader() throws IOException {
        InputStreamReader reader;
        if (StringUtils.isEmpty(characterEncoding)) {
            reader = new InputStreamReader(resource.getInputStream());
        } else {
            reader = new InputStreamReader(resource.getInputStream(), characterEncoding);
        }
        return new BufferedReader(reader);
    }

    @Override
    public ITemplateResource relative(String relativeLocation) {
        throw new UnsupportedOperationException();
    }
}
