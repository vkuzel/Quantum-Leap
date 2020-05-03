package cz.quantumleap.core.web;

import cz.quantumleap.core.business.DetailService;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

public final class DefaultDetailController<T> implements DetailController<T> {

    private final Class<T> transportType;
    private final String detailUrl;
    private final String detailView;
    private final DetailService<T> detailService;

    public DefaultDetailController(Class<T> transportType, String detailUrl, String detailView, DetailService<T> detailService) {
        this.transportType = transportType;
        this.detailUrl = detailUrl;
        this.detailView = detailView;
        this.detailService = detailService;
    }

    @Override
    public String show(Object id, Model model) {
        return show(id, model, (detail) -> detailView);
    }

    @Override
    public String show(Object id, Model model, Function<T, String> viewFunction) {
        T detail = id != null ? detailService.get(id) : createDetail();
        model.addAttribute(detail);
        return viewFunction.apply(detail);
    }

    private T createDetail() {
        try {
            return transportType.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String save(T transport, Errors errors, Model model, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            model.addAttribute(transport);
            return detailView;
        }
        T saved = detailService.save(transport, errors);
        if (errors.hasErrors()) {
            model.addAttribute(transport);
            return detailView;
        }
        redirectAttributes.addFlashAttribute("saved", true);
        return "redirect:" + detailUrl + '/' + getDetailId(saved);
    }

    // TODO This is "just for fun" solution!
    private Object getDetailId(T transport) {
        try {
            Method method = transport.getClass().getMethod("getId");
            return method.invoke(transport);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

}
