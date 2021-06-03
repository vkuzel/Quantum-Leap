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
    #tBodyListenersBinder = null

    #tHead = null
    #tBody = null
    #tFoot = null
    #searchInputListenersBound = false
    #searchInput = null
    #searchQueries = null

    constructor(table, tBodyListenersBinder) {
        this.#table = table
        this.#qualifier = this.#table.getAttribute('data-qualifier') || ''
        this.#tBodyListenersBinder = tBodyListenersBinder

        this.#tHead = this.#table.getElementsByTagName('thead')[0]
        this.#tBody = this.#table.getElementsByTagName('tbody')[0]
        this.#tFoot = this.#table.getElementsByTagName('tfoot')[0]
        this.#searchInput = document.getElementById(`${this.#qualifier}search`)
        this.#searchQueries = document.getElementsByClassName(`${this.#qualifier}query`)

        this.#bindListeners()
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

        if (this.#tBody && this.#tBodyListenersBinder) {
            this.#tBodyListenersBinder(this.#tBody)
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
}

class LookupControl {

    static #DROP_DOWN_CONTENT_REGEXP = new RegExp('<div class="[^"]*dropdown-menu[^"]*">(.*)</div>', 'is')
    static #MODAL_BODY_CONTENT_REGEXP = new RegExp('(<table[^>]*>.*</table>)', 'is')

    static #registry = {}

    #lookupField = null

    #dataInput = null
    #labelInput = null
    #resetButtonWrapper = null
    #resetButton = null

    #dropDownLabelsUrl = null
    #dropDown = null

    #openModalButton = null
    #listUrl = null
    #labelUrl = null
    #modal = null
    #modalBody = null

    constructor(lookupField) {
        this.#lookupField = lookupField

        this.#dataInput = this.#lookupField.querySelector('input[type="hidden"]')
        this.#labelInput = this.#lookupField.querySelector('input[type="text"]')
        this.#resetButtonWrapper = this.#lookupField.querySelector('.reset-wrapper')
        this.#resetButton = this.#lookupField.querySelector('.reset-wrapper > button')

        this.#dropDownLabelsUrl = this.#labelInput.getAttribute('data-lookup-labels-url')
        this.#dropDown = this.#lookupField.querySelector('div.dropdown-menu')

        this.#openModalButton = this.#lookupField.querySelector('button[data-lookup-list-url]')
        this.#listUrl = this.#openModalButton.getAttribute('data-lookup-list-url')
        this.#labelUrl = this.#openModalButton.getAttribute('data-lookup-label-url')
        this.#modal = this.#lookupField.querySelector('div.modal')
        this.#modalBody = this.#modal.querySelector('.modal-body')

        const id = this.#labelInput.getAttribute('id')
        LookupControl.#registry[id] = this

        this.#bindListeners()
    }

    #setValues(id, label) {
        this.#dataInput.value = id
        this.#labelInput.value = label
        if (id) {
            this.#resetButtonWrapper.removeAttribute('hidden')
        } else {
            this.#resetButtonWrapper.setAttribute('hidden', 'hidden')
        }
        const event = new Event('change')
        this.#dataInput.dispatchEvent(event)
    }

    resetValues() {
        this.#setValues(null, null)
    }

    #bindListeners() {
        this.#labelInput.addEventListener('keyup', (event) => {
            event.preventDefault()
            this.#fetchLabels.call()
        })
        this.#labelInput.addEventListener('blur', (event) => {
            event.preventDefault()
            this.#fetchLabels.cancel()
            setTimeout(() => this.#dropDown.classList.remove('show'), 300)
        })
        this.#resetButton.addEventListener('click', (event) => {
            event.preventDefault()
            this.resetValues()
        })
        this.#openModalButton.addEventListener('click', (event) => {
            event.preventDefault()
            this.#fetchList()
        })
    }

    #selectDropDownItem(anchor) {
        const id = anchor.getAttribute('data-id')
        if (id) {
            const label = anchor.innerHTML
            this.#setValues(id, label)
        }
        this.#dropDown.classList.remove('show')
    }

    #replaceDropDownContent(html) {
        const match = html.match(LookupControl.#DROP_DOWN_CONTENT_REGEXP)
        this.#dropDown.innerHTML = match[1] || ''
        this.#dropDown.classList.add('show')
        const anchors = this.#dropDown.querySelectorAll('a[data-id]')
        for (let anchor of anchors) {
            anchor.addEventListener('click', (event) => {
                event.preventDefault()
                this.#selectDropDownItem(anchor)
            })
        }
    }

    #fetchLabels = cancellableDebounce(() => {
        let query = this.#labelInput.value
        if (!query) {
            return
        }
        const queryPrefix = this.#labelInput.getAttribute('data-query-prefix')
        if (queryPrefix) {
            query = queryPrefix + ' ' + query
        }

        const url = new URL(location.href)
        url.pathname = this.#dropDownLabelsUrl
        url.search = ''
        url.searchParams.set('query', query)

        this.#get(url, (responseText) => this.#replaceDropDownContent(responseText))
    }, 300)

    #selectTableRow(tr) {
        const id = tr.getAttribute('data-id')
        if (id) {
            const url = new URL(location.href)
            url.pathname = this.#labelUrl
            url.search = ''
            url.searchParams.set('id', id)

            this.#get(
                url,
                (label) => this.#setValues(id, label),
                () => this.resetValues()
            )
        }

        $(this.#modal).modal('hide')
    }

    #replaceModalContent(html) {
        const match = html.match(LookupControl.#MODAL_BODY_CONTENT_REGEXP)
        this.#modalBody.innerHTML = match[1] || ''
        const table = this.#modalBody.getElementsByTagName('table')[0]
        const bindSelectRowListener = (tBody) => {
            const trs = tBody.querySelectorAll('tr[data-id]')
            for (let tr of trs) {
                tr.addEventListener('click', (event) => {
                    event.preventDefault()
                    this.#selectTableRow(tr)
                })
            }
        }

        new TableControl(table, bindSelectRowListener)

        $(this.#modal).modal()
    }

    #fetchList() {
        const url = new URL(location.href)
        url.pathname = this.#listUrl
        this.#get(url, (responseText) => this.#replaceModalContent(responseText))
    }

    #get(url, loadListener, errorListener) {
        const request = new XMLHttpRequest()
        const listener = (event) => loadListener(event.target.responseText)
        request.addEventListener('load', listener)
        request.addEventListener('error', (event) => errorListener(event))
        request.open('GET', url)
        request.setRequestHeader('X-Requested-With', 'XMLHttpRequest')
        request.send()
    }

    static getById(id) {
        return LookupControl.#registry[id]
    }
}

class TagsControl {

    #tagsField = null

    #tagsInput = null
    #tagsButton = null
    #modal = null
    #tagsContainer = null
    #tagsCheckboxes = null
    #newTagInput = null
    #createNewTagInput = null

    constructor(tagsField) {
        this.#tagsField = tagsField

        this.#tagsInput = this.#tagsField.querySelector('input[name="tags"]')
        this.#tagsButton = this.#tagsField.querySelector('button[name="select-tags"]')
        this.#modal = this.#tagsField.querySelector('div.modal')
        this.#tagsContainer = this.#tagsField.querySelector('.tags-container')
        this.#tagsCheckboxes = [...this.#tagsField.querySelectorAll('input[type="checkbox"]')]
        this.#newTagInput = this.#tagsField.querySelector('input[name="new-tag"]')
        this.#createNewTagInput = this.#tagsField.querySelector('button[name="create-new-tag"]')

        this.#bindListeners()
    }

    #bindListeners() {
        this.#tagsButton.addEventListener('click', (event) => {
            event.preventDefault()
            $(this.#modal).modal()
        })
        for (const checkbox of this.#tagsCheckboxes) {
            this.#bindCheckboxListener(checkbox)
        }
        this.#createNewTagInput.addEventListener('click', (event) => {
            event.preventDefault()
            this.#createTag(this.#newTagInput.value)
        })
    }

    #bindCheckboxListener(checkbox) {
        checkbox.addEventListener('click', () => {
            this.#changeTag(checkbox.value, checkbox.checked)
        })
    }

    #changeTag(tag, checked) {
        if (checked) {
            this.#addTag(tag)
        } else {
            this.#removeTag(tag)
        }
    }

    #addTag(tag) {
        const tagsValue = this.#tagsInput.value
        const tags = tagsValue ? tagsValue.split(',') : []
        tags.push(tag)
        this.#tagsInput.value = tags.join(',')
    }

    #removeTag(tag) {
        this.#tagsInput.value = this.#tagsInput.value
            .split(',')
            .filter((t) => t !== tag)
            .join(',')
    }

    #createTag(tag) {
        tag = tag.replace(/[^a-z0-9]/gi, '').toLowerCase();
        if (!tag) {
            return;
        }

        let checkbox = this.#tagsCheckboxes
            .find((checkbox) => checkbox.value === tag)

        if (!checkbox) {
            checkbox = document.createElement('input')
            checkbox.type = 'checkbox'
            checkbox.value = tag
            checkbox.className = 'form-check-input'
            this.#bindCheckboxListener(checkbox)
            this.#tagsCheckboxes.push(checkbox)

            const label = document.createElement('label')
            label.className = 'form-check-label'
            label.appendChild(checkbox)
            label.appendChild(document.createTextNode(` ${tag}`))

            const div = document.createElement('div')
            div.className = 'form-group w-25'
            div.appendChild(label)

            this.#tagsContainer.appendChild(div)
        }

        if (!checkbox.checked) {
            checkbox.checked = true
            this.#addTag(tag)
        }
    }
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
            new LookupControl(lookupField);
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
            new LookupControl(lookupField);
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

document.addEventListener('DOMContentLoaded', () => {
    const tables = document.querySelectorAll('table.data-table')
    for (let table of tables) {
        const bindOpenDetailListeners = (tBody) => {
            const tds = tBody.querySelectorAll('tr > td')
            for (let td of tds) {
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

        new TableControl(table, bindOpenDetailListeners)
    }

    const lookups = document.querySelectorAll('div.lookup')
    for (let lookup of lookups) {
        new LookupControl(lookup)
    }

    const tags = document.querySelectorAll('div.tags')
    for (let tag of tags) {
        new TagsControl(tag)
    }
})
