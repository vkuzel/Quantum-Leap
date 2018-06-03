package cz.quantumleap.core.web;

import cz.quantumleap.core.business.LookupService;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public final class DefaultLookupController implements LookupController {

    private static final String DETAIL_URL_MODEL_ATTRIBUTE_NAME = "detailUrl";
    private static final String LOOKUP_LABELS_ATTRIBUTE_NAME = "lookupLabels";
    private static final String LOOKUP_LABELS_VIEW = "admin/components/lookup-labels";

    private static final String TABLE_SLICE_MODEL_ATTRIBUTE_NAME = "tableSlice";
    private static final String DATABASE_TABLE_NAME_WITH_SCHEMA_ATTRIBUTE_NAME = "databaseTableNameWithSchema";
    private static final String LOOKUP_LIST_VIEW = "admin/components/lookup-modal-table";

    private final String databaseTableNameWithSchema;
    private final String detailUrl;
    private final String lookupLabelUrl;
    private final String lookupLabelsUrl;
    private final String lookupListUrl;
    private final LookupService lookupService;

    public DefaultLookupController(String databaseTableNameWithSchema, String detailUrl, String lookupLabelUrl, String lookupLabelsUrl, String lookupListUrl, LookupService lookupService) {
        this.databaseTableNameWithSchema = databaseTableNameWithSchema;
        this.detailUrl = detailUrl;
        this.lookupLabelUrl = lookupLabelUrl;
        this.lookupLabelsUrl = lookupLabelsUrl;
        this.lookupListUrl = lookupListUrl;
        this.lookupService = lookupService;
    }

    @Override
    public String supportedDatabaseTableNameWithSchema() {
        return databaseTableNameWithSchema;
    }

    @Override
    public String getDetailUrl() {
        return detailUrl;
    }

    @Override
    public String getLookupLabelUrl() {
        return lookupLabelUrl;
    }

    @Override
    public String getLookupLabelsUrl() {
        return lookupLabelsUrl;
    }

    @Override
    public String getLookupListUrl() {
        return lookupListUrl;
    }

    @Override
    public String resolveLookupLabel(String id) {
        return lookupService.findLookupLabel(id);
    }

    @Override
    public String findLookupLabels(String query, Model model, HttpServletRequest request) {
        model.addAttribute(DETAIL_URL_MODEL_ATTRIBUTE_NAME, detailUrl);
        Map<Object, String> lookupLabels = lookupService.findLookupLabels(query);
        model.addAttribute(LOOKUP_LABELS_ATTRIBUTE_NAME, lookupLabels);

        return LOOKUP_LABELS_VIEW;
    }

    @Override
    public String lookupList(SliceRequest sliceRequest, Model model, HttpServletRequest request) {
        Slice slice = lookupService.findSlice(sliceRequest);
        model.addAttribute(TABLE_SLICE_MODEL_ATTRIBUTE_NAME, slice);
        model.addAttribute(DATABASE_TABLE_NAME_WITH_SCHEMA_ATTRIBUTE_NAME, databaseTableNameWithSchema);
        model.addAttribute(DETAIL_URL_MODEL_ATTRIBUTE_NAME, detailUrl);

        return LOOKUP_LIST_VIEW;
    }
}
