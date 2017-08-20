// This function was extracted from sb-admin-2.js because that file also
// contains initialization of metisMenu  javascript that is not used.
$(window).bind("load resize", function () {
    var topOffset = 50;
    var width = (this.window.innerWidth > 0) ? this.window.innerWidth : this.screen.width;
    if (width < 768) {
        $('div.navbar-collapse').addClass('collapse');
        topOffset = 100; // 2-row-menu
    } else {
        $('div.navbar-collapse').removeClass('collapse');
    }

    var height = ((this.window.innerHeight > 0) ? this.window.innerHeight : this.screen.height) - 1;
    height = height - topOffset;
    if (height < 1) height = 1;
    if (height > topOffset) {
        $("#page-wrapper").css("min-height", (height) + "px");
    }
});

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

function TableControl(table, tBodyListenersBinder) {
    var $table = $(table);

    var tableControl = {
        $table: $table,

        $tHead: $table.find('thead'),
        $tBody: $table.find('tbody'),
        $tFoot: $table.find('tfoot')
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

        var url = urlUtils.removeQueryParams(this.href, 'size', 'offset');

        $.get(url, {offset: offset}, tableControl.appendContent);

        return false;
    };

    tableControl.sort = function () {
        var size = tableControl.$tBody.find('tr').length;

        var url = urlUtils.removeQueryParams(this.href, 'size', 'offset');

        $.get(url, {size: size}, tableControl.replaceContent);

        return false;
    };

    tableControl.bindListeners();
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
            lookupControl.$idInput.val(id);
            lookupControl.$labelInput.val($a.html());
        }
        lookupControl.$lookupField.removeClass('open');
    };

    dropDownControl.replaceDropdownContent = function (html) {
        var $dropDownReplacement = $(html).next('ul.dropdown-menu');

        $dropDownReplacement.find('a[data-id]').click(function () {
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
            lookupControl.$idInput.val(id);
            $.get(modalControl.lookupLabelUrl, {id: id}, function (label) {
                lookupControl.$labelInput.val(label);
            }).fail(function () {
                lookupControl.$labelInput.val(id);
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
    new LookupControl(lookupField);
});
