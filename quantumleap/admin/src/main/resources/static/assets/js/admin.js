var urlUtils = {
    getQueryParam: function (url, name) {
        var regExp = new RegExp(name + "=([^&#]+)");
        var match = url.match(regExp);
        return match != null ? match[1] : null;
    },
    replaceQueryParam: function (url, name, value) {
        var regExp = new RegExp(name + '=[&#]+');
        return url.replace(regExp, name + '=' + value);
    }
};

$('.btn-load-more').click(function (event) {

    var offset = urlUtils.getQueryParam(this.href, 'offset');
    var size = urlUtils.getQueryParam(this.href, 'size');

    var currentOffset = urlUtils.getQueryParam(window.location.href, 'offset') || 0;
    var currentSize = urlUtils.getQueryParam(window.location.href, 'size') || (size / 2);

    var newOffset = currentOffset + currentSize;

    var url = urlUtils.replaceQueryParam(this.href, 'offset', newOffset);

    $.get(url, {offset: newOffset}, function (html) {
        var result = $(html);

        var tHead = result.find('thead');
        var tBody = result.find('tbody');
        var tFoot = result.find('tfoot');

        $('thead').replaceWith(tHead);
        $('tbody').append(tBody.html());
        $('tfoot').replaceWith(tFoot);
    });

    return false;
});

$('.lookup :text').each(function () {
    var labelInput = $(this);
    var wrapper = labelInput.parents('.lookup');
    var idInput = wrapper.find(':hidden');
    var lookupButton = wrapper.find('button');

    lookupButton.click(function () {
        $('#personIdModal').modal();
    });

    labelInput.blur(function () {
        setTimeout(function() {
            wrapper.removeClass('open');
        }, 250);
    });

    var labelsRequestTimeout = null;

    labelInput.keyup(function () {
        var lookupLabelsUrl = labelInput.attr('data-lookup-labels-url');
        var filter = labelInput.val();

        if (!filter) {
            return;
        }

        if (labelsRequestTimeout) {
            clearTimeout(labelsRequestTimeout);
            labelsRequestTimeout = null;
        }

        labelsRequestTimeout = setTimeout(function () {
            $.get(lookupLabelsUrl, {filter: filter}, function (html) {
                var result = $(html);

                result.find('a').click(function () {
                    var a = $(this);
                    var id = a.attr('data-id');
                    console.log('a', a, id);
                    if (id) {
                        idInput.val(id);
                        labelInput.val(a.html());
                        console.log(id, a.html());
                    }
                    wrapper.removeClass('open');
                });

                wrapper.find('ul').replaceWith(result);
                wrapper.addClass('open');
            });
        }, 300);
    });
});
