package cz.quantumleap.core.view;

import cz.quantumleap.core.database.domain.FetchParams;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

public interface ListController {

    String list(FetchParams fetchParams, Model model, HttpServletRequest request);

}
