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

class Validate {

    static nonNull(value) {
        throw 'Value has to be non-null!'
    }

    static isTrue(value) {
        if (!value) {
            throw 'Value has to be true!'
        }
    }

    static isInstanceOf(object, clazz) {
        if (!object instanceof clazz) {
            throw `${object} has to be instance of ${clazz}`
        }
    }

    static isInstanceOfOrEmpty(object, clazz) {
        if (object != null && !object instanceof clazz) {
            throw `${object} has to be instance of ${clazz}`
        }
    }
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

    _table = null
    _qualifier = null
    _tBodyListenersBinder = null

    _tHead = null
    _tBody = null
    _tFoot = null
    _searchInputListenersBound = false
    _searchInput = null
    _searchQueries = null

    constructor(table, tBodyListenersBinder) {
        Validate.isInstanceOf(table, HTMLTableElement)
        Validate.isInstanceOfOrEmpty(tBodyListenersBinder, Function)

        this._table = table
        this._qualifier = this._table.getAttribute('data-qualifier') || ''
        this._tBodyListenersBinder = tBodyListenersBinder

        this._tHead = this._table.getElementsByTagName('thead')[0]
        this._tBody = this._table.getElementsByTagName('tbody')[0]
        this._tFoot = this._table.getElementsByTagName('tfoot')[0]
        this._searchInput = document.getElementById(`${this._qualifier}search`)
        this._searchQueries = document.getElementsByClassName(`${this._qualifier}query`)

        this._bindListeners()
    }

    _qualifyParamName(paramName) {
        return this._qualifier ? `${this._qualifier}_paramName` : paramName
    }

    _bindListeners() {
        if (this._tHead) {
            const sortButtons = this._tHead.getElementsByTagName('a')
            for (let sortButton of sortButtons) {
                this._bindSortListener(sortButton)
            }
        }

        if (this._tBody && this._tBodyListenersBinder) {
            this._tBodyListenersBinder(this._tBody)
        }

        if (this._tFoot) {
            const loadMoreButtons = this._tFoot.getElementsByClassName('btn-load-more')
            for (let loadMoreButton of loadMoreButtons) {
                this._bindLoadMoreListener(loadMoreButton)
            }
        }

        if (this._searchInput && !this._searchInputListenersBound) {
            this._searchInputListenersBound = true
            this._bindSearchInputListener(this._searchInput)
            for (const searchQuery of this._searchQueries) {
                this._bindSearchQueryListener(searchQuery)
            }
        }
    }

    _bindSortListener(sortButton) {
        sortButton.addEventListener('click', (event) => {
            event.preventDefault()
            this._sort(sortButton)
        })
    }

    _bindLoadMoreListener(loadMoreButton) {
        loadMoreButton.addEventListener("click", (event) => {
            event.preventDefault()
            this._fetchMore(loadMoreButton)
        })
    }

    _bindSearchInputListener(searchInput) {
        searchInput.addEventListener('keyup', (event) => {
            event.preventDefault()
            this._fetchSearchResults.call()
        })
        searchInput.addEventListener('blur', (event) => {
            event.preventDefault()
            this._fetchSearchResults.cancel()
        })
    }

    _bindSearchQueryListener(searchQuery) {
        searchQuery.addEventListener('click', (event) => {
            event.preventDefault()
            this._selectQuery(searchQuery)
        })
    }

    _appendContent(html) {
        const tHeadHtml = TableControl._extractTagContentFromStringHtml('thead', html)
        const tBodyHtml = TableControl._extractTagContentFromStringHtml('tbody', html)
        const tFootHtml = TableControl._extractTagContentFromStringHtml('tfoot', html)

        this._tHead.innerHTML = tHeadHtml
        this._tBody.innerHTML += tBodyHtml
        this._tFoot.innerHTML = tFootHtml

        this._bindListeners()
    }

    _replaceContent(html) {
        const tHeadHtml = TableControl._extractTagContentFromStringHtml('thead', html)
        const tBodyHtml = TableControl._extractTagContentFromStringHtml('tbody', html)
        const tFootHtml = TableControl._extractTagContentFromStringHtml('tfoot', html)

        this._tHead.innerHTML = tHeadHtml
        this._tBody.innerHTML = tBodyHtml
        if (!this._tFoot && tFootHtml) {
            this._tFoot = document.createElement('tfoot')
            this._tBody.parentElement.appendChild(this._tFoot)
        }
        if (this._tFoot) {
            this._tFoot.innerHTML = tFootHtml
        }

        this._bindListeners()
    }

    static _extractTagContentFromStringHtml(tagName, html) {
        const regExp = new RegExp(`<${tagName}>(.*)</${tagName}>`, 'is')
        const match = html.match(regExp);
        return match !== null ? match[1] : '';
    }

    _fetchMore(loadMoreButton) {
        const sizeParamName = this._qualifyParamName('size')
        const offset = this._tBody.getElementsByTagName('tr').length
        const offsetParamName = this._qualifyParamName('offset')

        const buttonHref = loadMoreButton.getAttribute('href')
        const url = new URL(buttonHref)
        url.searchParams.delete(sizeParamName)
        url.searchParams.set(offsetParamName, offset)

        this._get(url, (responseText) => this._appendContent(responseText))
    }

    _fetchSearchResults = cancellableDebounce(() => {
        const query = this._searchInput.value
        const sizeParamName = this._qualifyParamName('size')
        const offsetParamName = this._qualifyParamName('offset')

        const url = new URL(location.href)
        url.searchParams.delete(sizeParamName)
        url.searchParams.delete(offsetParamName)
        url.searchParams.set('query', query)

        this._get(url, (responseText) => this._replaceContent(responseText))
    })

    _selectQuery(a) {
        this._searchInput.value = a.getAttribute('data-query')
        this._fetchSearchResults.call()
    }

    _sort(sortButton) {
        const size = this._tBody.getElementsByTagName('tr').length
        const sizeParamName = this._qualifyParamName('size');
        const offsetParamName = this._qualifyParamName('offset');

        const url = new URL(sortButton.href)
        url.searchParams.set(sizeParamName, size)
        url.searchParams.delete(offsetParamName)

        this._get(url, (responseText) => this._replaceContent(responseText))
    }

    _get(url, loadListener) {
        const request = new XMLHttpRequest()
        const listener = (event) => loadListener(event.target.responseText)
        request.addEventListener('load', listener)
        request.open('GET', url)
        request.setRequestHeader('X-Requested-With', 'XMLHttpRequest')
        request.send()
    }
}

class LookupControl {

    static _DROP_DOWN_CONTENT_REGEXP = new RegExp('<div class="[^"]*dropdown-menu[^"]*">(.*)</div>', 'is')
    static _MODAL_BODY_CONTENT_REGEXP = new RegExp('(<table[^>]*>.*</table>)', 'is')

    static _registry = {}

    _lookupField = null

    _dataInput = null
    _labelInput = null
    _resetButtonWrapper = null
    _resetButton = null

    _dropDownLabelsUrl = null
    _dropDown = null

    _openModalButton = null
    _listUrl = null
    _labelUrl = null
    _modal = null
    _modalBody = null

    constructor(lookupField) {
        Validate.isInstanceOf(lookupField, HTMLDivElement)

        this._lookupField = lookupField

        this._dataInput = this._lookupField.querySelector('input[type="hidden"]')
        this._labelInput = this._lookupField.querySelector('input[type="text"]')
        this._resetButtonWrapper = this._lookupField.querySelector('.reset-wrapper')
        this._resetButton = this._lookupField.querySelector('.reset-wrapper > button')

        this._dropDownLabelsUrl = this._labelInput.getAttribute('data-lookup-labels-url')
        this._dropDown = this._lookupField.querySelector('div.dropdown-menu')

        this._openModalButton = this._lookupField.querySelector('button[data-lookup-list-url]')
        this._listUrl = this._openModalButton.getAttribute('data-lookup-list-url')
        this._labelUrl = this._openModalButton.getAttribute('data-lookup-label-url')
        this._modal = this._lookupField.querySelector('div.modal')
        this._modalBody = this._modal.querySelector('.modal-body')

        const id = this._labelInput.getAttribute('id')
        LookupControl._registry[id] = this

        this._bindListeners()
    }

    _setValues(id, label) {
        this._dataInput.value = id
        this._labelInput.value = label
        if (id) {
            this._resetButtonWrapper.removeAttribute('hidden')
        } else {
            this._resetButtonWrapper.setAttribute('hidden', 'hidden')
        }
        const event = new Event('change')
        this._dataInput.dispatchEvent(event)
    }

    resetValues() {
        this._setValues(null, null)
    }

    _bindListeners() {
        this._labelInput.addEventListener('keyup', (event) => {
            event.preventDefault()
            this._fetchLabels.call()
        })
        this._labelInput.addEventListener('blur', (event) => {
            event.preventDefault()
            this._fetchLabels.cancel()
            setTimeout(() => this._dropDown.classList.remove('show'), 300)
        })
        this._resetButton.addEventListener('click', (event) => {
            event.preventDefault()
            this.resetValues()
        })
        this._openModalButton.addEventListener('click', (event) => {
            event.preventDefault()
            this._fetchList()
        })
    }

    _selectDropDownItem(anchor) {
        const id = anchor.getAttribute('data-id')
        if (id) {
            const label = anchor.innerHTML
            this._setValues(id, label)
        }
        this._dropDown.classList.remove('show')
    }

    _replaceDropDownContent(html) {
        const match = html.match(LookupControl._DROP_DOWN_CONTENT_REGEXP)
        this._dropDown.innerHTML = match[1] || ''
        this._dropDown.classList.add('show')
        const anchors = this._dropDown.querySelectorAll('a[data-id]')
        for (let anchor of anchors) {
            anchor.addEventListener('click', (event) => {
                event.preventDefault()
                this._selectDropDownItem(anchor)
            })
        }
    }

    _fetchLabels = cancellableDebounce(() => {
        let query = this._labelInput.value
        if (!query) {
            return
        }
        const queryPrefix = this._labelInput.getAttribute('data-query-prefix')
        if (queryPrefix) {
            query = queryPrefix + ' ' + query
        }

        const url = new URL(this._dropDownLabelsUrl, location.origin)
        url.searchParams.set('query', query)

        this._get(url, (responseText) => this._replaceDropDownContent(responseText))
    }, 300)

    _selectTableRow(tr) {
        const id = tr.getAttribute('data-id')
        if (id) {
            const url = new URL(this._labelUrl, location.origin)
            url.searchParams.set('id', id)

            this._get(
                url,
                (label) => this._setValues(id, label),
                () => this.resetValues()
            )
        }

        $(this._modal).modal('hide')
    }

    _replaceModalContent(html) {
        const match = html.match(LookupControl._MODAL_BODY_CONTENT_REGEXP)
        this._modalBody.innerHTML = match[1] || ''
        const table = this._modalBody.getElementsByTagName('table')[0]
        const bindSelectRowListener = (tBody) => {
            const trs = tBody.querySelectorAll('tr[data-id]')
            for (let tr of trs) {
                tr.addEventListener('click', (event) => {
                    event.preventDefault()
                    this._selectTableRow(tr)
                })
            }
        }

        new TableControl(table, bindSelectRowListener)

        $(this._modal).modal()
    }

    _fetchList() {
        const url = new URL(this._listUrl, location.origin)
        this._get(url, (responseText) => this._replaceModalContent(responseText))
    }

    _get(url, loadListener, errorListener) {
        const request = new XMLHttpRequest()
        const listener = (event) => loadListener(event.target.responseText)
        request.addEventListener('load', listener)
        request.addEventListener('error', (event) => errorListener(event))
        request.open('GET', url)
        request.setRequestHeader('X-Requested-With', 'XMLHttpRequest')
        request.send()
    }

    static getById(id) {
        return LookupControl._registry[id]
    }
}

class TagsControl {

    _tagsField = null

    _tagsInput = null
    _tagsButton = null
    _modal = null
    _tagsContainer = null
    _tagsCheckboxes = null
    _newTagInput = null
    _createNewTagInput = null

    constructor(tagsField) {
        Validate.isInstanceOf(tagsField, HTMLDivElement)

        this._tagsField = tagsField

        this._tagsInput = this._tagsField.querySelector('input[name="tags"]')
        this._tagsButton = this._tagsField.querySelector('button[name="select-tags"]')
        this._modal = this._tagsField.querySelector('div.modal')
        this._tagsContainer = this._tagsField.querySelector('.tags-container')
        this._tagsCheckboxes = [...this._tagsField.querySelectorAll('input[type="checkbox"]')]
        this._newTagInput = this._tagsField.querySelector('input[name="new-tag"]')
        this._createNewTagInput = this._tagsField.querySelector('button[name="create-new-tag"]')

        this._bindListeners()
    }

    _bindListeners() {
        this._tagsButton.addEventListener('click', (event) => {
            event.preventDefault()
            $(this._modal).modal()
        })
        for (const checkbox of this._tagsCheckboxes) {
            this._bindCheckboxListener(checkbox)
        }
        this._createNewTagInput.addEventListener('click', (event) => {
            event.preventDefault()
            this._createTag(this._newTagInput.value)
        })
    }

    _bindCheckboxListener(checkbox) {
        checkbox.addEventListener('click', () => {
            this._changeTag(checkbox.value, checkbox.checked)
        })
    }

    _changeTag(tag, checked) {
        if (checked) {
            this._addTag(tag)
        } else {
            this._removeTag(tag)
        }
    }

    _addTag(tag) {
        const tagsValue = this._tagsInput.value
        const tags = tagsValue ? tagsValue.split(',') : []
        tags.push(tag)
        this._tagsInput.value = tags.join(',')
    }

    _removeTag(tag) {
        this._tagsInput.value = this._tagsInput.value
            .split(',')
            .filter((t) => t !== tag)
            .join(',')
    }

    _createTag(tag) {
        tag = tag.replace(/[^a-z0-9]/gi, '').toLowerCase();
        if (!tag) {
            return;
        }

        let checkbox = this._tagsCheckboxes
            .find((checkbox) => checkbox.value === tag)

        if (!checkbox) {
            checkbox = document.createElement('input')
            checkbox.type = 'checkbox'
            checkbox.value = tag
            checkbox.className = 'form-check-input'
            this._bindCheckboxListener(checkbox)
            this._tagsCheckboxes.push(checkbox)

            const label = document.createElement('label')
            label.className = 'form-check-label'
            label.appendChild(checkbox)
            label.appendChild(document.createTextNode(` ${tag}`))

            const div = document.createElement('div')
            div.className = 'form-group w-25'
            div.appendChild(label)

            this._tagsContainer.appendChild(div)
        }

        if (!checkbox.checked) {
            checkbox.checked = true
            this._addTag(tag)
        }
    }
}

class AsyncFormPartControl {

    static _CONTENT_REGEXP = new RegExp('^\\s*<[^>]+>(.*)</[^>]+>\\s*$', 'is')

    _formPart = null
    _actionElementsSelector = null

    _form = null
    _formPartChangeListener = null

    constructor(formPart, actionElementsSelector, formPartChangeListener) {
        Validate.isInstanceOf(formPart, HTMLElement)
        Validate.isInstanceOf(actionElementsSelector, String)
        Validate.isInstanceOfOrEmpty(formPartChangeListener, Function)

        this._formPart = formPart
        this._actionElementsSelector = actionElementsSelector
        this._formPartChangeListener = formPartChangeListener

        this._form = AsyncFormPartControl._findParentForm(this._formPart)

        const actionElements = document.querySelectorAll(actionElementsSelector)
        this._bindActionElementsListeners(actionElements)
        this._publishFormPartChange()
    }

    static _findParentForm(element) {
        for (let parent = element.parentElement; parent; parent = parent.parentElement) {
            if (parent.tagName.toLowerCase() === 'form') {
                return parent
            }
        }
    }

    _bindActionElementsListeners(actionElements) {
        for (const actionElement of actionElements) {
            actionElement.addEventListener('click', (event) => {
                event.preventDefault()
                this._onActionElementClick(actionElement)
            })
            actionElement.addEventListener('change', (event) => {
                this._onActionElementChange(actionElement)
            })
        }
    }

    _publishFormPartChange() {
        if (this._formPartChangeListener) {
            this._formPartChangeListener(this._formPart)
        }
    }

    _onActionElementClick(actionElement) {
        const name = actionElement.getAttribute('name')
        const value = actionElement.getAttribute('value') || null
        this._fetchFormPart((formData) => formData.set(name, value))
    }

    _onActionElementChange(actionElement) {
        // Because the actionElement can be part of form data, to distinguish
        // its change action the "change" string is prepend to its name
        const name = actionElement.getAttribute('name')
        const actionName = `change${name[0].toUpperCase()}${name.substring(1)}`
        this._fetchFormPart((formData) => formData.set(actionName, null))
    }

    _fetchFormPart(formDataConsumer) {
        const url = new URL(this._form.getAttribute('action'), location.origin)
        const formData = new FormData(this._form)
        formDataConsumer(formData)

        this._post(url, formData, (responseText) => this._replaceFormPartContent(responseText))
    }

    _post(url, formData, loadListener) {
        const request = new XMLHttpRequest()
        const listener = (event) => loadListener(event.target.responseText)
        request.addEventListener('load', listener)
        request.open('POST', url)
        request.setRequestHeader('X-Requested-With', 'XMLHttpRequest')
        request.send(formData)
    }

    _replaceFormPartContent(html) {
        const match = html.match(AsyncFormPartControl._CONTENT_REGEXP)
        this._formPart.innerHTML = match[1] || ''

        const actionElements = this._formPart.querySelectorAll(this._actionElementsSelector)
        this._bindActionElementsListeners(actionElements)

        const lookups = this._formPart.querySelectorAll('div.lookup')
        for (let lookup of lookups) {
            new LookupControl(lookup)
        }
        this._publishFormPartChange()
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
                    const primaryKeyAnchors = td.parentElement.querySelectorAll('td.primary-key > a')
                    const anchors = td.getElementsByTagName('a')
                    if (anchors.length) {
                        window.location = anchors[0].href
                        event.preventDefault()
                    } else if (primaryKeyAnchors.length) {
                        window.location = primaryKeyAnchors[0].href
                        event.preventDefault()
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
