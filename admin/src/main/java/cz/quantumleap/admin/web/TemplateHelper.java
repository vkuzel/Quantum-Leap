package cz.quantumleap.admin.web;

import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.web.controllerargument.SliceRequestControllerArgumentResolver;

public class TemplateHelper {

    public static TemplateTable createTemplateTable(String qualifier, String databaseTableNameWithSchema, Slice tableSlice, String detailUrl) {
        return new TemplateTable(qualifier, databaseTableNameWithSchema, tableSlice, detailUrl);
    }

    public static class TemplateTable {

        private final String qualifier;
        private final String databaseTableNameWithSchema;
        private final Slice tableSlice;
        private final String detailUrl;

        public TemplateTable(String qualifier, String databaseTableNameWithSchema, Slice tableSlice, String detailUrl) {
            this.qualifier = qualifier;
            this.databaseTableNameWithSchema = databaseTableNameWithSchema;
            this.tableSlice = tableSlice;
            this.detailUrl = detailUrl;
        }

        public String getSortParamName() {
            return SliceRequestControllerArgumentResolver.qualifyParamName(qualifier, SliceRequestControllerArgumentResolver.SORT_PARAM_NAME);
        }

        public String getOffsetParamName() {
            return SliceRequestControllerArgumentResolver.qualifyParamName(qualifier, SliceRequestControllerArgumentResolver.OFFSET_PARAM_NAME);
        }

        public String getSizeParamName() {
            return SliceRequestControllerArgumentResolver.qualifyParamName(qualifier, SliceRequestControllerArgumentResolver.SIZE_PARAM_NAME);
        }
    }
}
