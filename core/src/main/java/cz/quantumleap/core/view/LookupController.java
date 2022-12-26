package cz.quantumleap.core.view;

import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.database.entity.EntityIdentifier;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;

public interface LookupController {

    EntityIdentifier<?> getLookupEntityIdentifier();

    String getDetailUrl();

    String getLookupLabelUrl();

    String getLookupLabelsUrl();

    String getLookupListUrl();

    String resolveLookupLabel(String id, HttpServletRequest request, HttpServletResponse response);

    String findLookupLabels(String query, Model model, HttpServletRequest request, HttpServletResponse response);

    String lookupList(FetchParams fetchParams, Model model, HttpServletRequest request, HttpServletResponse response);
}
