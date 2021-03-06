package cz.quantumleap.core.view;

import cz.quantumleap.core.business.ListService;
import cz.quantumleap.core.common.Utils;
import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.database.domain.Slice;
import cz.quantumleap.core.database.entity.EntityIdentifier;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

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
        EntityIdentifier<?> identifier = listService.getListEntityIdentifier(null);
        Slice slice = listService.findSlice(fetchParams);

        model.addAttribute(SLICE_MODEL_ATTRIBUTE_NAME, slice);
        model.addAttribute(ENTITY_IDENTIFIER_MODEL_ATTRIBUTE_NAME, identifier.toString());
        model.addAttribute(DETAIL_URL_MODEL_ATTRIBUTE_NAME, detailUrl);

        return Utils.isAjaxRequest(request) ? ajaxListView : listView;
    }
}
