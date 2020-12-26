package cz.quantumleap.core.web;

import cz.quantumleap.core.business.DetailService;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;
import java.util.function.Supplier;

import static cz.quantumleap.core.common.ReflectionUtils.invokeClassMethod;

public final class DefaultDetailController<T> implements DetailController<T> {

    private final Class<T> transportType;
    private final DetailService<T> detailService;
    private final Supplier<T> detailSupplier;
    private final String detailUrl;
    private final String detailView;

    public DefaultDetailController(Class<T> transportType, DetailService<T> detailService, String detailUrl, String detailView) {
        this(transportType, detailService, null, detailUrl, detailView);
    }

    public DefaultDetailController(Class<T> transportType, DetailService<T> detailService, Supplier<T> detailSupplier, String detailUrl, String detailView) {
        this.transportType = transportType;
        this.detailService = detailService;
        this.detailSupplier = detailSupplier;
        this.detailUrl = detailUrl;
        this.detailView = detailView;
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
        Class<?> type = transport.getClass();
        return invokeClassMethod(type, transport, "getId");
    }
}
