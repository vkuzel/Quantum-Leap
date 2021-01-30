var lookupControlRegister = {};

var UrlUtils = {
    getQueryParam: function (url, name) {
        var regExp = new RegExp(name + "=([^&#]+)");
        var match = url.match(regExp);
        return match !== null ? match[1] : null;
    },
    replaceQueryParam: function (url, name, value) {
        var regExp = new RegExp(name + '=[^&#]+');
        if (url.match(regExp) != null) {
            return url.replace(regExp, name + '=' + encodeURIComponent(value));
        } else if (url.indexOf('?') < 0) {
            return url + '?' + name + '=' + encodeURIComponent(value);
        } else {
            return url + '&' + name + '=' + encodeURIComponent(value);
        }
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

function TableControl(tableSelector, tBodyListenersBinder) {
    var $table = $(tableSelector);

    var tableControl = {
        $table: $table,
        qualifier: $table.attr('data-qualifier'),

        $tHead: $table.find('thead'),
        $tBody: $table.find('tbody'),
        $tFoot: $table.find('tfoot'),
        $searchInput: $($table.attr('data-search-selector'))
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
        var offsetParamName = qualifyParamName(tableControl.qualifier, 'offset');

        var url = UrlUtils.removeQueryParams(this.href, qualifyParamName(tableControl.qualifier, 'size'), offsetParamName);
        var data = {};
        data[offsetParamName] = offset;

        $.get(url, data, tableControl.appendContent);

        return false;
    };

    tableControl.fetchSearchResults = DelayedFunctionCall(function () {
        var query = tableControl.$searchInput.val();
        var url = UrlUtils.removeQueryParams(location.href, qualifyParamName(tableControl.qualifier, 'size'), qualifyParamName(tableControl.qualifier, 'offset'));

        $.get(url, {query: query}, tableControl.replaceContent);

        return false;
    }, 500);

    tableControl.sort = function () {
        var size = tableControl.$tBody.find('tr').length;
        var sizeParamName = qualifyParamName(tableControl.qualifier, 'size');

        var url = UrlUtils.removeQueryParams(this.href, sizeParamName, qualifyParamName(tableControl.qualifier, 'offset'));
        var data = {};
        data[sizeParamName] = size;

        $.get(url, data, tableControl.replaceContent);

        return false;
    };

    tableControl.bindListeners();
    if (tableControl.$searchInput) {
        tableControl.$searchInput.keyup(tableControl.fetchSearchResults.call);
        tableControl.$searchInput.blur(tableControl.fetchSearchResults.cancel);
    }
}

function LookupControl(lookupField) {
    var $lookupField = $(lookupField);

    var lookupControl = {
        $lookupField: $lookupField,

        $dataInput: $lookupField.find(':hidden'),
        $labelInput: $lookupField.find(':text'),
        $resetButtonWrapper: $lookupField.find('.reset-wrapper'),
        $resetButton: $lookupField.find('.reset-wrapper > button')
    };

    lookupControlRegister[lookupControl.$labelInput.attr('id')] = lookupControl;

    lookupControl.setValues = function (id, label) {
        lookupControl.$dataInput.val(id);
        lookupControl.$labelInput.val(label);
        lookupControl.$resetButtonWrapper.attr('hidden', !id);
        lookupControl.$dataInput.trigger('change');
    };

    lookupControl.resetValues = function () {
        lookupControl.setValues(null, null);
    };

    lookupControl.getId = function() {
        return lookupControl.$dataInput.val();
    };

    var dropDownControl = {
        labelsUrl: lookupControl.$labelInput.attr('data-lookup-labels-url'),
        $dropDown: $lookupField.find('div.dropdown-menu')
    };

    dropDownControl.bindListeners = function () {
        lookupControl.$labelInput.keyup(dropDownControl.fetchLabels.call);
        lookupControl.$labelInput.blur(dropDownControl.fieldLostFocus);
        lookupControl.$resetButton.click(lookupControl.resetValues);
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

    dropDownControl.fetchLabels = DelayedFunctionCall(function () {
        var query = lookupControl.$labelInput.val();
        if (!query) {
            return;
        }
        var queryPrefix = lookupControl.$labelInput.attr('data-query-prefix');
        if (queryPrefix) {
            query = queryPrefix + ' ' + query;
        }

        $.get(dropDownControl.labelsUrl, {query: query}, dropDownControl.replaceDropdownContent);
    }, 300);

    dropDownControl.fieldLostFocus = function () {
        dropDownControl.fetchLabels.cancel();

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

        $modalBody: $modal.find('.modal-body').first()
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

function TagsControl(tagsFieldSelector) {
    var $tagsField = $(tagsFieldSelector);

    var modalControl = {
        $tagsInput: $tagsField.find('input[name="tags"]'),
        $tagsButton: $tagsField.find('button[name="select-tags"]'),
        $modal: $tagsField.find('div.modal'),
        $tagsContainer: $tagsField.find('.tags-container'),
        $tagsCheckboxes: $tagsField.find('input[type="checkbox"]'),
        $newTagInput: $tagsField.find('input[name="new-tag"]'),
        $createNewTagButton: $tagsField.find('button[name="create-new-tag"]')
    };

    modalControl.bindListeners = function () {
        modalControl.$tagsButton.click(modalControl.show);
        modalControl.$tagsCheckboxes.click(function () {
            modalControl.changeTag(this.value, this.checked);
        });
        modalControl.$createNewTagButton.click(function () {
            modalControl.createTag(modalControl.$newTagInput.val());
        });
    };

    modalControl.show = function () {
        modalControl.$modal.modal();
    };

    modalControl.changeTag = function (tag, checked) {
        if (checked) {
            modalControl.addTag(tag);
        } else {
            modalControl.removeTag(tag);
        }
    };

    modalControl.addTag = function (tag) {
        var tagsValue = modalControl.$tagsInput.val();
        var tags = tagsValue.length === 0 ? [] : tagsValue.split(',');
        tags.push(tag);
        modalControl.$tagsInput.val(tags.join(','));
    };

    modalControl.removeTag = function (tag) {
        var tags = modalControl.$tagsInput.val().split(',');
        tags = tags.filter(function (t) {
            return t !== tag;
        });
        modalControl.$tagsInput.val(tags.join(','));
    };

    modalControl.createTag = function (tag) {
        tag = tag.replace(/[^a-z0-9]/gi, '').toLowerCase();
        if (!tag) {
            return;
        }

        var $checkbox = modalControl.$tagsCheckboxes.filter('[value="' + tag + '"]');
        if ($checkbox.length > 0) {
            if (!$checkbox.prop('checked')) {
                $checkbox.prop('checked', true);
                modalControl.addTag(tag);
            }
        } else {
            var html = '<div class="form-group w-25"><label class="form-check-label">';
            html += '<input class="form-check-input" type="checkbox" value="' + tag + '" checked> ' + tag + '</label></div>';
            var $element = $(html);
            var $newCheckbox = $element.find('input');
            $newCheckbox.click(function () {
                modalControl.changeTag(this.value, this.checked);
            });
            modalControl.$tagsCheckboxes = modalControl.$tagsCheckboxes.add($newCheckbox);
            modalControl.$tagsContainer.append($element);
            modalControl.addTag(tag);
        }
    };

    modalControl.bindListeners();
}

function AsyncFormPartControl(formPartSelector, actionElementsSelector, formPartPromiseConsumer) {
    var $formPart = $(formPartSelector);
    var asyncFormPartControl = {
        $formPart: $formPart,
        $form: $formPart.parents('form'),
        $actionElements: $(actionElementsSelector)
    };

    asyncFormPartControl.bindListeners = function () {
        this.$actionElements.off('click', this.onActionButtonClick);
        this.$actionElements.on('click', this.onActionButtonClick);
        this.$actionElements.change(this.onActionElementChange);
    };

    asyncFormPartControl.onActionButtonClick = function (event) {
        event.preventDefault();
        var $actionButton = $(this);
        var additionalData = '&' + encodeURI($actionButton.attr('name'));
        var actionButtonValue = $actionButton.val();
        if (actionButtonValue) {
            additionalData += '=' + encodeURI(actionButtonValue);
        }
        asyncFormPartControl.requestFormPart(additionalData);
    };

    asyncFormPartControl.onActionElementChange = function () {
        var additionalData = $(this).attr('data-async-form-action');
        asyncFormPartControl.requestFormPart(additionalData);
    };

    asyncFormPartControl.requestFormPart = function (additionalData) {
        var action = asyncFormPartControl.$form.attr('action');
        var data = asyncFormPartControl.$form.serialize();
        data += '&' + additionalData;
        var formPartPromise = $.post(action, data).done(asyncFormPartControl.replaceFormPartContent);
        if (formPartPromiseConsumer) {
            formPartPromiseConsumer(formPartPromise, asyncFormPartControl);
        }
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
    if (formPartPromiseConsumer) {
        var formPartPromise = $.Deferred();
        formPartPromiseConsumer(formPartPromise, asyncFormPartControl);
        formPartPromise.resolve(asyncFormPartControl.$formPart);
    }
}

function ModalFormControl(modalSelector, openModalButtonsSelector, submitPromiseConsumer) {
    var $modal = $(modalSelector);

    var modalFormControl = {
        $modal: $modal,
        $openModalButtons: $(openModalButtonsSelector),
        $submitModalButtons: $()
    };

    modalFormControl.bindListeners = function () {
        this.$openModalButtons.off('click', this.onOpenModalButtonClick);
        this.$openModalButtons.on('click', this.onOpenModalButtonClick);
        this.$submitModalButtons.off('click', this.onSubmitModalButtonClick);
        this.$submitModalButtons.on('click', this.onSubmitModalButtonClick);
        this.$modal.find('.modal-body form').first().off('submit', this.onSubmitModal);
        this.$modal.find('.modal-body form').first().on('submit', this.onSubmitModal);
    };

    modalFormControl.onOpenModalButtonClick = function (event) {
        event.preventDefault();
        modalFormControl.fetchModal();
    };

    modalFormControl.onSubmitModalButtonClick = function (event) {
        event.preventDefault();
        var $actionButton = $(this);
        var additionalData = '&' + encodeURI($actionButton.attr('name'));
        var actionButtonValue = $actionButton.val();
        if (actionButtonValue) {
            additionalData += '=' + encodeURI(actionButtonValue);
        }
        modalFormControl.submitModal(additionalData);
    };

    modalFormControl.onSubmitModal = function (event) {
        event.preventDefault();
        modalFormControl.submitModal();
    };

    modalFormControl.replaceModalContent = function (html) {
        var $modalBodyReplacement = $(html);
        var $modalBody = modalFormControl.$modal.find('.modal-body').first();

        $modalBody.replaceWith($modalBodyReplacement);
        modalFormControl.$submitModalButtons = modalFormControl.$modal.find('input[type="submit"],button[type="submit"]');
        modalFormControl.bindListeners();
        $modalBodyReplacement.find('form div.lookup').each(function (i, lookupField) {
            LookupControl(lookupField);
        });
    };

    modalFormControl.fetchModal = function () {
        var url = modalFormControl.$openModalButtons.attr('data-modal-url');
        $.get(url, {}, modalFormControl.replaceModalContent);
    };

    modalFormControl.submitModal = function (additionalData) {
        var $form = modalFormControl.$modal.find('form');
        var action = $form.attr('action');
        var data = $form.serialize();
        if (additionalData) {
            data += '&' + additionalData;
        }
        var submitPromise = $.post(action, data).done(modalFormControl.replaceModalContent);
        if (submitPromiseConsumer) {
            submitPromiseConsumer(submitPromise, modalFormControl);
        }
    };

    modalFormControl.bindListeners();
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

$(function () {
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

    $('div.lookup').each(function (i, lookupField) {
        LookupControl(lookupField);
    });

    $('div.tags').each(function (i, tagsField) {
        TagsControl(tagsField);
    });
});

