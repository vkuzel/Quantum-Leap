package cz.quantumleap.core.web;

import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

public interface LookupController {

    String supportedDatabaseTableNameWithSchema();

    String getDetailUrl();

    String getLookupLabelsUrl();

    String findLookupLabels(String filter, Model model, HttpServletRequest request);
}
