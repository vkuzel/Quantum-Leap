package cz.quantumleap.core.view;

import cz.quantumleap.core.business.LookupService;
import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.database.domain.TableSlice;
import cz.quantumleap.core.database.entity.EntityIdentifier;
import cz.quantumleap.core.security.WebSecurityExpressionEvaluator;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public final class DefaultLookupController implements LookupController {

    private static final String DETAIL_URL_MODEL_ATTRIBUTE_NAME = "detailUrl";
    private static final String LOOKUP_LABELS_ATTRIBUTE_NAME = "lookupLabels";
    private static final String LOOKUP_LABELS_VIEW = "admin/components/lookup-labels";

    private static final String TABLE_SLICE_MODEL_ATTRIBUTE_NAME = "tableSlice";
    private static final String LIST_ENTITY_IDENTIFIER_MODEL_ATTRIBUTE_NAME = "entityIdentifier";
    private static final String LOOKUP_LIST_VIEW = "admin/components/lookup-modal-table";

    private final WebSecurityExpressionEvaluator webSecurityExpressionEvaluator;
    private final LookupService lookupService;
    private final String detailUrl;
    private final String lookupLabelUrl;
    private final String lookupLabelsUrl;
    private final String lookupListUrl;

    public DefaultLookupController(WebSecurityExpressionEvaluator webSecurityExpressionEvaluator, LookupService lookupService, String detailUrl, String lookupLabelUrl, String lookupLabelsUrl, String lookupListUrl) {
        this.webSecurityExpressionEvaluator = webSecurityExpressionEvaluator;
        this.lookupService = lookupService;
        this.detailUrl = detailUrl;
        this.lookupLabelsUrl = lookupLabelsUrl;
        this.lookupListUrl = lookupListUrl;
        this.lookupLabelUrl = lookupLabelUrl;
    }

    @Override
    public EntityIdentifier<?> getLookupEntityIdentifier() {
        return lookupService.getLookupEntityIdentifier(null);
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

    @ResponseBody
    @Override
    public String resolveLookupLabel(String id, HttpServletRequest request, HttpServletResponse response) {
        checkPermission(request, response);
        return lookupService.findLookupLabel(id);
    }

    @Override
    public String findLookupLabels(String query, Model model, HttpServletRequest request, HttpServletResponse response) {
        checkPermission(request, response);

        model.addAttribute(DETAIL_URL_MODEL_ATTRIBUTE_NAME, detailUrl);
        Map<Object, String> lookupLabels = lookupService.findLookupLabels(query);
        model.addAttribute(LOOKUP_LABELS_ATTRIBUTE_NAME, lookupLabels);

        return LOOKUP_LABELS_VIEW;
    }

    @Override
    public String lookupList(FetchParams fetchParams, Model model, HttpServletRequest request, HttpServletResponse response) {
        checkPermission(request, response);

        EntityIdentifier<?> identifier = lookupService.getListEntityIdentifier(null);
        TableSlice slice = lookupService.findSlice(fetchParams);

        model.addAttribute(TABLE_SLICE_MODEL_ATTRIBUTE_NAME, slice);
        model.addAttribute(LIST_ENTITY_IDENTIFIER_MODEL_ATTRIBUTE_NAME, identifier.toString());
        model.addAttribute(DETAIL_URL_MODEL_ATTRIBUTE_NAME, detailUrl);

        return LOOKUP_LIST_VIEW;
    }

    private void checkPermission(HttpServletRequest request, HttpServletResponse response) {
        String expression = "hasRole('ADMIN')";
        if (!webSecurityExpressionEvaluator.evaluate(expression, request, response)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
}
