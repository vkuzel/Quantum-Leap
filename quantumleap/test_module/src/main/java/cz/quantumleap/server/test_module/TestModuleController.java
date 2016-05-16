package cz.quantumleap.server.test_module;

import cz.quantumleap.server.i18n.TranslationService;
import cz.quantumleap.server.test_module.domain.TestEntity;
import cz.quantumleap.server.test_module.repository.TestEntityRepository;
import cz.quantumleap.server.webmvc.MappingGeneratorManager;
import cz.quantumleap.server.webmvc.RequestMappingGenerator;
import cz.quantumleap.server.webmvc.RequestMappingOnDemand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TestModuleController {

    // TODO Kick this method out because it infers with RequestMappings...
    @ModelAttribute("currentTime")
    public LocalDateTime currentTime() {
        return LocalDateTime.now();
    }

    @Autowired
    private TestEntityRepository testEntityRepository;

    volatile int i = (int) Math.round(Math.random() * 10000);

    @RequestMapping("/")
    public String index() {
        return "test/home";
    }

    @RequestMappingOnDemand(TranslatedPathsMappingGeneratorManager.class)
    public String translated(Model model) {
        List<TestEntity> testEntities = testEntityRepository.findAll();
        model.addAttribute("testEntities", Collections.emptyList());
        return "test/translated";
    }

    @RequestMapping("/sitemap")
    public String sitemap() {
        return "test/sitemap";
    }

    @Component
    public static class TranslatedPathsMappingGeneratorManager implements MappingGeneratorManager {

        @Autowired
        private TranslationService translationService;

        @Override
        public RequestMappingGenerator getMappingGenerator(RequestMappingInfo typeLevelRequestMappingInfo) {
            return new RequestMappingGenerator(typeLevelRequestMappingInfo) {
                @Override
                public List<RequestMappingInfo> generate() {
                    return translationService.getLanguages().stream()
                            .map(language -> {
                                // TODO Custom translate method for path-translations.
                                String path = translationService.translate(language, "/translated");
                                return RequestMappingInfo.paths(path).build();
                            }).collect(Collectors.toList());
                }
            };
        }
    }
}
