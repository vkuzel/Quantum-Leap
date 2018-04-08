var UrlUtils = {
    getQueryParam: function (url, name) {
        var regExp = new RegExp(name + "=([^&#]+)");
        var match = url.match(regExp);
        return match !== null ? match[1] : null;
    },
    replaceQueryParam: function (url, name, value) {
        var regExp = new RegExp(name + '=[^&#]+');
        return url.replace(regExp, name + '=' + value);
    },
    removeQueryParams: function (url, name) {
        for (var i = 1; i < arguments.length; i++) {
            var regExp = new RegExp(arguments[i] + '=[^&#]+&?');
            url = url.replace(regExp, '');
        }
        return url;
    }
};

var Loader = {
    show: function () {
        $('#loader').show();
    },
    hide: function () {
        $('#loader').hide();
    }
};

function MenuControl(menuSelector) {
    var $menu = $(menuSelector);
    var $activeAnchor = $menu.find('li > a.active');

    if ($activeAnchor.length > 0) {
        var href = $activeAnchor.attr('href');
        sessionStorage.setItem("lastActiveMenuItemHref", href);
    } else {
        var lastActiveMenuItemHref = sessionStorage.getItem("lastActiveMenuItemHref");
        if (lastActiveMenuItemHref) {
            $activeAnchor = $menu.find('a[href="' + lastActiveMenuItemHref + '"]');
            $activeAnchor.addClass('active');
            $activeAnchor.next('ul').addClass('show');
            $activeAnchor.parentsUntil(menuSelector, 'ul').addClass('show');
        }
    }

    if ($activeAnchor.length > 0) {
        $activeAnchor
            .parents('li.nav-item')
            .children('a.nav-link-collapse.collapsed')
            .removeClass('collapsed');
    }
}

MenuControl('#side-menu');

function TableControl(tableSelector, tBodyListenersBinder) {
    var $table = $(tableSelector);

    var tableControl = {
        $table: $table,
        qualifier: $table.attr('data-qualifier'),

        $tHead: $table.find('thead'),
        $tBody: $table.find('tbody'),
        $tFoot: $table.find('tfoot')
    };

    var qualifyParamName = function (qualifier, paramName) {
        return qualifier ? qualifier + '_' + paramName : paramName;
    };

    tableControl.bindListeners = function () {

        var $loadMoreButton = tableControl.$tFoot.find('.btn-load-more');
        $loadMoreButton.click(tableControl.fetchMore);

        if (tBodyListenersBinder) {
            tBodyListenersBinder(tableControl.$tBody);
        }

        var $sortButtons = tableControl.$tHead.find('a');
        $sortButtons.click(tableControl.sort);
    };

    tableControl.appendContent = function (table) {
        var $table = $(table);

        var $tHead = $table.find('thead');
        var $tBody = $table.find('tbody');
        var $tFoot = $table.find('tfoot');

        tableControl.$tHead.replaceWith($tHead);
        tableControl.$tHead = $tHead;
        tableControl.$tBody.append($tBody.html());
        tableControl.$tFoot.replaceWith($tFoot);
        tableControl.$tFoot = $tFoot;

        tableControl.bindListeners();
    };

    tableControl.replaceContent = function (table) {
        var $table = $(table);

        var $tHead = $table.find('thead');
        var $tBody = $table.find('tbody');
        var $tFoot = $table.find('tfoot');

        tableControl.$tHead.replaceWith($tHead);
        tableControl.$tHead = $tHead;
        tableControl.$tBody.replaceWith($tBody);
        tableControl.$tBody = $tBody;
        tableControl.$tFoot.replaceWith($tFoot);
        tableControl.$tFoot = $tFoot;

        tableControl.bindListeners();
    };

    tableControl.fetchMore = function () {
        var offset = tableControl.$tBody.find('tr').length;

        var url = UrlUtils.removeQueryParams(this.href, qualifyParamName(tableControl.qualifier, 'size'), qualifyParamName(tableControl.qualifier, 'offset'));

        $.get(url, {offset: offset}, tableControl.appendContent);

        return false;
    };

    tableControl.sort = function () {
        var size = tableControl.$tBody.find('tr').length;

        var url = UrlUtils.removeQueryParams(this.href, qualifyParamName(tableControl.qualifier, 'size'), qualifyParamName(tableControl.qualifier, 'offset'));

        $.get(url, {size: size}, tableControl.replaceContent);

        return false;
    };

    tableControl.bindListeners();
}

$('table.data-table').each(function (i, table) {
    var tBodyListenersBinder = function ($tBody) {
        $tBody.find('tr > td').click(function () {
            var $primaryKeyAnchors = $(this).parent().find('> td.primary-key > a');
            var $anchors = $(this).children('a');
            if ($anchors.length) {
                window.location = $anchors.first().attr('href');
            } else if ($primaryKeyAnchors.length) {
                window.location = $primaryKeyAnchors.first().attr('href');
            }
        });
    };

    TableControl(table, tBodyListenersBinder);
});

function LookupControl(lookupField) {
    var $lookupField = $(lookupField);

    var lookupControl = {
        $lookupField: $lookupField,

        $dataInput: $lookupField.find(':hidden'),
        $labelInput: $lookupField.find(':text')
    };

    lookupControl.setValues = function (id, label) {
        var json = JSON.stringify({id: id, label: label});
        lookupControl.$dataInput.val(json);
        lookupControl.$labelInput.val(label);
    };

    var dropDownControl = {
        labelsUrl: lookupControl.$labelInput.attr('data-lookup-labels-url'),
        $dropDown: $lookupField.find('div.dropdown-menu'),

        labelsRequestTimeout: null
    };

    dropDownControl.bindListeners = function () {
        lookupControl.$labelInput.keyup(dropDownControl.fetchLabels);
        lookupControl.$labelInput.blur(dropDownControl.fieldLostFocus);
    };

    dropDownControl.cancelRequestInProgress = function () {
        if (dropDownControl.labelsRequestTimeout) {
            clearTimeout(dropDownControl.labelsRequestTimeout);
            dropDownControl.labelsRequestTimeout = null;
        }
    };

    dropDownControl.selectDropdownItem = function (a) {
        var $a = $(a);

        var id = $a.attr('data-id');
        if (id) {
            var label = $a.html();
            lookupControl.setValues(id, label);
        }
        dropDownControl.$dropDown.removeClass('show');
    };

    dropDownControl.replaceDropdownContent = function (html) {
        var $dropDownReplacement = $(html).next('div.dropdown-menu');

        $dropDownReplacement.find('a[data-id]').click(function (event) {
            event.preventDefault();

            dropDownControl.selectDropdownItem(this);
        });

        dropDownControl.$dropDown.replaceWith($dropDownReplacement);
        dropDownControl.$dropDown = $dropDownReplacement;
    };

    dropDownControl.fetchLabels = function () {
        dropDownControl.cancelRequestInProgress();

        var filter = lookupControl.$labelInput.val();
        if (!filter) {
            return;
        }

        dropDownControl.labelsRequestTimeout = setTimeout(function () {
            $.get(dropDownControl.labelsUrl, {filter: filter}, dropDownControl.replaceDropdownContent);
        }, 300);
    };

    dropDownControl.fieldLostFocus = function () {
        dropDownControl.cancelRequestInProgress();

        setTimeout(function () {
            dropDownControl.$dropDown.removeClass('show');
        }, 300);
    };

    dropDownControl.bindListeners();

    var $lookupButton = $lookupField.find('button[data-lookup-list-url]');
    var $modal = $lookupField.find('div.modal');

    var modalControl = {
        $lookupButton: $lookupField.find('button[data-lookup-list-url]'),
        lookupListUrl: $lookupButton.attr('data-lookup-list-url'),
        lookupLabelUrl: $lookupButton.attr('data-lookup-label-url'),
        $modal: $modal,

        $modalBody: $modal.find('.modal-body')
    };

    modalControl.bindListeners = function () {
        modalControl.$lookupButton.click(modalControl.fetchList);
    };

    modalControl.selectTableRow = function (tr) {
        var $tr = $(tr);

        var id = $tr.attr('data-id');
        if (id) {
            $.get(modalControl.lookupLabelUrl, {id: id}, function (label) {
                lookupControl.setValues(id, label);
            }).fail(function () {
                lookupControl.setValues(id, id);
            });
        }

        modalControl.$modal.modal('hide');
    };

    modalControl.replaceModalContent = function (html) {
        var $table = $(html).next('table');

        var tBodyListenersBinder = function ($tBody) {
            $tBody.find('tr[data-id]').click(function () {
                modalControl.selectTableRow(this);
            });
        };

        new TableControl($table, tBodyListenersBinder);
        modalControl.$modalBody.empty().append($table);

        modalControl.$modal.modal();
    };

    modalControl.fetchList = function () {
        $.get(modalControl.lookupListUrl, {}, modalControl.replaceModalContent);
    };

    modalControl.bindListeners();
}

$('div.lookup').each(function (i, lookupField) {
    LookupControl(lookupField);
});

function AsyncFormPartControl(formPartSelector, actionElementsSelector) {
    var $formPart = $(formPartSelector);
    var asyncFormPartControl = {
        $formPart: $formPart,
        $form: $formPart.parents('form'),
        $actionElements: $(actionElementsSelector)
    };

    asyncFormPartControl.bindListeners = function () {
        this.$actionElements.off('click', '*', this.clickActionButton);
        this.$actionElements.click(this.clickActionButton);
    };

    asyncFormPartControl.clickActionButton = function (event) {
        event.preventDefault();
        asyncFormPartControl.requestFormPart(this);
    };

    asyncFormPartControl.requestFormPart = function (actionElement) {
        var action = asyncFormPartControl.$form.attr('action');
        var data = asyncFormPartControl.$form.serialize();
        var $actionElement = $(actionElement);
        data += '&' + encodeURI($actionElement.attr('name'));
        var actionButtonValue = $actionElement.val();
        if (actionButtonValue) {
            data += '=' + encodeURI(actionButtonValue);
        }

        $.post(action, data, asyncFormPartControl.replaceFormPartContent);
    };

    asyncFormPartControl.replaceFormPartContent = function (html) {
        var $formPartReplacement = $(html);

        asyncFormPartControl.$formPart.replaceWith($formPartReplacement);
        asyncFormPartControl.$formPart = $formPartReplacement;
        asyncFormPartControl.$actionElements = $(actionElementsSelector);

        asyncFormPartControl.bindListeners();
        asyncFormPartControl.$formPart.find('div.lookup').each(function (i, lookupField) {
            LookupControl(lookupField);
        });
    };

    asyncFormPartControl.bindListeners();
}

function DelayedFunctionCall(func, wait) {
    var timeout;

    return {
        call: function () {
            var context = this;
            var args = arguments;

            if (timeout) {
                clearTimeout(timeout);
            }

            timeout = setTimeout(function () {
                timeout = null;
                func.apply(context, args);
            }, wait);
        },
        cancel: function () {
            if (timeout) {
                clearTimeout(timeout);
            }
        }
    }
}
