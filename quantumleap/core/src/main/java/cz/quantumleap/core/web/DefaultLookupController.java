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
    private static final String TABLE_NAME_MODEL_ATTRIBUTE_NAME = "tableName";
    private static final String LOOKUP_LIST_VIEW = "admin/components/lookup-modal-table";

    private final String tableName;
    private final String supportedDatabaseTableNameWithSchema;
    private final String detailUrl;
    private final String lookupLabelUrl;
    private final String lookupLabelsUrl;
    private final String lookupListUrl;
    private final LookupService lookupService;

    public DefaultLookupController(String tableName, String supportedDatabaseTableNameWithSchema, String detailUrl, String lookupLabelUrl, String lookupLabelsUrl, String lookupListUrl, LookupService lookupService) {
        this.tableName = tableName;
        this.supportedDatabaseTableNameWithSchema = supportedDatabaseTableNameWithSchema;
        this.detailUrl = detailUrl;
        this.lookupLabelUrl = lookupLabelUrl;
        this.lookupLabelsUrl = lookupLabelsUrl;
        this.lookupListUrl = lookupListUrl;
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
    public String findLookupLabels(String filter, Model model, HttpServletRequest request) {
        model.addAttribute(DETAIL_URL_MODEL_ATTRIBUTE_NAME, detailUrl);
        Map<Object, String> lookupLabels = lookupService.findLookupLabels(filter);
        model.addAttribute(LOOKUP_LABELS_ATTRIBUTE_NAME, lookupLabels);

        return LOOKUP_LABELS_VIEW;
    }

    @Override
    public String lookupList(SliceRequest sliceRequest, Model model, HttpServletRequest request) {
        Slice slice = lookupService.findSlice(sliceRequest);
        model.addAttribute(TABLE_SLICE_MODEL_ATTRIBUTE_NAME, slice);
        model.addAttribute(TABLE_NAME_MODEL_ATTRIBUTE_NAME, tableName);
        model.addAttribute(DETAIL_URL_MODEL_ATTRIBUTE_NAME, detailUrl);

        return LOOKUP_LIST_VIEW;
    }
}
