
function refreshComments(arr) {
    $(".comments").empty();
    arr.forEach(function(row) {
        $(".comments").append(
            $("<div class='comment-row'></div>")
                .append(
                    $("<div class='comment-row-header'></div>")
                        .append($("<span class='comment-username'></span>").text(row.user.username))
                        .append($("<span class='comment-datetime'></span>").text(moment(row.createdOn).format("YYYY-MM-DD HH:mm"))))
                .append($("<div class='comment-view'></div>").append($("<span></span>").text(row.message.body))));
    });
}

function getComments(postPath) {
    if ($(".comments").length > 0) {
        $.ajax({
            url: postPath + "/comments",
            type: "GET",
            success: function (data) {
                // console.log(data);
                refreshComments(data);
            },
            error: function (xhr, ajaxOptions, thrownError) {
                console.error("error: " + xhr.status);
                console.error("error: " + xhr.responseText);
                console.error(thrownError);
            }
        });
    }
}

$(document).ready(function() {
    console.log("Welcome to Spring Board Demo!");

    var path = window.location.pathname;
    getComments(path);

    $("#post-comment-btn").click(function() {
        console.log();
        var commentText = $("#comment-text");
        console.log(commentText.val());
        $.ajax({
            url: path + "/comments/new",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({body: commentText.val()}),
            success: function () {
                console.log("done");
                commentText.val("");
                getComments(path);
            },
            error: function () {
                console.error("error: " + xhr.status);
                console.error("error: " + xhr.responseText);
                console.error(thrownError);
            }
        });
    });

    $(".posts-table tbody tr").click(function() {

    });
});