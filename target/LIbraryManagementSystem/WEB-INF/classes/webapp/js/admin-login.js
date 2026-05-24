document.querySelector('.login-form').addEventListener('submit', function(e) {
    e.preventDefault();

    const username = document.getElementById('id_username').value;
    const password = document.getElementById('id_password').value;

    console.log(username);
    console.log(password);

    const admin = {
        username: username,
        password: password
    };

    fetch('http://localhost:8080/v1/lms/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include', // 🔥 Keep the session cookie
        body: JSON.stringify(admin)
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(errorData => {
                    console.log("Error data from backend:", errorData); // 👈 debug this
                    throw new Error(errorData.message || "Login failed");
                });
            }
            return response.json();
        })
        .then(data => {
            alert("Log in successfully")
        })
        .catch(error => {
            const alertBox = document.querySelector('.alert.alert-danger');
            alertBox.textContent = error.message;
            alertBox.classList.add('show');

            setTimeout(() => {
                alertBox.classList.remove('show');
            }, 3000);
        });
});
