package cz.quantumleap.core.web;

import org.springframework.ui.Model;
import org.springframework.validation.Errors;

public interface DetailController<T> {

    String show(Object id, Model model);

    String save(T detail, Model model, Errors errors);
}
