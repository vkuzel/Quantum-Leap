package cz.quantumleap.core.web;

import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public interface DetailController<T> {

    String show(Object id, Model model);

    String save(T detail, Errors errors, Model model, RedirectAttributes redirectAttributes);
}
