package cz.quantumleap.server.test_module;

import cz.quantumleap.server.common.ResourceManager;
import cz.quantumleap.server.test_module.repository.TestEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class TestModuleController {

    @Autowired
    private TestEntityRepository testEntityRepository;

    @Autowired
    private ResourceManager resourceManager;

    @ModelAttribute("currentTime")
    public LocalDateTime currentTime() {
        return LocalDateTime.now();
    }

    volatile int i = (int) Math.round(Math.random() * 10000);

    @RequestMapping(value = "/")
//    @Transactional
    public String index() {

        // TODO ResourceDatabasePopulator and others DatabasePopulator* classes...

//        TestEntity entity = new TestEntity();
//        entity.setId(1L);
////        entity.setComment("treti entita");
//        List<Integer> subList = new ArrayList<>();
//        subList.add(null);
//        subList.add(12);
//        subList.add(null);
////        entity.setArr(ImmutableList.of(6, 6, 6));
//        entity.setFlatArray(subList);
//        subList = new MyList<>();
//        subList.add(i++);
//        subList.add(null);
//        entity.setMultidimensionalArray(ImmutableList.of(ImmutableList.of(9, 9), ImmutableList.of(10, 10), subList)); // TODO Test null values randomly placed in lists...
//        entity.setDate(LocalDate.now());
//        entity.setTime(LocalTime.NOON);
//        entity.setDateTime(LocalDateTime.now());
//        entity.setJson(ImmutableMap.of("a", ImmutableMap.of("b", "c")));
//        testEntityRepository.save(entity);
//        entity.setId(2L);
//        testEntityRepository.save(entity);
//
//        TestEntity entity2 = testEntityRepository.findOne(2L);
//        entity2.setFlatArray(null);
//        entity2.setMultidimensionalArray(null);
//        entity2.setDate(null);
//        entity2.setTime(null);
//        entity2.setDateTime(null);
//        entity2.setJson(null);
//        testEntityRepository.save(entity2);
//
//        TestEntity entity3 = testEntityRepository.findOne(2L);
//
//        Map<String, Object> json = testEntityRepository.jsonTest();
//        int count = testEntityRepository.countTestEnties();
//        boolean b = testEntityRepository.bool();
//        List<Integer> rec = testEntityRepository.array();

        System.out.println("#################### resource ####################");
        Resource s = resourceManager.findFirstSpecificFromClasspathOrWorkingDir("db/scripts/*.sql");
        try {
            System.out.println("script: " + s.getURL().toString());
        } catch (IOException e) {
            System.out.println("Exception! " + e.getMessage());
        }
        System.out.println("--------------------------------------------------");
        List<ResourceManager.ModuleResource> scripts = resourceManager.findOnClasspath("db/scripts/*.sql");
        scripts.forEach(script -> {
            try {
                System.out.println("script: " + script.getResource().getURL().toString());
            } catch (IOException e) {
                System.out.println("Exception! " + e.getMessage());
            }
        });
        System.out.println("==================================================");

        return "test/home";
    }

    @RequestMapping(value = "/sitemap")
    public String sitemap() {
        return "test/sitemap";
    }
}
