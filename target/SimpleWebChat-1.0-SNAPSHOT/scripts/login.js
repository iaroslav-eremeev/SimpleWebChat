$('#btn-go-to-sign-up').click(function () {
        $(location).attr('href', "http://localhost:8080/SimpleWebChat/registration.html");
    }
)

$('#btn-login').click(function () {
        $.ajax({
            url: 'login',
            method: "POST",
            data: {"login": $('#login').val(), "password": $('#password').val()},
            success: [function (result) {
                localStorage.setItem('userId', result.id);
                $(location).attr('href', "http://localhost:8080/SimpleWebChat/index.html");
            }],
            error: [function (result) {
                alert("Wrong login or password!")
            }]
        })
    }
)