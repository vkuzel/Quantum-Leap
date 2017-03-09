package cz.quantumleap.core.web;

import cz.quantumleap.core.business.LookupService;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public final class DefaultLookupController implements LookupController {

    private static final String LOOKUP_LABELS_ATTRIBUTE_NAME = "lookupLabels";
    private static final String AJAX_HEADER_NAME = "X-Requested-With";
    private static final String AJAX_HEADER_VALUE = "XMLHttpRequest";
    private static final String LOOKUP_LABELS_VIEW = "admin/components/lookup-labels";

    private final String supportedDatabaseTableNameWithSchema;
    private final String detailUrl;
    private final String lookupLabelsUrl;
    private final String lookupLabelsView;
    private final LookupService lookupService;

    public DefaultLookupController(String supportedDatabaseTableNameWithSchema, String detailUrl, String lookupLabelsUrl, String lookupLabelsView, LookupService lookupService) {
        this.supportedDatabaseTableNameWithSchema = supportedDatabaseTableNameWithSchema;
        this.detailUrl = detailUrl;
        this.lookupLabelsUrl = lookupLabelsUrl;
        this.lookupLabelsView = lookupLabelsView;
        this.lookupService = lookupService;
    }

    @Override
    public boolean supportsTable(String databaseTableNameWithSchema) {
        return supportedDatabaseTableNameWithSchema.equals(databaseTableNameWithSchema);
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

        Map<Object, String> lookupLabels = lookupService.findLookupLabels(filter);
        model.addAttribute(LOOKUP_LABELS_ATTRIBUTE_NAME, lookupLabels);

        if (AJAX_HEADER_VALUE.equals(request.getHeader(AJAX_HEADER_NAME))) {
            return LOOKUP_LABELS_VIEW;
        }

        return lookupLabelsView;
    }
}
