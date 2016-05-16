package cz.quantumleap.server.webmvc;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class RequestMappingGenerator {

    private final RequestMappingInfo typeLevelRequestMapping;

    /**
     * @param typeLevelRequestMappingInfo @RequestMapping on @Controller class
     *                                    to be combined with generated list of
     *                                    RequestMappingInfo.
     */
    public RequestMappingGenerator(RequestMappingInfo typeLevelRequestMappingInfo) {
        this.typeLevelRequestMapping = typeLevelRequestMappingInfo;
    }

    protected abstract List<RequestMappingInfo> generate();

    /**
     * Method that returns (generates) list of request mapping information.
     * This method is the right place to implement some smart caching so it
     * won't re-generate mapping objects if it's not necessary.
     */
    private List<RequestMappingInfo> getRequestMappingInfoList() {
        if (typeLevelRequestMapping != null) {
            return generate().stream().map(info -> info.combine(typeLevelRequestMapping)).collect(Collectors.toList());
        } else {
            return generate();
        }
    }

    public Set<String> getMappingPathPatterns() {
        Set<String> pathPatterns = new HashSet<>();
        getRequestMappingInfoList().forEach(info -> pathPatterns.addAll(info.getPatternsCondition().getPatterns()));
        return pathPatterns;
    }

    public Set<MediaType> getProducibleMediaTypes() {
        Set<MediaType> mediaTypes = new HashSet<>();
        getRequestMappingInfoList().forEach(info -> mediaTypes.addAll(info.getProducesCondition().getProducibleMediaTypes()));
        return mediaTypes;
    }

    public RequestMappingGenerator getMatchingCondition(HttpServletRequest request) {
        List<RequestMappingInfo> mappingInfoList = getRequestMappingInfoList();
        List<RequestMappingInfo> matchingMappingInfoList = mappingInfoList.stream()
                .map(info -> info.getMatchingCondition(request)).filter(info -> info != null).collect(Collectors.toList());
        if (!matchingMappingInfoList.isEmpty()) {
            return new RequestMappingGenerator(typeLevelRequestMapping) {
                @Override
                protected List<RequestMappingInfo> generate() {
                    return matchingMappingInfoList;
                }
            };
        } else {
            return null;
        }
    }

    public int compareTo(RequestMappingGenerator other, HttpServletRequest request) {
        List<RequestMappingInfo> mappingInfoList1 = getRequestMappingInfoList();
        List<RequestMappingInfo> mappingInfoList2 = other.getRequestMappingInfoList();
        mappingInfoList1.sort((info1, info2) -> info1.compareTo(info2, request));
        mappingInfoList2.sort((info1, info2) -> info1.compareTo(info2, request));
        return mappingInfoList1.get(0).compareTo(mappingInfoList2.get(0), request);
    }

    @Override
    public String toString() {
        return "[" + getMappingPathPatterns().stream().collect(Collectors.joining(" || ")) + "] (Mappings can be changed during application's lifetime!)";
    }
}
