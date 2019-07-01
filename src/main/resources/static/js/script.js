
function getAjaxErrorHandler(msg) {
    return function(jqXHR, textStatus, errorThrown) {
        console.error("error while " + msg + " : " + jqXHR.status);
        console.error("error while " + msg + " : " + jqXHR.responseText);
        console.error(textStatus);
        console.error(errorThrown);
    }
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
                        .append($("<span class='date align-middle pr-lg-3'></span>").text(moment(row.createdOn).format("YYYY-MM-DD HH:mm"))))
                .append($("<div class='mx-1 text-dark'></div>").append($("<span></span>").text(row.message.body))));
    });
}

function getComments(postPath) {
    if ($("#comments").length > 0) {
        $.ajax({
            url: postPath + "/comments",
            type: "GET",
            success: function (data, textStatus, jqXHR) {
                // console.debug(data);
                // console.debug(jqXHR);
                refreshComments(data);
            },
            error: getAjaxErrorHandler("getComments()")
        });
    }
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

    // button for new comment in postDetails.html
    $("#form-comment").submit(function() {
        var messageBody = $("#form-comment #body");
        $.ajax({
            url: path + "/comments/new",
            type: "post",
            contentType: "application/json",
            data: JSON.stringify({body: messageBody.val()}),
            success: function (data, textStatus, jqXHR) {
                // console.debug(data);
                // console.debug(jqXHR);
                messageBody.val("");
                getComments(path);
            },
            error: getAjaxErrorHandler("#form-comment.submit")
        });
        return false;
    });
});