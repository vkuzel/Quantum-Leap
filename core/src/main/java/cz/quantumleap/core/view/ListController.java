package cz.quantumleap.core.view;

import cz.quantumleap.core.database.domain.FetchParams;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;

public interface ListController {

    String list(FetchParams fetchParams, Model model, HttpServletRequest request);

}
