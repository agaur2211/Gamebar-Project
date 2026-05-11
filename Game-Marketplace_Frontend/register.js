const registerForm = document.querySelector("form");
const registerButton = document.querySelector(".register-btn");

registerForm.addEventListener("submit", function (event) {
    event.preventDefault();
    registerUser();
});

function registerUser() {
    const name = document.getElementById("name").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();
    const whatsappNumber = document.getElementById("whatsapp").value.trim();

    if (name === "" || email === "" || password === "" || whatsappNumber === "") {
        alert("Please fill all fields");
        return;
    }

    if (password.length < 4) {
        alert("Password must be at least 4 characters");
        return;
    }

    if (whatsappNumber.length < 10) {
        alert("Please enter a valid WhatsApp number");
        return;
    }

    registerButton.innerText = "Registering...";
    registerButton.disabled = true;

    fetch("https://gamebar-backend.onrender.com/users/register", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            name: name,
            email: email,
            password: password,
            whatsappNumber: whatsappNumber
        })
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(errorText => {
                throw new Error(errorText || "Registration failed");
            });
        }

        return response.text();
    })
    .then(data => {
        console.log("Register response:", data);
        alert("Registration successful! Please login now.");
        window.location.href = "login.html";
    })
    .catch(error => {
        console.error("Register error:", error);
        alert(error.message || "Something went wrong during registration");
    })
    .finally(() => {
        registerButton.innerText = "Register";
        registerButton.disabled = false;
    });
}