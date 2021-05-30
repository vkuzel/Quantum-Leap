const lookupControlRegister = {};

function cancellableDebounce(func, wait = 500) {
    let timeout;

    return {
        call: function () {
            const context = this;
            const args = arguments;

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

function debounce(func, wait = 500) {
    return cancellableDebounce(func, wait).call
}

// There can be only one loader on a page
class Loader {
    static show() {
        const el = document.getElementById('loader')
        el.style.display = 'block';
    }

    static hide() {
        const el = document.getElementById('loader')
        el.style.display = 'none';
    }
}

class TableControl {

    #table = null
    #qualifier = null

    #tHead = null
    #tBody = null
    #tFoot = null
    #searchInputListenersBound = false
    #searchInput = null
    #searchQueries = null

    constructor(table) {
        this.#table = table
        this.#qualifier = this.#table.getAttribute('data-qualifier') || ''

        this.#tHead = this.#table.getElementsByTagName('thead')[0]
        this.#tBody = this.#table.getElementsByTagName('tbody')[0]
        this.#tFoot = this.#table.getElementsByTagName('tfoot')[0]
        this.#searchInput = document.getElementById(`${this.#qualifier}search`)
        this.#searchQueries = document.getElementsByClassName(`${this.#qualifier}query`)
    }

    #qualifyParamName(paramName) {
        return this.#qualifier ? `${this.#qualifier}_paramName` : paramName
    }

    #bindListeners() {
        if (this.#tHead) {
            const sortButtons = this.#tHead.getElementsByTagName('a')
            for (let sortButton of sortButtons) {
                this.#bindSortListener(sortButton)
            }
        }

        if (this.#tBody) {
            const trs = this.#tBody.getElementsByTagName('tr')
            for (let tr of trs) {
                this.#bindOpenDetailListeners(tr)
            }
        }

        if (this.#tFoot) {
            const loadMoreButtons = this.#tFoot.getElementsByClassName('btn-load-more')
            for (let loadMoreButton of loadMoreButtons) {
                this.#bindLoadMoreListener(loadMoreButton)
            }
        }

        if (this.#searchInput && !this.#searchInputListenersBound) {
            this.#searchInputListenersBound = true
            this.#bindSearchInputListener(this.#searchInput)
            for (const searchQuery of this.#searchQueries) {
                this.#bindSearchQueryListener(searchQuery)
            }
        }
    }

    #bindSortListener(sortButton) {
        sortButton.addEventListener('click', (event) => {
            event.preventDefault()
            this.#sort(sortButton)
        })
    }

    #bindOpenDetailListeners(tr) {
        const tds = tr.getElementsByTagName('td')
        for (const td of tds) {
            td.addEventListener('click', (event) => {
                event.preventDefault()
                const primaryKeyAnchors = td.parentElement.querySelectorAll('td.primary-key > a')
                const anchors = td.getElementsByTagName('a')
                if (anchors.length) {
                    window.location = anchors[0].href
                } else if (primaryKeyAnchors.length) {
                    window.location = primaryKeyAnchors[0].href
                }
            })
        }
    }

    #bindLoadMoreListener(loadMoreButton) {
        loadMoreButton.addEventListener("click", (event) => {
            event.preventDefault()
            this.#fetchMore(loadMoreButton)
        })
    }

    #bindSearchInputListener(searchInput) {
        searchInput.addEventListener('keyup', (event) => {
            event.preventDefault()
            this.#fetchSearchResults.call()
        })
        searchInput.addEventListener('blur', (event) => {
            event.preventDefault()
            this.#fetchSearchResults.cancel()
        })
    }

    #bindSearchQueryListener(searchQuery) {
        searchQuery.addEventListener('click', (event) => {
            event.preventDefault()
            this.#selectQuery(event.target)
        })
    }

    #appendContent(html) {
        const tHeadHtml = TableControl.#extractTagContentFromStringHtml('thead', html)
        const tBodyHtml = TableControl.#extractTagContentFromStringHtml('tbody', html)
        const tFootHtml = TableControl.#extractTagContentFromStringHtml('tfoot', html)

        this.#tHead.innerHTML = tHeadHtml
        this.#tBody.innerHTML += tBodyHtml
        this.#tFoot.innerHTML = tFootHtml

        this.#bindListeners()
    }

    #replaceContent(html) {
        const tHeadHtml = TableControl.#extractTagContentFromStringHtml('thead', html)
        const tBodyHtml = TableControl.#extractTagContentFromStringHtml('tbody', html)
        const tFootHtml = TableControl.#extractTagContentFromStringHtml('tfoot', html)

        this.#tHead.innerHTML = tHeadHtml
        this.#tBody.innerHTML = tBodyHtml
        if (!this.#tFoot && tFootHtml) {
            this.#tFoot = document.createElement('tfoot')
            this.#tBody.parentElement.appendChild(this.#tFoot)
        }
        if (this.#tFoot) {
            this.#tFoot.innerHTML = tFootHtml
        }

        this.#bindListeners()
    }

    static #extractTagContentFromStringHtml(tagName, html) {
        const regExp = new RegExp(`<${tagName}>(.*)</${tagName}>`, 'is')
        const match = html.match(regExp);
        return match !== null ? match[1] : '';
    }

    #fetchMore(loadMoreButton) {
        const sizeParamName = this.#qualifyParamName('size')
        const offset = this.#tBody.getElementsByTagName('tr').length
        const offsetParamName = this.#qualifyParamName('offset')

        const buttonHref = loadMoreButton.getAttribute('href')
        const url = new URL(buttonHref)
        url.searchParams.delete(sizeParamName)
        url.searchParams.set(offsetParamName, offset)

        this.#get(url, (responseText) => this.#appendContent(responseText))
    }

    #fetchSearchResults = cancellableDebounce(() => {
        const query = this.#searchInput.value
        const sizeParamName = this.#qualifyParamName('size')
        const offsetParamName = this.#qualifyParamName('offset')

        const url = new URL(location.href)
        url.searchParams.delete(sizeParamName)
        url.searchParams.delete(offsetParamName)
        url.searchParams.set('query', query)

        this.#get(url, (responseText) => this.#replaceContent(responseText))
    })

    #selectQuery(a) {
        this.#searchInput.value = a.getAttribute('data-query')
        this.#fetchSearchResults.call()
    }

    #sort(sortButton) {
        const size = this.#tBody.getElementsByTagName('tr').length
        const sizeParamName = this.#qualifyParamName('size');
        const offsetParamName = this.#qualifyParamName('offset');

        const url = new URL(sortButton.href)
        url.searchParams.set(sizeParamName, size)
        url.searchParams.delete(offsetParamName)

        this.#get(url, (responseText) => this.#replaceContent(responseText))
    }

    #get(url, loadListener) {
        const request = new XMLHttpRequest()
        const listener = (event) => loadListener(event.target.responseText)
        request.addEventListener('load', listener)
        request.open('GET', url)
        request.setRequestHeader('X-Requested-With', 'XMLHttpRequest')
        request.send()
    }

    static create(table) {
        const tableControl = new TableControl(table)
        tableControl.#bindListeners()
        return tableControl
    }
}

function LookupControl(lookupField) {
    const $lookupField = $(lookupField);

    const lookupControl = {
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

    const dropDownControl = {
        labelsUrl: lookupControl.$labelInput.attr('data-lookup-labels-url'),
        $dropDown: $lookupField.find('div.dropdown-menu')
    };

    dropDownControl.bindListeners = function () {
        lookupControl.$labelInput.keyup(dropDownControl.fetchLabels.call);
        lookupControl.$labelInput.blur(dropDownControl.fieldLostFocus);
        lookupControl.$resetButton.click(lookupControl.resetValues);
    };

    dropDownControl.selectDropdownItem = function (a) {
        const $a = $(a);

        const id = $a.attr('data-id');
        if (id) {
            const label = $a.html();
            lookupControl.setValues(id, label);
        }
        dropDownControl.$dropDown.removeClass('show');
    };

    dropDownControl.replaceDropdownContent = function (html) {
        const $dropDownReplacement = $(html).next('div.dropdown-menu');

        $dropDownReplacement.find('a[data-id]').click(function (event) {
            event.preventDefault();

            dropDownControl.selectDropdownItem(this);
        });

        dropDownControl.$dropDown.replaceWith($dropDownReplacement);
        dropDownControl.$dropDown = $dropDownReplacement;
    };

    dropDownControl.fetchLabels = cancellableDebounce(function () {
        let query = lookupControl.$labelInput.val();
        if (!query) {
            return;
        }
        const queryPrefix = lookupControl.$labelInput.attr('data-query-prefix');
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

    const $lookupButton = $lookupField.find('button[data-lookup-list-url]');
    const $modal = $lookupField.find('div.modal');

    const modalControl = {
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
        const $tr = $(tr);

        const id = $tr.attr('data-id');
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
        const $table = $(html).next('table');

        const tBodyListenersBinder = function ($tBody) {
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
    const $tagsField = $(tagsFieldSelector);

    const modalControl = {
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
        const tagsValue = modalControl.$tagsInput.val();
        const tags = tagsValue.length === 0 ? [] : tagsValue.split(',');
        tags.push(tag);
        modalControl.$tagsInput.val(tags.join(','));
    };

    modalControl.removeTag = function (tag) {
        const tags = modalControl.$tagsInput.val()
            .split(',')
            .filter((t) => t !== tag)
            .join(',')
        modalControl.$tagsInput.val(tags);
    };

    modalControl.createTag = function (tag) {
        tag = tag.replace(/[^a-z0-9]/gi, '').toLowerCase();
        if (!tag) {
            return;
        }

        const $checkbox = modalControl.$tagsCheckboxes.filter('[value="' + tag + '"]');
        if ($checkbox.length > 0) {
            if (!$checkbox.prop('checked')) {
                $checkbox.prop('checked', true);
                modalControl.addTag(tag);
            }
        } else {
            let html = '<div class="form-group w-25"><label class="form-check-label">';
            html += '<input class="form-check-input" type="checkbox" value="' + tag + '" checked> ' + tag + '</label></div>';
            const $element = $(html);
            const $newCheckbox = $element.find('input');
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
    const $formPart = $(formPartSelector);
    const asyncFormPartControl = {
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
        const $actionButton = $(this);
        let additionalData = '&' + encodeURI($actionButton.attr('name'));
        const actionButtonValue = $actionButton.val();
        if (actionButtonValue) {
            additionalData += '=' + encodeURI(actionButtonValue);
        }
        asyncFormPartControl.requestFormPart(additionalData);
    };

    asyncFormPartControl.onActionElementChange = function () {
        const additionalData = $(this).attr('data-async-form-action');
        asyncFormPartControl.requestFormPart(additionalData);
    };

    asyncFormPartControl.requestFormPart = function (additionalData) {
        const action = asyncFormPartControl.$form.attr('action');
        let data = asyncFormPartControl.$form.serialize();
        data += '&' + additionalData;
        const formPartPromise = $.post(action, data).done(asyncFormPartControl.replaceFormPartContent);
        if (formPartPromiseConsumer) {
            formPartPromiseConsumer(formPartPromise, asyncFormPartControl);
        }
    };

    asyncFormPartControl.replaceFormPartContent = function (html) {
        const $formPartReplacement = $(html);

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
        const formPartPromise = $.Deferred();
        formPartPromiseConsumer(formPartPromise, asyncFormPartControl);
        formPartPromise.resolve(asyncFormPartControl.$formPart);
    }
}

function ModalFormControl(modalSelector, openModalButtonsSelector, submitPromiseConsumer) {
    const $modal = $(modalSelector);

    const modalFormControl = {
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
        const $actionButton = $(this);
        let additionalData = '&' + encodeURI($actionButton.attr('name'));
        const actionButtonValue = $actionButton.val();
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
        const $modalBodyReplacement = $(html);
        const $modalBody = modalFormControl.$modal.find('.modal-body').first();

        $modalBody.replaceWith($modalBodyReplacement);
        modalFormControl.$submitModalButtons = modalFormControl.$modal.find('input[type="submit"],button[type="submit"]');
        modalFormControl.bindListeners();
        $modalBodyReplacement.find('form div.lookup').each(function (i, lookupField) {
            LookupControl(lookupField);
        });
    };

    modalFormControl.fetchModal = function () {
        const url = modalFormControl.$openModalButtons.attr('data-modal-url');
        $.get(url, {}, modalFormControl.replaceModalContent);
    };

    modalFormControl.submitModal = function (additionalData) {
        const $form = modalFormControl.$modal.find('form');
        const action = $form.attr('action');
        let data = $form.serialize();
        if (additionalData) {
            data += '&' + additionalData;
        }
        const submitPromise = $.post(action, data).done(modalFormControl.replaceModalContent);
        if (submitPromiseConsumer) {
            submitPromiseConsumer(submitPromise, modalFormControl);
        }
    };

    modalFormControl.bindListeners();
}

$(function () {
    const tables = document.querySelectorAll('table.data-table')
    for (let table of tables) {
        TableControl.create(table)
    }

    $('div.lookup').each(function (i, lookupField) {
        LookupControl(lookupField);
    });

    $('div.tags').each(function (i, tagsField) {
        TagsControl(tagsField);
    });
});

