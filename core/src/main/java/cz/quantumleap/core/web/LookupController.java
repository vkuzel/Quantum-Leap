package cz.quantumleap.core.web;

import cz.quantumleap.core.data.transport.SliceRequest;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

public interface LookupController {

    String supportedDatabaseTableNameWithSchema();

    String getDetailUrl();

    String getLookupLabelUrl();

    String getLookupLabelsUrl();

    String getLookupListUrl();

    String resolveLookupLabel(String id);

    String findLookupLabels(String query, Model model, HttpServletRequest request);

    String lookupList(SliceRequest sliceRequest, Model model, HttpServletRequest request);
}
