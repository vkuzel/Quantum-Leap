package cz.quantumleap.core.web;

import cz.quantumleap.core.data.transport.SliceRequest;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

public interface ListController {

    String list(SliceRequest sliceRequest, Model model, HttpServletRequest request);

}
