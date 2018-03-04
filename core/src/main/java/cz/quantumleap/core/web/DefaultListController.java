package cz.quantumleap.core.web;

import cz.quantumleap.core.business.ListService;
import cz.quantumleap.core.common.Utils;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

public final class DefaultListController implements ListController {

    private static final String TABLE_SLICE_MODEL_ATTRIBUTE_NAME = "tableSlice";
    private static final String DATABASE_TABLE_NAME_WITH_SCHEMA_MODEL_ATTRIBUTE_NAME = "databaseTableNameWithSchema";
    private static final String DETAIL_URL_MODEL_ATTRIBUTE_NAME = "detailUrl";
    private static final String AJAX_LIST_VIEW = "admin/components/table";

    private final String databaseTableNameWithSchema;
    private final String listView;
    private final String detailUrl;
    private final ListService listService;

    public DefaultListController(String databaseTableNameWithSchema, String listView, String detailUrl, ListService listService) {
        this.databaseTableNameWithSchema = databaseTableNameWithSchema;
        this.listView = listView;
        this.detailUrl = detailUrl;
        this.listService = listService;
    }

    @Override
    public String list(SliceRequest sliceRequest, Model model, HttpServletRequest request) {
        Slice slice = listService.findSlice(sliceRequest);
        model.addAttribute(TABLE_SLICE_MODEL_ATTRIBUTE_NAME, slice);
        model.addAttribute(DATABASE_TABLE_NAME_WITH_SCHEMA_MODEL_ATTRIBUTE_NAME, databaseTableNameWithSchema);
        model.addAttribute(DETAIL_URL_MODEL_ATTRIBUTE_NAME, detailUrl);

        return Utils.isAjaxRequest(request) ? AJAX_LIST_VIEW : listView;
    }
}
