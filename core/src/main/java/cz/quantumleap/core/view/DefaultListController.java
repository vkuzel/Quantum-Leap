package cz.quantumleap.core.view;

import cz.quantumleap.core.business.ListService;
import cz.quantumleap.core.utils.Utils;
import cz.quantumleap.core.database.domain.FetchParams;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;

public final class DefaultListController implements ListController {

    public static final String SLICE_MODEL_ATTRIBUTE_NAME = "slice";
    public static final String ENTITY_IDENTIFIER_MODEL_ATTRIBUTE_NAME = "entityIdentifier";
    public static final String DETAIL_URL_MODEL_ATTRIBUTE_NAME = "detailUrl";

    private final String listView;
    private final String ajaxListView;
    private final String detailUrl;
    private final ListService listService;

    public DefaultListController(ListService listService, String listView, String ajaxListView, String detailUrl) {
        this.listService = listService;
        this.listView = listView;
        this.ajaxListView = ajaxListView;
        this.detailUrl = detailUrl;
    }

    @Override
    public String list(FetchParams fetchParams, Model model, HttpServletRequest request) {
        var identifier = listService.getListEntityIdentifier(null);
        var slice = listService.findSlice(fetchParams);

        model.addAttribute(SLICE_MODEL_ATTRIBUTE_NAME, slice);
        model.addAttribute(ENTITY_IDENTIFIER_MODEL_ATTRIBUTE_NAME, identifier.toString());
        model.addAttribute(DETAIL_URL_MODEL_ATTRIBUTE_NAME, detailUrl);

        return Utils.isAjaxRequest(request) ? ajaxListView : listView;
    }
}
