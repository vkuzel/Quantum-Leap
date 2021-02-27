package cz.quantumleap.core.view;

import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.function.Function;

public interface DetailController<T> {

    String show(Object id, Model model);

    String show(Object id, Model model, Function<T, String> viewFunction);

    String save(T detail, Errors errors, Model model, RedirectAttributes redirectAttributes);
}
