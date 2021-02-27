package cz.quantumleap.core.view;

import cz.quantumleap.core.database.domain.SliceRequest;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

public interface ListController {

    String list(SliceRequest sliceRequest, Model model, HttpServletRequest request);

}
