package cz.quantumleap.core.web;

import cz.quantumleap.core.business.DetailService;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Supplier;

public final class DefaultDetailController<T> implements DetailController<T> {

    private final Class<T> transportType;
    private final String detailUrl;
    private final String detailView;
    private final DetailService<T> detailService;
    private final Supplier<T> detailSupplier;

    public DefaultDetailController(Class<T> transportType, String detailUrl, String detailView, DetailService<T> detailService) {
        this(transportType, detailUrl, detailView, detailService, null);
    }

    public DefaultDetailController(Class<T> transportType, String detailUrl, String detailView, DetailService<T> detailService, Supplier<T> detailSupplier) {
        this.transportType = transportType;
        this.detailUrl = detailUrl;
        this.detailView = detailView;
        this.detailService = detailService;
        this.detailSupplier = detailSupplier;
    }

    @Override
    public String show(Object id, Model model) {
        return show(id, model, (detail) -> detailView);
    }

    @Override
    public String show(Object id, Model model, Function<T, String> viewFunction) {
        T detail;
        if (id != null) {
            detail = detailService.get(id);
        } else {
            detail = detailSupplier != null ? detailSupplier.get() : createDetail();
        }
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

    private Object getDetailId(T transport) {
        try {
            Method method = transport.getClass().getMethod("getId");
            return method.invoke(transport);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }
}
