package cz.quantumleap.core.view;

import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.data.transport.SliceRequest;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LookupController {

    EntityIdentifier<?> getLookupEntityIdentifier();

    String getDetailUrl();

    String getLookupLabelUrl();

    String getLookupLabelsUrl();

    String getLookupListUrl();

    String resolveLookupLabel(String id, HttpServletRequest request, HttpServletResponse response);

    String findLookupLabels(String query, Model model, HttpServletRequest request, HttpServletResponse response);

    String lookupList(SliceRequest sliceRequest, Model model, HttpServletRequest request, HttpServletResponse response);
}
