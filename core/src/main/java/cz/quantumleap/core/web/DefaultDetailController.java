package cz.quantumleap.core.web;

import cz.quantumleap.core.business.DetailService;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
        T detail = id != null ? detailService.get(id) : createDetail();
        model.addAttribute(detail);
        return detailView;
    }

    private T createDetail() {
        try {
            return transportType.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String save(T transport, Errors errors) {
        if (errors.hasErrors()) {
            return detailView;
        }
        T saved = detailService.save(transport, errors);
        if (errors.hasErrors()) {
            return detailView;
        }
        return "redirect:" + detailUrl + "/" + getDetailId(saved);
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
