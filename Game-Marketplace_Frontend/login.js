const API_BASE_URL = "https://gamebar-project-production.up.railway.app";

const loginForm = document.querySelector("form");
const loginButton = document.querySelector(".login-btn");
const signupButton = document.querySelector(".signup-btn");

loginForm.addEventListener("submit", function (event) {
    event.preventDefault();
    loginUser();
});

async function loginUser() {
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    if (!email || !password) {
        alert("Please enter both email and password");
        return;
    }

    loginButton.innerText = "Logging in...";
    loginButton.disabled = true;

    try {
        const response = await fetch(`${API_BASE_URL}/users/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ email, password })
        });

        const data = await response.text();
        console.log("Login status:", response.status);
        console.log("Login response:", data);

        if (!response.ok) {
            alert(data || "Login failed");
            return;
        }

        if (data && data.startsWith("ey")) {
            localStorage.setItem("userToken", data);
            localStorage.setItem("userEmail", email);

            alert("Login successful");
            window.location.href = "index.html";
        } else {
            alert(data || "Invalid login response");
        }

    } catch (error) {
        console.error("Login error:", error);
        alert("Login failed. Please check backend or CORS.");
    } finally {
        loginButton.innerText = "Login";
        loginButton.disabled = false;
    }
}

signupButton.addEventListener("click", function () {
    window.location.href = "register.html";
});
