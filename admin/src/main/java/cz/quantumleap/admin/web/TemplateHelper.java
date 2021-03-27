package cz.quantumleap.admin.web;

import cz.quantumleap.core.database.domain.Slice;
import cz.quantumleap.core.view.controllerargument.FetchParamsControllerArgumentResolver;

public class TemplateHelper {

    public static TemplateTable createTemplateTable(String qualifier, String entityIdentifier, Slice slice, String detailUrl) {
        return new TemplateTable(qualifier, entityIdentifier, slice, detailUrl);
    }

    public static class TemplateTable {

        private final String qualifier;
        private final String entityIdentifier;
        private final Slice slice;
        private final String detailUrl;

        public TemplateTable(String qualifier, String entityIdentifier, Slice slice, String detailUrl) {
            this.qualifier = qualifier;
            this.entityIdentifier = entityIdentifier;
            this.slice = slice;
            this.detailUrl = detailUrl;
        }

        public String getSortParamName() {
            return FetchParamsControllerArgumentResolver.qualifyParamName(qualifier, FetchParamsControllerArgumentResolver.SORT_PARAM_NAME);
        }

        public String getOffsetParamName() {
            return FetchParamsControllerArgumentResolver.qualifyParamName(qualifier, FetchParamsControllerArgumentResolver.OFFSET_PARAM_NAME);
        }

        public String getSizeParamName() {
            return FetchParamsControllerArgumentResolver.qualifyParamName(qualifier, FetchParamsControllerArgumentResolver.SIZE_PARAM_NAME);
        }
    }
}
