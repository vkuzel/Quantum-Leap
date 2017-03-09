package cz.quantumleap.core.web;

import cz.quantumleap.core.business.LookupService;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public final class DefaultLookupController implements LookupController {

    private static final String DETAIL_URL_MODEL_ATTRIBUTE_NAME = "detailUrl";
    private static final String LOOKUP_LABELS_ATTRIBUTE_NAME = "lookupLabels";
    private static final String LOOKUP_LABELS_VIEW = "admin/components/lookup-labels";

    private final String supportedDatabaseTableNameWithSchema;
    private final String detailUrl;
    private final String lookupLabelsUrl;
    private final LookupService lookupService;

    public DefaultLookupController(String supportedDatabaseTableNameWithSchema, String detailUrl, String lookupLabelsUrl, LookupService lookupService) {
        this.supportedDatabaseTableNameWithSchema = supportedDatabaseTableNameWithSchema;
        this.detailUrl = detailUrl;
        this.lookupLabelsUrl = lookupLabelsUrl;
        this.lookupService = lookupService;
    }

    @Override
    public String supportedDatabaseTableNameWithSchema() {
        return supportedDatabaseTableNameWithSchema;
    }

    @Override
    public String getDetailUrl() {
        return detailUrl;
    }

    @Override
    public String getLookupLabelsUrl() {
        return lookupLabelsUrl;
    }

    @Override
    public String findLookupLabels(String filter, Model model, HttpServletRequest request) {

        model.addAttribute(DETAIL_URL_MODEL_ATTRIBUTE_NAME, detailUrl);
        Map<Object, String> lookupLabels = lookupService.findLookupLabels(filter);
        model.addAttribute(LOOKUP_LABELS_ATTRIBUTE_NAME, lookupLabels);

        return LOOKUP_LABELS_VIEW;
    }
}
