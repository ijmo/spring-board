function getAjaxErrorHandler(msg) {
    return function(jqXHR, textStatus, errorThrown) {
        console.error("error while " + msg + " : " + jqXHR.status);
        console.error("error while " + msg + " : " + jqXHR.responseText);
        console.error(textStatus);
        console.error(errorThrown);
    }
}

function dateFormat(date) {
    return moment(date).format("YYYY-MM-DD HH:mm");
}

function refreshComments(arr) {
    var comments = $("#comments");
    comments.empty();
    arr.forEach(function(row) {
        comments.append(
            $("<div class='p-1 mb-2 comment-item'></div>")
                .append(
                    $("<div class='d-flex flex-row text-light m-1 comment-header align-items-center'></div>")
                        .append($("<span class='mr-auto pl-lg-3'></span>").text(row.user.username))
                        .append($("<span class='date align-middle pr-lg-3'></span>").text(dateFormat(row.createdOn))))
                .append($("<div class='mx-1 text-dark'></div>").append($("<span></span>").text(row.message.body))));
    });
}

function getComments(postPath) {
    if ($("#comments").length > 0) {
        $.ajax({
            url: postPath + "/comments",
            type: "GET",
            success: function (data, textStatus, jqXHR) {
                // console.log(data);
                // console.log(jqXHR);
                refreshComments(data);
            },
            error: getAjaxErrorHandler("getComments()")
        });
    }
}

function csrfHeader() {
    var token = $("meta[name='_csrf']").attr("content");
    var headerName = $("meta[name='_csrf_header']").attr("content");
    var headers = {};
    headers[headerName] = token;
    return headers;
}

function showHistory(what, id) {
    var url = '';

    if (what === 'post') {
        url = window.location.pathname + "/history";
    } else {
        url = '/comments/' + id + '/history';
    }

    $.ajax({
        url: url,
        type: "get",
        contentType: "application/json",
        headers: csrfHeader(),
        success: function (data, textStatus, jqXHR) {
            // console.log(data);
            // console.log(jqXHR);
            $("#historyModal .modal-body").empty();
            data.forEach(function(row, i) {
                console.log(row);
                $("<span></span>").text(row.body);
                $("#historyModal .modal-body")
                    .prepend($("<div></div>")
                        .append(i < data.length - 1 ? $("<div class='border-bottom pb-3 mb-3'></div>") : null)
                        .append($("<div class='d-flex flex-row'></div>")
                            .append($("<span></span>").text("[" + row.revision + "] "))
                            .append($("<span class='ml-2 mr-auto'></span>").text(row.title))
                            .append($("<span class='date'></span>").text(dateFormat(row.createdOn)))
                        )
                        .append($("<div></div>")
                            .append($("<span></span>").text(row.body))
                        )
                    );
            });
        },
        error: getAjaxErrorHandler("showHistory()")
    });
}

$(document).ready(function() {
    console.log("Welcome to Spring Board Demo!!");

    var path = window.location.pathname;
    getComments(path);

    ///////////////////////////////////////////////////////
    // Event handlers
    ///////////////////////////////////////////////////////

    // post list table row
    $("#posts tbody tr").click(function() {
        window.location = $(this).data("href");
    });

    // textarea for new comment in postDetails.html
    $("#form-comment textarea#body").focus(function() {
        var authenticated = $("#form-comment").data("authenticated");
        if (authenticated === false) {
            window.location = "/login";
        }
    });

    // button for new comment in postDetails.html
    $("#form-comment").submit(function() {
        var messageBody = $(this).find("#body");
        $.ajax({
            url: path + "/comments/new",
            type: "post",
            contentType: "application/json",
            headers: csrfHeader(),
            data: JSON.stringify({body: messageBody.val()}),
            success: function (data, textStatus, jqXHR) {
                // console.log(data);
                // console.log(jqXHR);
                messageBody.val("");
                getComments(path);
            },
            error: getAjaxErrorHandler("#form-comment.submit()")
        });
        return false;
    });
});