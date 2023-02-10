var post=function (url, params, callback) {
    $.ajax({
        url: url,
        data: params,
        contentType: "application/json",
        type: "post",
        // 设置头部属性
        beforeSend: function(request) {
            request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function (data) {
            callback(data);
        },
        error: function (data) {

        }
    })
}