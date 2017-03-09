package cz.quantumleap.core.web;

import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

public interface LookupController {

    boolean supportsTable(String databaseTableNameWithSchema);

    String getDetailUrl();

    String getLookupLabelsUrl();

    String findLookupLabels(String filter, Model model, HttpServletRequest request);
}
