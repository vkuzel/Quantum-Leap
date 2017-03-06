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

    $.get(url, {offset: newOffset}, function(html) {
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
