package cz.quantumleap.server.i18n.transport;

import java.util.HashMap;
import java.util.Locale;

abstract class Multilingual<T> extends HashMap<String, T> {

    public T getValue(Locale locale) {
        return this.get(locale.getLanguage());
    }
}
