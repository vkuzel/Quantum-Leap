package cz.quantumleap.core.web;

import cz.quantumleap.core.business.ListService;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

public final class DefaultListController implements ListController {

    private static final String TABLE_SLICE_MODEL_ATTRIBUTE_NAME = "tableSlice";
    private static final String TABLE_NAME_MODEL_ATTRIBUTE_NAME = "tableName";
    private static final String DETAIL_URL_MODEL_ATTRIBUTE_NAME = "detailUrl";
    // TODO Externalize Ajax support?
    private static final String AJAX_HEADER_NAME = "X-Requested-With";
    private static final String AJAX_HEADER_VALUE = "XMLHttpRequest";
    private static final String LIST_VIEW = "admin/components/table";

    private final String tableName;
    private final String listView;
    private final String detailUrl;
    private final ListService listService;

    public DefaultListController(String tableName, String listView, String detailUrl, ListService listService) {
        this.tableName = tableName;
        this.listView = listView;
        this.detailUrl = detailUrl;
        this.listService = listService;
    }

    @Override
    public String list(SliceRequest sliceRequest, Model model, HttpServletRequest request) {

        Slice slice = listService.findSlice(sliceRequest);
        model.addAttribute(TABLE_SLICE_MODEL_ATTRIBUTE_NAME, slice);
        model.addAttribute(TABLE_NAME_MODEL_ATTRIBUTE_NAME, tableName);
        model.addAttribute(DETAIL_URL_MODEL_ATTRIBUTE_NAME, detailUrl);

        if (AJAX_HEADER_VALUE.equals(request.getHeader(AJAX_HEADER_NAME))) {
            return LIST_VIEW;
        }

        return listView;
    }
}
