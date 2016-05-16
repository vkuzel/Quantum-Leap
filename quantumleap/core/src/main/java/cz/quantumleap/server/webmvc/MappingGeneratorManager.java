package cz.quantumleap.server.webmvc;

import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

public interface MappingGeneratorManager {

    RequestMappingGenerator getMappingGenerator(RequestMappingInfo typeLevelRequestMappingInfo);
}
