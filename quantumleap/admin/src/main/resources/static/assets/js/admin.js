var urlUtils = {
    getQueryParam: function (url, name) {
        var regExp = new RegExp(name + "=([^&#]+)");
        var match = url.match(regExp);
        return match != null ? match[1] : null;
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

// TODO Loaders, spinners?

// TODO Add a support of multiple tables on a single page! Tables (and query string parameters) will be prefixed by a unique identifier (table name?).

function TableControl(table) {
    var $table = $(table);

    var instance = { // TODO tableControl
        $table: $table,

        $tHead: $table.find('thead'),
        $tBody: $table.find('tbody'),
        $tFoot: $table.find('tfoot')
    };

    instance.attachListeners = function () {

        var $loadMoreButton = instance.$tFoot.find('.btn-load-more');
        $loadMoreButton.click(instance.fetchMore);

        var $sortButtons = instance.$tHead.find('a');
        $sortButtons.click(instance.sort);
    };

    instance.appendContent = function (table) {
        var $table = $(table);

        var $tHead = $table.find('thead');
        var $tBody = $table.find('tbody');
        var $tFoot = $table.find('tfoot');

        instance.$tHead.replaceWith($tHead);
        instance.$tHead = $tHead;
        instance.$tBody.append($tBody.html());
        instance.$tFoot.replaceWith($tFoot);
        instance.$tFoot = $tFoot;

        instance.attachListeners(instance.$tHead, instance.$tFoot);
    };

    instance.replaceContent = function (table) {
        var $table = $(table);

        var $tHead = $table.find('thead');
        var $tBody = $table.find('tbody');
        var $tFoot = $table.find('tfoot');

        instance.$tHead.replaceWith($tHead);
        instance.$tHead = $tHead;
        instance.$tBody.replaceWith($tBody);
        instance.$tBody = $tBody;
        instance.$tFoot.replaceWith($tFoot);
        instance.$tFoot = $tFoot;

        instance.attachListeners(instance.$tHead, instance.$tFoot);
    };

    instance.fetchMore = function () {
        var offset = instance.$tBody.find('tr').length;

        var url = urlUtils.removeQueryParams(this.href, 'size', 'offset');

        $.get(url, {offset: offset}, instance.appendContent);

        return false;
    };

    instance.sort = function () {
        var size = instance.$tBody.find('tr').length;

        var url = urlUtils.removeQueryParams(this.href, 'size', 'offset');

        $.get(url, {size: size}, instance.replaceContent);

        return false;
    };

    instance.attachListeners(instance.$tHead, instance.$tFoot);
}

$('table.dataTable').each(function (i, table) {
    new TableControl(table);
});

function LookupControl(lookupField) {
    var $lookupField = $(lookupField);

    var lookupControl = {
        $lookupField: $lookupField,

        $idInput: $lookupField.find(':hidden'),
        $labelInput: $lookupField.find(':text')
    };

    var dropDownControl = {
        labelsUrl: lookupControl.$labelInput.attr('data-lookup-labels-url'),
        $dropDown: $lookupField.find('ul.dropdown-menu'),

        labelsRequestTimeout: null
    };

    dropDownControl.attachListeners = function () {
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
            lookupControl.$idInput.val(id);
            lookupControl.$labelInput.val($a.html());
        }
        lookupControl.$lookupField.removeClass('open');
    };

    dropDownControl.replaceDropdownContent = function (html) {
        var $dropDownReplacement = $(html).next('ul.dropdown-menu');

        $dropDownReplacement.find('a').click(function () {
            dropDownControl.selectDropdownItem(this);
        });

        dropDownControl.$dropDown.replaceWith($dropDownReplacement);
        dropDownControl.$dropDown = $dropDownReplacement;
        lookupControl.$lookupField.addClass('open');
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
            lookupControl.$lookupField.removeClass('open');
        }, 300);
    };

    dropDownControl.attachListeners();

    var $lookupButton = $lookupField.find('button[data-lookup-list-url]');
    var $modal = $lookupField.find('div.modal');

    var modalControl = {
        $lookupButton: $lookupField.find('button[data-lookup-list-url]'),
        lookupListUrl: $lookupButton.attr('data-lookup-list-url'),
        $modal: $modal,

        $modalBody: $modal.find('.modal-body')
    };

    modalControl.attachListeners = function () {
        modalControl.$lookupButton.click(modalControl.fetchList);
    };

    modalControl.replaceModalContent = function (html) {
        var $table = $(html).next('table');

        new TableControl($table);
        modalControl.$modalBody.empty().append($table);

        modalControl.$modal.modal();
    };

    modalControl.fetchList = function () {
        $.get(modalControl.lookupListUrl, {}, modalControl.replaceModalContent);
    };

    modalControl.attachListeners();
}

$('div.lookup').each(function (i, lookupField) {
    new LookupControl(lookupField);
});


var lookupControlFactory = {
    createLookupControl: function (lookupField) {
        var $lookupField = $(lookupField);

        var $idInput = $lookupField.find(':hidden');
        var $labelInput = $lookupField.find(':text');

        // drop down

        var labelsUrl = $labelInput.attr('data-lookup-labels-url');
        var $dropDown = $lookupField.find('ul.dropdown-menu');

        var labelsRequestTimeout = null;

        var cancelRequestInProgress = function () {
            if (labelsRequestTimeout) {
                clearTimeout(labelsRequestTimeout);
                labelsRequestTimeout = null;
            }
        };

        var selectDropdownItem = function (a) {
            var $a = $(a);

            var id = a.attr('data-id');
            if (id) {
                $idInput.val(id);
                $labelInput.val(a.html());
            }
            $lookupField.removeClass('open');
        };

        var replaceDropdownContent = function (html) {
            var $dropDownReplacement = $(html).next('ul.dropdown-menu');

            $dropDownReplacement.find('a').click(function () {
                selectDropdownItem(this);
            });

            $dropDown.replaceWith($dropDownReplacement);
            $dropDown = $dropDownReplacement;
            $lookupField.addClass('open');
        };

        var fetchLabels = function () {
            cancelRequestInProgress();

            var filter = $labelInput.val();

            if (!filter) {
                return;
            }

            labelsRequestTimeout = setTimeout(function () {
                $.get(labelsUrl, {filter: filter}, replaceDropdownContent);
            }, 300);
        };

        $labelInput.keyup(fetchLabels);

        var fieldLostFocus = function () {
            cancelRequestInProgress();

            setTimeout(function () {
                $lookupField.removeClass('open');
            }, 300);
        };

        $labelInput.blur(fieldLostFocus);

        // modal

        var $lookupButton = $lookupField.find('button[data-lookup-list-url]');
        var lookupListUrl = $lookupButton.attr('data-lookup-list-url');
        var $modal = $lookupField.find('div.modal');

        var $modalBody = $modal.find('.modal-body');

        var replaceModalContent = function (html) {
            var $table = $(html).next('table');

            tableControlFactory.createTableControl($table);
            $modalBody.empty().append($table);

            $modal.modal();
        };

        var fetchList = function () {
            $.get(lookupListUrl, {}, replaceModalContent);
        };

        $lookupButton.click(fetchList);
    }
};
